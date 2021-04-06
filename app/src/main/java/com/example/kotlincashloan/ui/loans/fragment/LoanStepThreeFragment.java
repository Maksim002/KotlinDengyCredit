package com.example.kotlincashloan.ui.loans.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kotlincashloan.R;
import com.example.kotlincashloan.adapter.loans.StepClickListener;
import com.example.kotlincashloan.service.model.login.ImageStringModel;
import com.example.kotlincashloan.service.model.login.SaveLoanResultModel;
import com.example.kotlincashloan.ui.loans.GetLoanActivity;
import com.example.kotlincashloan.ui.loans.LoansViewModel;
import com.example.kotlincashloan.ui.loans.fragment.dialogue.StepBottomFragment;
import com.example.kotlincashloan.ui.registration.login.HomeActivity;
import com.example.kotlincashloan.utils.ObservedInternet;
import com.example.kotlinscreenscanner.service.model.CommonResponseReject;
import com.regula.documentreader.api.DocumentReader;
import com.regula.documentreader.api.completions.IDocumentReaderCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderInitCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderPrepareCompletion;
import com.regula.documentreader.api.enums.DocReaderAction;
import com.regula.documentreader.api.enums.eGraphicFieldType;
import com.regula.documentreader.api.enums.eRFID_Password_Type;
import com.regula.documentreader.api.enums.eRPRM_ResultType;
import com.regula.documentreader.api.enums.eVisualFieldType;
import com.regula.documentreader.api.errors.DocumentReaderException;
import com.regula.documentreader.api.results.DocumentReaderResults;
import com.regula.documentreader.api.results.DocumentReaderScenario;
import com.regula.documentreader.api.results.DocumentReaderTextField;
import com.timelysoft.tsjdomcom.service.AppPreferences;
import com.timelysoft.tsjdomcom.service.ResultStatus;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static android.graphics.BitmapFactory.decodeStream;
import static com.regula.documentreader.api.enums.LCID.KYRGYZ_CYRILICK;
import static com.regula.documentreader.api.enums.LCID.RUSSIAN;

public class LoanStepThreeFragment extends Fragment implements StepClickListener {
    private HashMap<String, ImageStringModel> list = new HashMap();
    private HashMap<String, String> map = new HashMap<>();
    private LoansViewModel viewModel = new LoansViewModel();
    private Bitmap documentImageTwo;
    private Bitmap portrait;
    private Bitmap documentImage;
    private TextView theeIncorrect;

    private static final int REQUEST_BROWSE_PICTURE = 11;

    private Button showScanner;
    private Thread thread;

    private ImageView portraitIv, threeCrossBack;
    private ImageView docImageIv;

    private LinearLayout layout_status, status_no_questionnaire, status_technical_work, status_not_found;
    private Button no_connection_repeat, technical_work, not_found;

    private boolean doRfid = false;
    private AlertDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_step_three, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppPreferences.INSTANCE.init(requireContext());

        showScanner = view.findViewById(R.id.thee_active_status);
        portraitIv = view.findViewById(R.id.portraitIv);
        docImageIv = view.findViewById(R.id.documentImageIv);

        layout_status = view.findViewById(R.id.layout_status);
        status_technical_work = view.findViewById(R.id.status_technical_work);
        status_no_questionnaire = view.findViewById(R.id.status_no_questionnaire);
        status_not_found = view.findViewById(R.id.status_not_found);
        theeIncorrect = view.findViewById(R.id.thee_incorrect);
        threeCrossBack = view.findViewById(R.id.three_cross_back);

        no_connection_repeat = view.findViewById(R.id.no_connection_repeat);
        technical_work = view.findViewById(R.id.technical_work);
        not_found = view.findViewById(R.id.not_found);

        initInternet();
        initClick();
        getLists();
    }

    //Получает данные на редактирование заёма
    private void getLists() {
        if (AppPreferences.INSTANCE.getStatus() == true) {
            threeCrossBack.setVisibility(View.GONE);
        }
    }

    private void initClick() {
        no_connection_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInternet();
            }
        });

        technical_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInternet();
            }
        });

        not_found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInternet();
            }
        });

        threeCrossBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GetLoanActivity) getActivity()).get_loan_view_pagers.setCurrentItem(1);
            }
        });
    }

    private void initResult() {
        GetLoanActivity.alert.show();
        map.put("login", AppPreferences.INSTANCE.getLogin());
        map.put("token", AppPreferences.INSTANCE.getToken());

        if (list.containsKey("passport_photo")) {
            map.put("passport_photo", list.get("passport_photo").getString());
        } else {
            map.put("passport_photo", "");
        }
        if (list.containsKey("passport_img_1")) {
            map.put("passport_img_1", list.get("passport_img_1").getString());
        } else {
            map.put("passport_img_1", "");
        }
        if (list.containsKey("passport_img_2")) {
            map.put("passport_img_2", list.get("passport_img_2").getString());
        } else {
            map.put("passport_img_2", "");
        }
        map.put("id", AppPreferences.INSTANCE.getApplicationId());
        map.put("step", "2");


        viewModel.saveLoans(map).observe(getViewLifecycleOwner(), new Observer<ResultStatus<CommonResponseReject<SaveLoanResultModel>>>() {
            @Override
            public void onChanged(ResultStatus<CommonResponseReject<SaveLoanResultModel>> result) {
                switch (result.getStatus()) {
                    case SUCCESS: {
                        if (result.getData().getResult() != null) {
                            layout_status.setVisibility(View.VISIBLE);
                            theeIncorrect.setVisibility(View.GONE);
                            status_technical_work.setVisibility(View.GONE);
                            status_no_questionnaire.setVisibility(View.GONE);
                            status_not_found.setVisibility(View.GONE);
                            docImageIv.setImageBitmap(documentImage);
                            portraitIv.setImageBitmap(portrait);
                            if (AppPreferences.INSTANCE.getStatus() == true){
                                getActivity().finish();
                            }else {
                                ((GetLoanActivity) getActivity()).get_loan_view_pagers.setCurrentItem(3);
                            }
                        } else if (result.getData().getError() != null) {
                            if (result.getData().getError().getCode() == 400) {
                                theeIncorrect.setText("Отсканируйте документ повторно");
                                theeIncorrect.setVisibility(View.VISIBLE);
                            } else if (result.getData().getError().getCode() == 500) {
                                status_technical_work.setVisibility(View.VISIBLE);
                                status_no_questionnaire.setVisibility(View.GONE);
                                layout_status.setVisibility(View.GONE);
                                status_not_found.setVisibility(View.GONE);
                                theeIncorrect.setVisibility(View.GONE);
                            } else if (result.getData().getError().getCode() == 401) {
                                initAuthorized();
                            } else if (result.getData().getError().getCode() == 409) {
                                Toast.makeText(requireContext(), "Анкета уже создана", Toast.LENGTH_LONG).show();
                                theeIncorrect.setVisibility(View.GONE);
                            } else if (result.getData().getError().getCode() == 404) {
                                status_not_found.setVisibility(View.VISIBLE);
                                status_technical_work.setVisibility(View.GONE);
                                status_no_questionnaire.setVisibility(View.GONE);
                                layout_status.setVisibility(View.GONE);
                                theeIncorrect.setVisibility(View.GONE);
                            }
                        } else if (result.getData().getReject() != null) {
                            initBottomSheet(result.getData().getReject().getMessage());
                            layout_status.setVisibility(View.VISIBLE);
                            status_technical_work.setVisibility(View.GONE);
                            status_no_questionnaire.setVisibility(View.GONE);
                            status_not_found.setVisibility(View.GONE);
                            theeIncorrect.setVisibility(View.GONE);
                        }
                    }
                    case NETWORK:
                    case ERROR: {
                        if (result.getMsg() != null) {
                            errorSaveLoan(result.getMsg());
                        } else if (result.getData().getCode() != 200) {
                            errorSaveLoan(String.valueOf(result.getData().getCode()));
                        }
                    }
                }
                GetLoanActivity.alert.hide();
            }
        });
    }

    private void errorSaveLoan(String error) {
        if (error != null) {
            if (error.equals("601")) {
                status_no_questionnaire.setVisibility(View.VISIBLE);
                status_technical_work.setVisibility(View.GONE);
                layout_status.setVisibility(View.GONE);
                status_not_found.setVisibility(View.GONE);
                theeIncorrect.setVisibility(View.GONE);
            } else if (error.equals("401")) {
                initAuthorized();
            } else if (error.equals("500")) {
                status_technical_work.setVisibility(View.VISIBLE);
                status_no_questionnaire.setVisibility(View.GONE);
                layout_status.setVisibility(View.GONE);
                status_not_found.setVisibility(View.GONE);
                theeIncorrect.setVisibility(View.GONE);
            } else if (error.equals("409")) {
                Toast.makeText(requireContext(), "Анкета уже создана", Toast.LENGTH_LONG).show();
                theeIncorrect.setVisibility(View.GONE);
            } else if (error.equals("404")) {
                status_not_found.setVisibility(View.VISIBLE);
                status_technical_work.setVisibility(View.GONE);
                status_no_questionnaire.setVisibility(View.GONE);
                layout_status.setVisibility(View.GONE);
                theeIncorrect.setVisibility(View.GONE);
            } else if (error.equals("400")) {
                theeIncorrect.setText("Отсканируйте документ повторно");
                theeIncorrect.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initBottomSheet(String message) {
        StepBottomFragment stepBottomFragment = new StepBottomFragment(this, message);
        stepBottomFragment.setCancelable(false);
        stepBottomFragment.show(requireActivity().getSupportFragmentManager(), stepBottomFragment.getTag());
    }

    private void initAuthorized() {
        Intent intent = new Intent(requireActivity(), HomeActivity.class);
        requireActivity().startActivity(intent);
    }

    private void initInternet() {
        new ObservedInternet().observedInternet(requireContext());
        if (!AppPreferences.INSTANCE.getObservedInternet()) {
            status_no_questionnaire.setVisibility(View.VISIBLE);
            status_technical_work.setVisibility(View.GONE);
        } else {
            layout_status.setVisibility(View.VISIBLE);
            viewModel.getErrorSaveLoan().setValue(null);
            status_no_questionnaire.setVisibility(View.GONE);
            status_technical_work.setVisibility(View.GONE);
            Runnable runnable = new Runnable() {
                public void run() {
                    initDocumentReader();
                    thread.interrupt();
                }
            };
            thread = new Thread(runnable);
            thread.start();
        }
    }

    private void initDocumentReader() {
        if (!DocumentReader.Instance().getDocumentReaderIsReady()) {
//            final AlertDialog initDialog = showDialog("Инициализация");
            showScanner.setText("Ожидайте, идет загрузка...");
            showScanner.setClickable(false);


            //Reading the license from raw resource file
            try {
                InputStream licInput = getResources().openRawResource(R.raw.regula);
                int available = licInput.available();
                final byte[] license = new byte[available];
                //noinspection ResultOfMethodCallIgnored
                licInput.read(license);

                //preparing database files, it will be downloaded from network only one time and stored on user device
                DocumentReader.Instance().prepareDatabase(requireContext(), "Full", new IDocumentReaderPrepareCompletion() {
                    @Override
                    public void onPrepareProgressChanged(int progress) {
//                        initDialog.setTitle("Загрузка базы данных: " + progress + "%");
                        showScanner.setText("Загрузка базы данных: " + progress + "%");
                    }

                    @Override
                    public void onPrepareCompleted(boolean b, DocumentReaderException e) {
                        //Initializing the reader
                        DocumentReader.Instance().initializeReader(getActivity(), license, new IDocumentReaderInitCompletion() {
                            @Override
                            public void onInitCompleted(boolean b, DocumentReaderException e) {

                                showScanner.setText("Сканировать документ");
                                showScanner.setClickable(true);
                                layout_status.setVisibility(View.VISIBLE);
                                status_no_questionnaire.setVisibility(View.GONE);

                                DocumentReader.Instance().customization().edit().setShowHelpAnimation(false).apply();
                                DocumentReader.Instance().functionality().edit().setShowSkipNextPageButton(false).apply();

                                //initialization successful
                                if (b) {
                                    showScanner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            clearResults();

                                            //starting video processing
                                            DocumentReader.Instance().showScanner(requireContext(), completion);
                                            DocumentReader.Instance().processParams().multipageProcessing = true;

                                        }
                                    });

                                    //getting current processing scenario and loading available scenarios to ListView
                                    String currentScenario = DocumentReader.Instance().processParams().scenario;
                                    ArrayList<String> scenarios = new ArrayList<>();
                                    for (DocumentReaderScenario scenario : DocumentReader.Instance().availableScenarios) {
                                        scenarios.add(scenario.name);
                                    }

                                    //setting default scenario
                                    if (currentScenario == null || currentScenario.isEmpty()) {
                                        currentScenario = scenarios.get(3);
                                        DocumentReader.Instance().processParams().scenario = currentScenario;
                                    }
                                }
                                //Initialization was not successful
                                else {
                                    try {
                                        Toast.makeText(requireContext(), "Init failed:" + e, Toast.LENGTH_LONG).show();
                                    } catch (Exception el) {
                                        el.printStackTrace();
                                    }
                                }
                            }

                        });
                    }
                });

                licInput.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            layout_status.setVisibility(View.VISIBLE);
            status_no_questionnaire.setVisibility(View.GONE);

            showScanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    theeIncorrect.setVisibility(View.GONE);
                    clearResults();

                    //starting video processing
                    DocumentReader.Instance().showScanner(requireContext(), completion);
                    DocumentReader.Instance().processParams().multipageProcessing = true;
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Image browsing intent processed successfully
            if (requestCode == REQUEST_BROWSE_PICTURE) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Bitmap bmp = getBitmap(selectedImage, 1920, 1080);

                    showScanner.setText("Processing image");
//                    loadingDialog = showDialog("Processing image");

                    DocumentReader.Instance().recognizeImage(bmp, completion);
                }
            }
        }
    }

    //DocumentReader processing callback
    private IDocumentReaderCompletion completion = new IDocumentReaderCompletion() {
        @Override
        public void onCompleted(int i, DocumentReaderResults documentReaderResults, DocumentReaderException e) {
//processing is finished, all results are ready
            if (i == DocReaderAction.COMPLETE) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                //Checking, if nfc chip reading should be performed
                if (doRfid && documentReaderResults != null && documentReaderResults.chipPage != 0) {
                    //setting the chip's access key - mrz on car access number
                    String accessKey = null;
                    if ((accessKey = documentReaderResults.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)) != null && !accessKey.isEmpty()) {
                        accessKey = documentReaderResults.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                                .replace("^", "").replace("\n", "");
                        DocumentReader.Instance().rfidScenario().setMrz(accessKey);
                        DocumentReader.Instance().rfidScenario().setPacePasswordType(eRFID_Password_Type.PPT_MRZ);
                    } else if ((accessKey = documentReaderResults.getTextFieldValueByType(eVisualFieldType.FT_CARD_ACCESS_NUMBER)) != null && !accessKey.isEmpty()) {
                        DocumentReader.Instance().rfidScenario().setPassword(accessKey);
                        DocumentReader.Instance().rfidScenario().setPacePasswordType(eRFID_Password_Type.PPT_CAN);
                    }

                    //starting chip reading
                    DocumentReader.Instance().startRFIDReader(requireContext(), new IDocumentReaderCompletion() {
                        @Override
                        public void onCompleted(int i, DocumentReaderResults documentReaderResults, DocumentReaderException e) {
                            if (i == DocReaderAction.COMPLETE || i == DocReaderAction.CANCEL) {
                                displayResults(documentReaderResults);
                            }
                        }
                    });
                } else {
                    displayResults(documentReaderResults);
                }
            } else {
                //something happened before all results were ready
                if (i == DocReaderAction.CANCEL) {
                    Toast.makeText(requireContext(), "Scanning was cancelled", Toast.LENGTH_LONG).show();
                } else if (i == DocReaderAction.ERROR) {
                    Toast.makeText(requireContext(), "Error:" + e, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    //show received results on the UI
    private void displayResults(DocumentReaderResults results) {
        if (results != null) {
            try {
                //Фамилия
                String surnameS = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME);
                if (surnameS != null) {
                    map.put("passport_last_name_l", surnameS);
                } else {
                    map.put("passport_last_name_l", "");
                }

                //Имя
                String namesS = results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES);
                if (namesS != null) {
                    map.put("passport_first_name_l", namesS);
                } else {
                    map.put("passport_first_name_l", "");
                }

                //Фамилия (Ныц)
                String surnameNationalS = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME, RUSSIAN);
                if (surnameNationalS != null) {
                    map.put("passport_last_name", surnameNationalS);
                } else {
                    map.put("passport_last_name", "");
                }

                //Имя (Нац)
                String namesNationalS = results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES, RUSSIAN);
                if (namesNationalS != null) {
                    map.put("passport_first_name", namesNationalS);
                } else {
                    map.put("passport_first_name", "");
                }

                //Отчество (Нац)
                String nameNationalityS = results.getTextFieldValueByType(eVisualFieldType.FT_FATHERS_NAME, RUSSIAN);
                if (nameNationalityS != null) {
                    map.put("passport_second_name_l", "");
                    map.put("passport_second_name", nameNationalityS);
                } else {
                    map.put("passport_second_name_l", "");
                    map.put("passport_second_name", "");
                }

                //Дата рождения
                String dataS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH);
                SimpleDateFormat sdf = null;
                if (dataS != null) {
                    if (dataS.length() == 8) {
                        sdf = new SimpleDateFormat("dd.MM.yy");
                    } else {
                        sdf = new SimpleDateFormat("dd.MM.yyy");
                    }
                    Date d1 = sdf.parse(dataS);
                    sdf.applyPattern("yyyy-MM-dd");
                    map.put("passport_u_date", sdf.format(d1));
                } else {
                    map.put("passport_u_date", "");
                }

                //пол
                String sexS = results.getTextFieldValueByType(eVisualFieldType.FT_SEX);
                if (sexS != null) {
                    map.put("passport_gender", sexS);
                } else {
                    map.put("passport_gender", "");
                }

                //Код государтсва выдочи
                String placeCodeS = results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_CODE);
                if (placeCodeS != null) {
                    map.put("passport_nationality", placeCodeS);
                    AppPreferences.INSTANCE.setNationality(placeCodeS);
                } else {
                    map.put("passport_nationality", "");
                }

                //номер документа
                String documentNumberS = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER);
                if (documentNumberS != null) {
                    map.put("passport_reg_number", documentNumberS);
                } else {
                    map.put("passport_reg_number", "");
                }

                //Личный номер
                String personalNumberS = results.getTextFieldValueByType(eVisualFieldType.FT_PERSONAL_NUMBER);
                if (personalNumberS != null) {
                    map.put("passport_inn", personalNumberS);
                } else {
                    map.put("passport_inn", "");
                }

                // оставшися срок
                String remainderTermS = results.getTextFieldValueByType(eVisualFieldType.FT_REMAINDER_TERM);
                if (remainderTermS != null) {
                    map.put("passport_valid", remainderTermS);
                } else {
                    map.put("passport_valid", "");
                }

                // Дата окончания действия
                String dateExpiryS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_EXPIRY);
                if (dateExpiryS != null) {
                    if (dataS.length() == 8) {
                        sdf = new SimpleDateFormat("dd.MM.yy");
                    } else {
                        sdf = new SimpleDateFormat("dd.MM.yyy");
                    }
                    Date d1 = sdf.parse(dateExpiryS);
                    sdf.applyPattern("yyyy-MM-dd");
                    map.put("passport_expired", sdf.format(d1));
                } else {
                    map.put("passport_expired", "");
                }

                // Орган выдачи
                String authorityS = results.getTextFieldValueByType(eVisualFieldType.FT_AUTHORITY);
                if (authorityS != null) {
                    map.put("passport_authority", authorityS);
                } else {
                    map.put("passport_authority", "");
                }

                // Код типа документа
                String documentClassCodeS = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_CLASS_CODE);
                if (documentClassCodeS != null) {
                    map.put("passport_type", documentClassCodeS);
                } else {
                    map.put("passport_type", "");
                }

                // Дата выпуска
                String date_of_IssueS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_ISSUE);
                if (date_of_IssueS != null) {
                    if (dataS.length() == 8) {
                        sdf = new SimpleDateFormat("dd.MM.yy");
                    } else {
                        sdf = new SimpleDateFormat("dd.MM.yyy");
                    }
                    Date d1 = sdf.parse(date_of_IssueS);
                    sdf.applyPattern("yyyy-MM-dd");
                    map.put("passport_issue", sdf.format(d1));
                } else {
                    map.put("passport_issue", "");
                }

                // Код национальности (Нац)
                String placeOfIssueS = results.getTextFieldValueByType(eVisualFieldType.FT_PLACE_OF_ISSUE, KYRGYZ_CYRILICK);
                if (placeOfIssueS != null) {
                    if (authorityS == null) {
                        map.put("passport_authority", placeOfIssueS);
                    } else {
                        map.put("passport_authority", "");
                    }
                }

                // through all text fields
                if (results.textResult != null && results.textResult.fields != null) {
                    for (DocumentReaderTextField textField : results.textResult.fields) {
                        String value = results.getTextFieldValueByType(textField.fieldType, textField.lcid);
                        Log.d("MainActivity", value + "\n");
                    }
                }

                portrait = results.getGraphicFieldImageByType(eGraphicFieldType.GF_PORTRAIT);
                if (portrait != null) {
                    gotImageString(portrait, "passport_photo");
                }

                documentImage = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE);
                if (documentImage != null) {
                    double aspectRatio = (double) documentImage.getWidth() / (double) documentImage.getHeight();
                    documentImage = Bitmap.createScaledBitmap(documentImage, (int) (480 * aspectRatio), 480, false);
                    gotImageString(documentImage, "passport_img_1");
                }

                documentImageTwo = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE, eRPRM_ResultType.RPRM_RESULT_TYPE_RAW_IMAGE, 1);
                if (documentImageTwo != null) {
                    double aspectRatio = (double) documentImageTwo.getWidth() / (double) documentImageTwo.getHeight();
                    documentImageTwo = Bitmap.createScaledBitmap(documentImageTwo, (int) (480 * aspectRatio), 480, false);
                    gotImageString(documentImageTwo, "passport_img_2");
                }

                initResult();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            initResult();
        }
    }

    //encode image to base64 string
    private void gotImageString(Bitmap bitmap, String key) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        list.put(key, new ImageStringModel(imageString));
    }

    private void clearResults() {
        portraitIv.setImageResource(R.drawable.portrait);
        docImageIv.setImageResource(R.drawable.id);
    }

    // loads bitmap from uri
    private Bitmap getBitmap(Uri selectedImage, int targetWidth, int targetHeight) {
        ContentResolver resolver = getActivity().getContentResolver();
        InputStream is = null;
        try {
            is = resolver.openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, options);

        //Re-reading the input stream to move it's pointer to start
        try {
            is = resolver.openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return decodeStream(is, null, options);
    }

    // see https://developer.android.com/topic/performance/graphics/load-bitmap.html
    private int calculateInSampleSize(BitmapFactory.Options options, int bitmapWidth, int bitmapHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > bitmapHeight || width > bitmapWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > bitmapHeight
                    && (halfWidth / inSampleSize) > bitmapWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void onClickStepListener() {
        getActivity().finish();
    }
}