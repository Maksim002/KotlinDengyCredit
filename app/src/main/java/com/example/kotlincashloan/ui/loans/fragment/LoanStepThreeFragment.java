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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.graphics.BitmapFactory.decodeStream;
import static com.regula.documentreader.api.enums.LCID.KYRGYZ_CYRILICK;

public class LoanStepThreeFragment extends Fragment {

    private static final int REQUEST_BROWSE_PICTURE = 11;

    private TextView showScanner;

    private ImageView portraitIv;
    private ImageView docImageIv;

    private LinearLayout layout_status, status_no_questionnaire;
    private Button no_connection_repeat;

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
        status_no_questionnaire = view.findViewById(R.id.status_no_questionnaire);

        no_connection_repeat = view.findViewById(R.id.no_connection_repeat);

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
    }

    private void initInternet(){
        new ObservedInternet().observedInternet(requireContext());
        if (!AppPreferences.INSTANCE.getObservedInternet()) {
            status_no_questionnaire.setVisibility(View.VISIBLE);
            layout_status.setVisibility(View.GONE);
        }else {
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
        }else {
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
        if(loadingDialog!=null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Image browsing intent processed successfully
            if (requestCode == REQUEST_BROWSE_PICTURE){
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
                if(loadingDialog!=null && loadingDialog.isShowing()){
                    loadingDialog.dismiss();
                }

                //Checking, if nfc chip reading should be performed
                if (doRfid && results!=null && results.chipPage != 0) {
                    //setting the chip's access key - mrz on car access number
                    String accessKey = null;
                    if ((accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)) != null && !accessKey.isEmpty()) {
                        accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                                .replace("^", "").replace("\n","");
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
                if(action==DocReaderAction.CANCEL){
                    Toast.makeText(requireContext(), "Scanning was cancelled",Toast.LENGTH_LONG).show();
                } else if(action == DocReaderAction.ERROR){
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
    private void displayResults(DocumentReaderResults results){
        if(results!=null) {
            try {
                //ФИО
                String name = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME_AND_GIVEN_NAMES);
                if (name != null){

                }

                //ФИО (Нац)
                String nameTv = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME_AND_GIVEN_NAMES, KYRGYZ_CYRILICK);
                if (nameTv != null){

                }


                //Фамилия
                String surnameS = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME);
                if (surnameS != null){

                }

                //Фамилия (Ныц)
                String surnameNationalS = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME, KYRGYZ_CYRILICK);
                if (surnameNationalS != null){

                }

                //Имя
                String namesS = results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES);
                if (namesS != null){

                }

                //Имя (Нац)
                String namesNationalS = results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES, KYRGYZ_CYRILICK);
                if (namesNationalS != null){

                }

                //Отчество (Нац)
                String NameNationalityS = results.getTextFieldValueByType(eVisualFieldType.FT_FATHERS_NAME);
                if (NameNationalityS != null){

                }

                //Дата рождения
                String dataS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH);
                if (dataS != null){

                }

                // Место рождения
                String laceOfBirthS = results.getTextFieldValueByType(eVisualFieldType.FT_PLACE_OF_BIRTH);
                if (laceOfBirthS != null){

                }

                //Код государтсва выдочи
                String placeCodeS = results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_CODE);
                if (placeCodeS != null){

                }

                //номер документа
                String documentNumberS = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER);
                if (documentNumberS != null){

                }

                //Личный номер
                String personalNumberS = results.getTextFieldValueByType(eVisualFieldType.FT_PERSONAL_NUMBER);
                if (personalNumberS != null){

                }

                //возраст
                String ageS = results.getTextFieldValueByType(eVisualFieldType.FT_AGE);
                if (ageS != null){

                }

                //пол
                String sexS = results.getTextFieldValueByType(eVisualFieldType.FT_SEX);
                if (sexS != null){

                }
                // оставшися срок
                String remainderTermS = results.getTextFieldValueByType(eVisualFieldType.FT_REMAINDER_TERM);
                if (remainderTermS != null){

                }

                // Дата окончания действия
                String dateExpiryS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_EXPIRY);
                if (dateExpiryS != null){

                }

                // Орган выдачи
                String authorityS = results.getTextFieldValueByType(eVisualFieldType.FT_AUTHORITY);
                if (authorityS != null){

                }

                // Тип mrz
                String mrzS = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_TYPE);
                if (mrzS != null){

                }

                // Строки mrz
                String MRZStringsS = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS);
                if (MRZStringsS != null){

                }

                // Дополонительные данные
                String optionalDataS = results.getTextFieldValueByType(eVisualFieldType.FT_OPTIONAL_DATA);
                if (optionalDataS != null){

                }

                // Контрольная цифра номера документа
                String documentNumberCheckS = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER_CHECK_DIGIT);
                if (documentNumberCheckS != null){

                }

                // Контрольная цифра даты рождения
                String dateOfBirthS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH_CHECK_DIGIT);
                if (dateOfBirthS != null){

                }

                // Общая контрольная цифра
                String finalCheckDigitS = results.getTextFieldValueByType(eVisualFieldType.FT_FINAL_CHECK_DIGIT);
                if (finalCheckDigitS != null){

                }

                // Линея 2 дополниотельные данные
                String line2OptionalDataS = results.getTextFieldValueByType(eVisualFieldType.FT_LINE_2_OPTIONAL_DATA);
                if (line2OptionalDataS != null){

                }

                // Лет с мамента выпуска
                String yearsSinceIssueS = results.getTextFieldValueByType(eVisualFieldType.FT_YEARS_SINCE_ISSUE);
                if (yearsSinceIssueS != null){

                }

                // Код типа документа
                String documentClassCodeS = results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_CLASS_CODE);
                if (documentClassCodeS != null){

                }

                // Нацанальность (Нац)
                String nationalityS = results.getTextFieldValueByType(eVisualFieldType.FT_NATIONALITY, KYRGYZ_CYRILICK);
                if (nationalityS != null){

                }

                // Название государтсво выдачи
                String issuingStateNameS = results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_NAME);
                if (issuingStateNameS != null){

                }

                // Дата выпуска
                String date_of_IssueS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_ISSUE);
                if (date_of_IssueS != null){

                }

                // Контрольная цифра даты окончания действия
                String expiryCheckDigitS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_EXPIRY_CHECK_DIGIT);
                if (expiryCheckDigitS != null){

                }

                // Код национальности
                String nationalityCodeS = results.getTextFieldValueByType(eVisualFieldType.FT_NATIONALITY_CODE);
                if (nationalityCodeS != null){

                }

                // Код национальности (Нац)
                String placeOfIssueS = results.getTextFieldValueByType(eVisualFieldType.FT_PLACE_OF_ISSUE, KYRGYZ_CYRILICK);
                if (placeOfIssueS != null){

                }

                // Адрес
                String addressS = results.getTextFieldValueByType(eVisualFieldType.FT_ADDRESS, KYRGYZ_CYRILICK);
                if (addressS != null){

                }

                // Тип документа
                String documentClassNameS = results.documentType.get(0).name;
                if (documentClassNameS != null){

                }

                // through all text fields
                if(results.textResult != null && results.textResult.fields != null) {
                    for (DocumentReaderTextField textField : results.textResult.fields) {
                        String value = results.getTextFieldValueByType(textField.fieldType, textField.lcid);
                        Log.d("MainActivity", value + "\n");
                    }
                }

                Bitmap portrait = results.getGraphicFieldImageByType(eGraphicFieldType.GF_PORTRAIT);
                if(portrait!=null){
                    portraitIv.setImageBitmap(portrait);
                }

                Bitmap documentImage = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE);
                if(documentImage!=null){
                    double aspectRatio = (double) documentImage.getWidth() / (double) documentImage.getHeight();
                    documentImage = Bitmap.createScaledBitmap(documentImage, (int)(480 * aspectRatio), 480, false);
                    docImageIv.setImageBitmap(documentImage);
                }

                Bitmap documentImageTwo = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE, eRPRM_ResultType.RPRM_RESULT_TYPE_RAW_IMAGE, 1);
                if(documentImageTwo!=null){

                    double aspectRatio = (double) documentImageTwo.getWidth() / (double) documentImageTwo.getHeight();
                    documentImageTwo = Bitmap.createScaledBitmap(documentImageTwo, (int)(480 * aspectRatio), 480, false);

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void clearResults(){
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