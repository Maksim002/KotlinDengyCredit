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
import com.example.kotlincashloan.service.model.login.ImageStringModel;
import com.example.kotlincashloan.service.model.login.SaveLoanModel;
import com.example.kotlincashloan.ui.loans.GetLoanActivity;
import com.example.kotlincashloan.ui.loans.LoansViewModel;
import com.example.kotlincashloan.ui.registration.login.HomeActivity;
import com.example.kotlincashloan.utils.ObservedInternet;
import com.regula.documentreader.api.DocumentReader;
import com.regula.documentreader.api.completions.IDocumentReaderCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderInitCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderPrepareCompletion;
import com.regula.documentreader.api.enums.DocReaderAction;
import com.regula.documentreader.api.enums.eGraphicFieldType;
import com.regula.documentreader.api.enums.eRFID_Password_Type;
import com.regula.documentreader.api.enums.eRPRM_ResultType;
import com.regula.documentreader.api.enums.eVisualFieldType;
import com.regula.documentreader.api.results.DocumentReaderResults;
import com.regula.documentreader.api.results.DocumentReaderScenario;
import com.regula.documentreader.api.results.DocumentReaderTextField;
import com.timelysoft.tsjdomcom.service.AppPreferences;

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

public class LoanStepThreeFragment extends Fragment {
    ArrayList<ImageStringModel> list = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<>();
    private LoansViewModel viewModel = new LoansViewModel();
    private Bitmap documentImageTwo;
    private Bitmap portrait;
    private Bitmap documentImage;

    private static final int REQUEST_BROWSE_PICTURE = 11;

    private TextView showScanner;

    private ImageView portraitIv;
    private ImageView docImageIv;

    private LinearLayout layout_status, status_no_questionnaire, status_technical_work, status_not_found;
    private Button no_connection_repeat, get_view_click, technical_work, not_found;

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

        showScanner = view.findViewById(R.id.loan_active_status);
        portraitIv = view.findViewById(R.id.portraitIv);
        docImageIv = view.findViewById(R.id.documentImageIv);

        layout_status = view.findViewById(R.id.layout_status);
        status_technical_work = view.findViewById(R.id.status_technical_work);
        status_no_questionnaire = view.findViewById(R.id.status_no_questionnaire);
        status_not_found = view.findViewById(R.id.status_not_found);

        no_connection_repeat = view.findViewById(R.id.no_connection_repeat);
        get_view_click = view.findViewById(R.id.get_view_click);
        technical_work = view.findViewById(R.id.technical_work);
        not_found = view.findViewById(R.id.not_found);

        initInternet();
        initClick();
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
                initResult();
            }
        });

        not_found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initResult();
            }
        });


        get_view_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initResult();
            }
        });
    }

    private void initResult() {
        HomeActivity.alert.show();
        map.put("login", AppPreferences.INSTANCE.getLogin());
        map.put("token", AppPreferences.INSTANCE.getToken());
        if (portrait != null) {
            map.put("passport_photo", list.get(0).getString());
        } else {
            map.put("passport_photo", "");
        }
        if (documentImage != null) {
            map.put("passport_img_a", list.get(1).getString());
        } else {
            map.put("passport_img_a", "");
        }
        if (documentImageTwo != null) {
            map.put("passport_img_b", list.get(2).getString());
        } else {
            map.put("passport_img_b", "");
        }
        map.put("loan_type", "1");
        map.put("loan_term", AppPreferences.INSTANCE.getType());
        map.put("loan_sum", AppPreferences.INSTANCE.getSum());
        map.put("step", "1");

        viewModel.saveLoan(map);

        viewModel.getGetSaveLoan().observe(getViewLifecycleOwner(), new Observer<SaveLoanModel>() {
            @Override
            public void onChanged(SaveLoanModel result) {
                if (result.getResult() != null) {
                    if (result.getResult().getId() != null) {
                        layout_status.setVisibility(View.VISIBLE);
                        status_technical_work.setVisibility(View.GONE);
                        status_no_questionnaire.setVisibility(View.GONE);
                        status_not_found.setVisibility(View.GONE);
                        AppPreferences.INSTANCE.setSum(result.getResult().getId().toString());
                        ((GetLoanActivity) getActivity()).get_loan_view_pagers.setCurrentItem(3);
                    } else if (result.getResult().getMessage() != null) {
                        initBottomSheet(result.getReject().getMessage());
                        layout_status.setVisibility(View.VISIBLE);
                        status_technical_work.setVisibility(View.GONE);
                        status_no_questionnaire.setVisibility(View.GONE);
                        status_not_found.setVisibility(View.GONE);
                    }
                } else {
                    if (result.getError().getCode().equals(400)) {
                        Toast.makeText(requireContext(), "Отсканируйте документ повторно", Toast.LENGTH_LONG).show();
                    } else if (result.getError().getCode() == 500) {
                        status_technical_work.setVisibility(View.VISIBLE);
                        status_no_questionnaire.setVisibility(View.GONE);
                        layout_status.setVisibility(View.GONE);
                        status_not_found.setVisibility(View.GONE);
                    } else if (result.getError().getCode() == 401) {
                        initAuthorized();
                    } else if (result.getError().getCode() == 409) {
                        Toast.makeText(requireContext(), "Анкета уже создана", Toast.LENGTH_LONG).show();
                    } else if (result.getError().getCode() == 404) {
                        status_not_found.setVisibility(View.VISIBLE);
                        status_technical_work.setVisibility(View.GONE);
                        status_no_questionnaire.setVisibility(View.GONE);
                        layout_status.setVisibility(View.GONE);
                    }
                }
            }
        });

        viewModel.getErrorSaveLoan().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    if (error.equals("601")) {
                        status_no_questionnaire.setVisibility(View.VISIBLE);
                        status_technical_work.setVisibility(View.GONE);
                        layout_status.setVisibility(View.GONE);
                        status_not_found.setVisibility(View.GONE);
                    } else if (error.equals("401")) {
                        initAuthorized();
                    } else if (error.equals("500")) {
                        status_technical_work.setVisibility(View.VISIBLE);
                        status_no_questionnaire.setVisibility(View.GONE);
                        layout_status.setVisibility(View.GONE);
                        status_not_found.setVisibility(View.GONE);
                    } else if (error.equals("409")) {
                        Toast.makeText(requireContext(), "Анкета уже создана", Toast.LENGTH_LONG).show();
                    } else if (error.equals("404")) {
                        status_not_found.setVisibility(View.VISIBLE);
                        status_technical_work.setVisibility(View.GONE);
                        status_no_questionnaire.setVisibility(View.GONE);
                        layout_status.setVisibility(View.GONE);
                    } else if (error.equals("400")) {
                        Toast.makeText(requireContext(), "Отсканируйте документ повторно", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void initBottomSheet(String message) {
        StepBottomFragment stepBottomFragment = new StepBottomFragment(message);
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
            layout_status.setVisibility(View.GONE);
        } else {
            initDocumentReader();
        }
    }


    private void initDocumentReader() {
        if (!DocumentReader.Instance().getDocumentReaderIsReady()) {
            final AlertDialog initDialog = showDialog("Инициализация");

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
                        initDialog.setTitle("Загрузка базы данных: " + progress + "%");
                    }

                    @Override
                    public void onPrepareCompleted(boolean status, Throwable error) {

                        //Initializing the reader
                        DocumentReader.Instance().initializeReader(getActivity(), license, new IDocumentReaderInitCompletion() {
                            @Override
                            public void onInitCompleted(boolean success, Throwable error) {
                                if (initDialog.isShowing()) {
                                    initDialog.dismiss();
                                    layout_status.setVisibility(View.VISIBLE);
                                    status_no_questionnaire.setVisibility(View.GONE);
                                }

                                DocumentReader.Instance().customization().edit().setShowHelpAnimation(false).apply();

                                //initialization successful
                                if (success) {
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
                                    Toast.makeText(requireContext(), "Init failed:" + error, Toast.LENGTH_LONG).show();
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

                    loadingDialog = showDialog("Processing image");

                    DocumentReader.Instance().recognizeImage(bmp, completion);
                }
            }
        }
    }

    //DocumentReader processing callback
    private IDocumentReaderCompletion completion = new IDocumentReaderCompletion() {
        @Override
        public void onCompleted(int action, DocumentReaderResults results, Throwable error) {
            //processing is finished, all results are ready
            if (action == DocReaderAction.COMPLETE) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }

                //Checking, if nfc chip reading should be performed
                if (doRfid && results != null && results.chipPage != 0) {
                    //setting the chip's access key - mrz on car access number
                    String accessKey = null;
                    if ((accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)) != null && !accessKey.isEmpty()) {
                        accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                                .replace("^", "").replace("\n", "");
                        DocumentReader.Instance().rfidScenario().setMrz(accessKey);
                        DocumentReader.Instance().rfidScenario().setPacePasswordType(eRFID_Password_Type.PPT_MRZ);
                    } else if ((accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_CARD_ACCESS_NUMBER)) != null && !accessKey.isEmpty()) {
                        DocumentReader.Instance().rfidScenario().setPassword(accessKey);
                        DocumentReader.Instance().rfidScenario().setPacePasswordType(eRFID_Password_Type.PPT_CAN);
                    }

                    //starting chip reading
                    DocumentReader.Instance().startRFIDReader(requireContext(), new IDocumentReaderCompletion() {
                        @Override
                        public void onCompleted(int rfidAction, DocumentReaderResults results, Throwable error) {
                            if (rfidAction == DocReaderAction.COMPLETE || rfidAction == DocReaderAction.CANCEL) {
                                displayResults(results);
                            }
                        }
                    });
                } else {
                    displayResults(results);
                }
            } else {
                //something happened before all results were ready
                if (action == DocReaderAction.CANCEL) {
                    Toast.makeText(requireContext(), "Scanning was cancelled", Toast.LENGTH_LONG).show();
                } else if (action == DocReaderAction.ERROR) {
                    Toast.makeText(requireContext(), "Error:" + error, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private AlertDialog showDialog(String msg) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.simple_dialog, null);
        dialog.setTitle(msg);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        return dialog.show();
    }

    //show received results on the UI
    private void displayResults(DocumentReaderResults results) {
        if (results != null) {
            try {
                //Фамилия
                String surnameS = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME);
                if (surnameS != null) {
                    map.put("last_name_l", surnameS);
                } else {
                    map.put("last_name_l", "");
                }

                //Имя
                String namesS = results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES);
                if (namesS != null) {
                    map.put("first_name_l", namesS);
                } else {
                    map.put("first_name_l", "");
                }

                //Фамилия (Ныц)
                String surnameNationalS = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME, KYRGYZ_CYRILICK);
                if (surnameNationalS != null) {
                    map.put("last_name", surnameNationalS);
                } else {
                    map.put("last_name", "");
                }

                //Имя (Нац)
                String namesNationalS = results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES, KYRGYZ_CYRILICK);
                if (namesNationalS != null) {
                    map.put("first_name", namesNationalS);
                } else {
                    map.put("first_name", "");
                }

                //Отчество (Нац)
                String nameNationalityS = results.getTextFieldValueByType(eVisualFieldType.FT_FATHERS_NAME);
                if (nameNationalityS != null) {
                    map.put("second_name_l", "");
                    map.put("second_name", nameNationalityS);
                } else {
                    map.put("second_name_l", "");
                    map.put("second_name", "");
                }

                //Дата рождения
                String dataS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH);
                SimpleDateFormat sdf = null;
                if (dataS != null) {
                   if (dataS.length() == 8){
                       sdf = new SimpleDateFormat("dd.MM.yy");
                   }else {
                       sdf = new SimpleDateFormat("dd.MM.yyy");
                   }
                    Date d1 = sdf.parse(dataS);
                    sdf.applyPattern("yyyy-MM-dd");
                    map.put("u_date", sdf.format(d1));
                } else {
                    map.put("u_date", "");
                }

                //пол
                String sexS = results.getTextFieldValueByType(eVisualFieldType.FT_SEX);
                if (sexS != null) {
                    map.put("gender", sexS);
                } else {
                    map.put("gender", "");
                }

                // Место рождения
                String laceOfBirthS = results.getTextFieldValueByType(eVisualFieldType.FT_PLACE_OF_BIRTH);
                if (laceOfBirthS != null) {

                }

                //Код государтсва выдочи
                String placeCodeS = results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_CODE);
                if (placeCodeS != null) {
                    map.put("nationality", placeCodeS);
                } else {
                    map.put("nationality", "");
                }

                //номер документа
                String documentNumberS = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER);
                if (documentNumberS != null) {
                    map.put("passport_number", documentNumberS);
                } else {
                    map.put("passport_number", "");
                }

                //Личный номер
                String personalNumberS = results.getTextFieldValueByType(eVisualFieldType.FT_PERSONAL_NUMBER);
                if (personalNumberS != null) {
                    map.put("passport_inn", personalNumberS);
                } else {
                    map.put("passport_inn", "");
                }

                //возраст
                String ageS = results.getTextFieldValueByType(eVisualFieldType.FT_AGE);
                if (ageS != null) {

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
                    if (dataS.length() == 8){
                        sdf = new SimpleDateFormat("dd.MM.yy");
                    }else {
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
                    map.put("passport_series", documentClassCodeS);
                } else {
                    map.put("passport_series", "");
                }

                // Название государтсво выдачи
                String issuingStateNameS = results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_NAME);
                if (issuingStateNameS != null) {
                    map.put("nationality", issuingStateNameS);
                } else {
                    map.put("nationality", "");
                }

                // Дата выпуска
                String date_of_IssueS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_ISSUE);
                if (date_of_IssueS != null) {
                    if (dataS.length() == 8){
                        sdf = new SimpleDateFormat("dd.MM.yy");
                    }else {
                        sdf = new SimpleDateFormat("dd.MM.yyy");
                    }
                    Date d1 = sdf.parse(date_of_IssueS);
                    sdf.applyPattern("yyyy-MM-dd");
                    map.put("passport_issue", sdf.format(d1));
                } else {
                    map.put("passport_issue", "");
                }

                // Тип mrz
                String mrzS = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_TYPE);
                if (mrzS != null) {

                }

                // Строки mrz
                String MRZStringsS = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS);
                if (MRZStringsS != null) {

                }

                // Дополонительные данные
                String optionalDataS = results.getTextFieldValueByType(eVisualFieldType.FT_OPTIONAL_DATA);
                if (optionalDataS != null) {

                }

                // Контрольная цифра номера документа
                String documentNumberCheckS = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER_CHECK_DIGIT);
                if (documentNumberCheckS != null) {

                }

                // Контрольная цифра даты рождения
                String dateOfBirthS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH_CHECK_DIGIT);
                if (dateOfBirthS != null) {

                }

                // Общая контрольная цифра
                String finalCheckDigitS = results.getTextFieldValueByType(eVisualFieldType.FT_FINAL_CHECK_DIGIT);
                if (finalCheckDigitS != null) {

                }

                // Линея 2 дополниотельные данные
                String line2OptionalDataS = results.getTextFieldValueByType(eVisualFieldType.FT_LINE_2_OPTIONAL_DATA);
                if (line2OptionalDataS != null) {

                }

                // Лет с мамента выпуска
                String yearsSinceIssueS = results.getTextFieldValueByType(eVisualFieldType.FT_YEARS_SINCE_ISSUE);
                if (yearsSinceIssueS != null) {

                }

                // Нацанальность (Нац)
                String nationalityS = results.getTextFieldValueByType(eVisualFieldType.FT_NATIONALITY, KYRGYZ_CYRILICK);
                if (nationalityS != null) {

                }

                // Контрольная цифра даты окончания действия
                String expiryCheckDigitS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_EXPIRY_CHECK_DIGIT);
                if (expiryCheckDigitS != null) {

                }

                // Код национальности
                String nationalityCodeS = results.getTextFieldValueByType(eVisualFieldType.FT_NATIONALITY_CODE);
                if (nationalityCodeS != null) {

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

                // Адрес
                String addressS = results.getTextFieldValueByType(eVisualFieldType.FT_ADDRESS, KYRGYZ_CYRILICK);
                if (addressS != null) {

                }

                // Тип документа
                String documentClassNameS = results.documentType.get(0).name;
                if (documentClassNameS != null) {

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
                    portraitIv.setImageBitmap(portrait);
                    gotImageString(portrait);
                }

                documentImage = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE);
                if (documentImage != null) {
                    double aspectRatio = (double) documentImage.getWidth() / (double) documentImage.getHeight();
                    documentImage = Bitmap.createScaledBitmap(documentImage, (int) (480 * aspectRatio), 480, false);
                    docImageIv.setImageBitmap(documentImage);
                    gotImageString(documentImage);
                }

                documentImageTwo = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE, eRPRM_ResultType.RPRM_RESULT_TYPE_RAW_IMAGE, 1);
                if (documentImageTwo != null) {

                    double aspectRatio = (double) documentImageTwo.getWidth() / (double) documentImageTwo.getHeight();
                    documentImageTwo = Bitmap.createScaledBitmap(documentImageTwo, (int) (480 * aspectRatio), 480, false);
                    gotImageString(documentImageTwo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //encode image to base64 string
    private void gotImageString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        list.add(new ImageStringModel(imageString));
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
}