package com.example.kotlincashloan.ui.loans.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.service.model.Loans.EntryGoalResultModel
import com.example.kotlincashloan.service.model.Loans.MyDataListModel
import com.example.kotlincashloan.service.model.Loans.MyImageModel
import com.example.kotlincashloan.service.model.Loans.TypeContractResultModel
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.spinnerdatepickerlib.DatePicker
import com.example.spinnerdatepickerlib.DatePickerDialog
import com.example.spinnerdatepickerlib.SpinnerDatePickerDialogBuilder
import com.regula.documentreader.api.DocumentReader
import com.regula.documentreader.api.completions.IDocumentReaderCompletion
import com.regula.documentreader.api.completions.IDocumentReaderPrepareCompletion
import com.regula.documentreader.api.enums.*
import com.regula.documentreader.api.errors.DocumentReaderException
import com.regula.documentreader.api.results.DocumentReaderResults
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_loan_step_fifth.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class LoanStepFifthFragment : Fragment(), ListenerGeneralResult,
    DatePickerDialog.OnDateSetListener {
    private var viewModel = LoansViewModel()
    private val IMAGE_PICK_CODE = 10
    private val CAMERA_PERM_CODE = 101
    private val REQUEST_BROWSE_PICTURE = 11
    private lateinit var currentPhotoPath: String
    private lateinit var myThread: Thread
    private val imageString = HashMap<String, MyImageModel>()
    private var stringImKey: ArrayList<String> = arrayListOf()
    private var imageKey = ""
    private val doRfid = false
    private var documentImageTwo: Bitmap? = null
    private var portrait: Bitmap? = null
    private var documentImage: Bitmap? = null

    private val onCancel: DatePickerDialog.OnDateCancelListener? = null
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var data: String = ""
    private var keyData = ""
    private var countriesList: ArrayList<MyDataListModel> = arrayListOf()
    private var goalList: ArrayList<MyDataListModel> = arrayListOf()
    private var contractList: ArrayList<MyDataListModel> = arrayListOf()

    private lateinit var calendar: GregorianCalendar

    private var listEntryError = ""
    private var listContractError = ""

    private var goalPosition = -1
    private var contractPosition = -1

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listEntryGoal: ArrayList<EntryGoalResultModel> = arrayListOf()
    private var listTypeContract: ArrayList<TypeContractResultModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_step_fifth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //формат даты
        simpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)

        initListEntryGoal()
        initTypeContract()
        initClick()
        iniData()
        initHidingFields()
        initDocumentReader()
    }

    //Скрытие полей
    private fun initHidingFields() {
        var l = "UZB"
        var t = "TJK"
        var n = "RUS"
        var g = "KGZ"

        if (l != "" || t != "") {
            patent.visibility = View.VISIBLE
        }
    }

    //listEntryGoal Список целей въезда
    private fun initListEntryGoal() {
        val mapEntry = mutableMapOf<String, String>()
        mapEntry["login"] = AppPreferences.login.toString()
        mapEntry["token"] = AppPreferences.token.toString()
        mapEntry["id"] = "0"
        viewModel.listEntryGoal(mapEntry)

        viewModel.listEntryGoal.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                listEntryError = result.code.toString()
                getResultOk()
                listEntryGoal = result.result
            } else {
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListEntryGoal.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                listEntryError = error
                errorList(error)
            }
        })
    }

    //listTypeContract Список типов договора.
    private fun initTypeContract() {
        val mapContract = mutableMapOf<String, String>()
        mapContract["login"] = AppPreferences.login.toString()
        mapContract["token"] = AppPreferences.token.toString()
        mapContract["id"] = "0"
        viewModel.listTypeContract(mapContract)

        viewModel.listTypeContract.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                listEntryError = result.code.toString()
                getResultOk()
                listTypeContract = result.result
            } else {
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListTypeContract.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                listEntryError = error
                errorList(error)
            }
        })
    }

    private fun initClick() {
        list_countries.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listEntryGoal.size) {
                    if (i <= listEntryGoal.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listEntryGoal[i - 1].name.toString(),
                                "listEntryGoal",
                                i - 1
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, goalPosition)
            }
        }

        contract_type.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listTypeContract.size) {
                    if (i <= listTypeContract.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listTypeContract[i - 1].name.toString(),
                                "listTypeContract",
                                i - 1
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, contractPosition)
            }
        }

        russian_federation.setOnClickListener {
            imageKey = "russian_federation"
            loadFiles()
        }

        migration_card.setOnClickListener {
            imageKey = "migration_card"
            loadFiles()
        }

        receipt_patent.setOnClickListener {
            imageKey = "receipt_patent"
            loadFiles()
        }

        work_permit.setOnClickListener {
            imageKey = "work_permit"
            loadFiles()
        }

        photo_2NDFL.setOnClickListener {
            imageKey = "photo_2NDFL"
            loadFiles()
        }

        last_page_one.setOnClickListener {
            imageKey = "last_page_one"
            loadFiles()
        }

        last_page_two.setOnClickListener {
            imageKey = "last_page_two"
            loadFiles()
        }

        photo_RVP.setOnClickListener {
            imageKey = "photo_RVP"
            loadFiles()
        }

        photo_VNJ.setOnClickListener {
            imageKey = "photo_VNJ"
            loadFiles()
        }
    }

    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listEntryGoal") {
            list_countries.setText(listEntryGoal[model.position].name)
            goalPosition = model.position
            list_countries.error = null
        }

        if (model.key == "listTypeContract") {
            contract_type.setText(listTypeContract[model.position].name)
            contractPosition = model.position
            contract_type.error = null
        }
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(list: ArrayList<GeneralDialogModel>, selectionPosition: Int) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition)
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    //Метод выгружает картинку с памяти телефона
    private fun loadFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERM_CODE
                )
            } else {
                getMyFile()
            }
        } else {
            getMyFile()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMyFile()
        } else {
            Toast.makeText(context, "Нет разрешений", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyFile() {
        val file = "photo"
        val dtoregDirectiry: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try {
            val files = File.createTempFile(file, ".jpg", dtoregDirectiry)
            currentPhotoPath = files.absolutePath
            val imagUri: Uri =
                FileProvider.getUriForFile(requireContext(), "com.example.kotlincashloan", files)
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagUri)
            startActivityForResult(takePictureIntent, IMAGE_PICK_CODE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            if (data == null) {
                val imageBitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val nh = (imageBitmap.height * (512.0 / imageBitmap.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(imageBitmap, 512, nh, true)
                imageConverter(scaled)
                if (imageKey == "russian_federation") {
                    russian_federation.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "migration_card") {
                    migration_card.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "receipt_patent") {
                    receipt_patent.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "work_permit") {
                    work_permit.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "photo_2NDFL") {
                    photo_2NDFL.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "last_page_one") {
                    last_page_one.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "last_page_two") {
                    last_page_two.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "photo_RVP") {
                    photo_RVP.setImageBitmap(scaled)
                    imageKey = ""
                } else if (imageKey == "photo_VNJ") {
                    photo_VNJ.setImageBitmap(scaled)
                    imageKey = ""
                }
            }
        }

        if (requestCode == REQUEST_BROWSE_PICTURE) {
            if (data!!.data != null) {
                val selectedImage = data.data
                val bmp: Bitmap? = selectedImage?.let { getBitmap(it, 1920, 1080) }
//                russian_patent.setText("Processing image")
                //                    loadingDialog = showDialog("Processing image");
                if (bmp != null) {
                    DocumentReader.Instance().recognizeImage(bmp, completion)
                }
            }
        }
    }

    //encode image to base64 string
    private fun imageConverter(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes: ByteArray = baos.toByteArray()
        val image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        imageString.put(imageKey, MyImageModel(image))
    }

    private fun iniData() {
        fifth_goal_name.setOnClickListener(View.OnClickListener { v: View? ->
            keyData = "fifth_goal_name"
            if (countriesList.size != 0) {
                showDate(
                    countriesList[0].year,
                    countriesList[0].monthOfYear,
                    countriesList[0].dayOfMonth,
                    R.style.DatePickerSpinner
                )
            } else {
                showDate(1990, 0, 1, R.style.DatePickerSpinner)
            }
        })

        date_entry.setOnClickListener {
            keyData = "list_countries"
            if (goalList.size != 0) {
                showDate(
                    goalList[0].year,
                    goalList[0].monthOfYear,
                    goalList[0].dayOfMonth,
                    R.style.DatePickerSpinner
                )
            } else {
                showDate(1990, 0, 1, R.style.DatePickerSpinner)
            }
        }

        date_the_contract.setOnClickListener {
            keyData = "date_the_contract"
            if (contractList.size != 0) {
                showDate(
                    contractList[0].year,
                    contractList[0].monthOfYear,
                    contractList[0].dayOfMonth,
                    R.style.DatePickerSpinner
                )
            } else {
                showDate(1990, 0, 1, R.style.DatePickerSpinner)
            }
        }
    }

    //Получение даты
    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
        data = (MyUtils.convertDate(year, monthOfYear, dayOfMonth))
        //проверяет на своподение ключа
        if (keyData == "fifth_goal_name") {
            if (countriesList.size != 0) {
                //пробегается по цыклу и удолят эдемент с этим ключом
                for (i in 1..countriesList.size) {
                    while (countriesList[i - 1].key == "fifth_goal_name") {
                        countriesList.removeAt(i - 1)
                        //отоброжает данные в TextView
                        fifth_goal_name.setText(simpleDateFormat.format(calendar.getTime()))
                        //доболяет новый элемент в список
                        countriesList.add(
                            MyDataListModel(
                                "fifth_goal_name",
                                year,
                                monthOfYear,
                                dayOfMonth
                            )
                        )
                        break
                    }
                }
            } else {
                fifth_goal_name.setText(simpleDateFormat.format(calendar.getTime()))
                countriesList.add(MyDataListModel("fifth_goal_name", year, monthOfYear, dayOfMonth))
            }
        } else if (keyData == "list_countries") {
            if (goalList.size != 0) {
                for (i in 1..goalList.size) {
                    while (goalList[i - 1].key == "fifth_goal_name") {
                        goalList.removeAt(i - 1)
                        date_entry.setText(simpleDateFormat.format(calendar.getTime()))
                        goalList.add(
                            MyDataListModel(
                                "fifth_goal_name",
                                year,
                                monthOfYear,
                                dayOfMonth
                            )
                        )
                        break
                    }
                }
            } else {
                date_entry.setText(simpleDateFormat.format(calendar.getTime()))
                goalList.add(MyDataListModel("fifth_goal_name", year, monthOfYear, dayOfMonth))
            }
        } else if (keyData == "date_the_contract") {
            if (contractList.size != 0) {
                for (i in 1..contractList.size) {
                    while (contractList[i - 1].key == "date_the_contract") {
                        contractList.removeAt(i - 1)
                        date_the_contract.setText(simpleDateFormat.format(calendar.getTime()))
                        contractList.add(
                            MyDataListModel(
                                "date_the_contract",
                                year,
                                monthOfYear,
                                dayOfMonth
                            )
                        )
                        break
                    }
                }
            } else {
                date_the_contract.setText(simpleDateFormat.format(calendar.getTime()))
                contractList.add(
                    MyDataListModel(
                        "date_the_contract",
                        year,
                        monthOfYear,
                        dayOfMonth
                    )
                )
            }
        }
    }

    @VisibleForTesting
    fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(context)
            .callback(this)
            .onCancel(onCancel)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
    }

    private fun getResultOk() {
        if (listEntryError == "200" && listContractError == "200") {
            layout_fifth.visibility = View.VISIBLE
            fifth_ste_technical_work.visibility = View.GONE
            fifth_ste_no_connection.visibility = View.GONE
            fifth_ste_access_restricted.visibility = View.GONE
            fifth_ste_not_found.visibility = View.GONE
        }
    }

    private fun listResult(result: Int) {
        if (result == 400 || result == 500 || result == 409 || result == 429) {
            fifth_ste_technical_work.visibility = View.VISIBLE
            layout_fifth.visibility = View.GONE
            fifth_ste_no_connection.visibility = View.GONE
            fifth_ste_access_restricted.visibility = View.GONE
            fifth_ste_not_found.visibility = View.GONE
        } else if (result == 403) {
            fifth_ste_access_restricted.visibility = View.VISIBLE
            layout_fifth.visibility = View.GONE
            fifth_ste_technical_work.visibility = View.GONE
            fifth_ste_no_connection.visibility = View.GONE
            fifth_ste_not_found.visibility = View.GONE
        } else if (result == 404) {
            fifth_ste_not_found.visibility = View.VISIBLE
            layout_fifth.visibility = View.GONE
            fifth_ste_technical_work.visibility = View.GONE
            fifth_ste_no_connection.visibility = View.GONE
            fifth_ste_access_restricted.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        }
    }

    private fun errorList(error: String) {
        if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409") {
            fifth_ste_technical_work.visibility = View.VISIBLE
            layout_fifth.visibility = View.GONE
            fifth_ste_no_connection.visibility = View.GONE
            fifth_ste_access_restricted.visibility = View.GONE
            fifth_ste_not_found.visibility = View.GONE
        } else if (error == "403") {
            fifth_ste_access_restricted.visibility = View.VISIBLE
            layout_fifth.visibility = View.GONE
            fifth_ste_technical_work.visibility = View.GONE
            fifth_ste_no_connection.visibility = View.GONE
            fifth_ste_not_found.visibility = View.GONE
        } else if (error == "404") {
            fifth_ste_not_found.visibility = View.VISIBLE
            layout_fifth.visibility = View.GONE
            fifth_ste_technical_work.visibility = View.GONE
            fifth_ste_no_connection.visibility = View.GONE
            fifth_ste_access_restricted.visibility = View.GONE
        } else if (error == "601") {
            fifth_ste_no_connection.visibility = View.VISIBLE
            fifth_ste_not_found.visibility = View.GONE
            layout_fifth.visibility = View.GONE
            fifth_ste_technical_work.visibility = View.GONE
            fifth_ste_access_restricted.visibility = View.GONE
        } else if (error == "401") {
            initAuthorized()
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    private fun initDocumentReader() {
        try {

            if (!DocumentReader.Instance().documentReaderIsReady) {
                text.setText("Инициализация")
                russian_patent.setClickable(false)


                //Reading the license from raw resource file
                try {
                    val licInput = resources.openRawResource(R.raw.regula)
                    val available = licInput.available()
                    val license = ByteArray(available)
                    licInput.read(license)


                    //preparing database files, it will be downloaded from network only one time and stored on user device
                    DocumentReader.Instance().prepareDatabase(
                        requireContext(),
                        "Full",
                        object : IDocumentReaderPrepareCompletion {
                            override fun onPrepareProgressChanged(progress: Int) {
                                text.setText("Загрузка базы данных: $progress%")
                            }

                            override fun onPrepareCompleted(
                                p0: Boolean,
                                p1: DocumentReaderException?
                            ) {
                                //Initializing the reader
                                DocumentReader.Instance().initializeReader(
                                    requireContext(),
                                    license
                                ) { success, error_initializeReader ->
                                    text.setText("Фото патента РФ:")
                                    russian_patent.isClickable = true
                                    DocumentReader.Instance().customization().edit().setShowHelpAnimation(false).apply()
                                    DocumentReader.Instance().functionality().edit().setShowSkipNextPageButton(false).apply()
                                    if (success) {
                                        //initialization successful
                                        russian_patent.setOnClickListener(View.OnClickListener {
                                            //starting video processing
                                            DocumentReader.Instance().showScanner(requireContext(), completion)
                                            DocumentReader.Instance().processParams().multipageProcessing = true
                                        })

                                        //getting current processing scenario and loading available scenarios to ListView
                                        var currentScenario = DocumentReader.Instance().processParams().scenario
                                        val scenarios = ArrayList<String>()
                                        for (scenario in DocumentReader.Instance().availableScenarios) {
                                            scenarios.add(scenario.name)
                                        }

                                        //setting default scenario
                                        if (currentScenario == null || currentScenario.isEmpty()) {
                                            currentScenario = scenarios[3]
                                            DocumentReader.Instance().processParams().scenario =
                                                currentScenario
                                        }

                                    } else {
                                        //Initialization was not successful
                                        Toast.makeText(requireContext(), "Init failed:$p1", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        })
                    licInput.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else {
                russian_patent.setOnClickListener(View.OnClickListener {
                    //starting video processing
                    DocumentReader.Instance().showScanner(requireContext(), completion)
                    DocumentReader.Instance().processParams().multipageProcessing = true
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //DocumentReader processing callback
    private val completion = IDocumentReaderCompletion { i, documentReaderResults, e ->
        //processing is finished, all results are ready
        if (i == DocReaderAction.COMPLETE) {
//                if (loadingDialog != null && loadingDialog.isShowing()) {
//                    loadingDialog.dismiss()
//                }

            //Checking, if nfc chip reading should be performed
            if (doRfid && documentReaderResults != null && documentReaderResults.chipPage != 0) {
                //setting the chip's access key - mrz on car access number
                var accessKey: String? = null
                if (documentReaderResults.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                        .also {
                            accessKey = it
                        } != null && !accessKey!!.isEmpty()
                ) {
                    accessKey =
                        documentReaderResults.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                            .replace("^", "").replace("\n", "")
                    DocumentReader.Instance().rfidScenario().setMrz(accessKey)
                    DocumentReader.Instance().rfidScenario()
                        .setPacePasswordType(eRFID_Password_Type.PPT_MRZ)
                } else if (documentReaderResults.getTextFieldValueByType(eVisualFieldType.FT_CARD_ACCESS_NUMBER)
                        .also { accessKey = it } != null && !accessKey!!.isEmpty()
                ) {
                    DocumentReader.Instance().rfidScenario().setPassword(accessKey)
                    DocumentReader.Instance().rfidScenario()
                        .setPacePasswordType(eRFID_Password_Type.PPT_CAN)
                }

                //starting chip reading
                DocumentReader.Instance()
                    .startRFIDReader(requireContext()) { i, documentReaderResults, e ->
                        if (i == DocReaderAction.COMPLETE || i == DocReaderAction.CANCEL) {
                            displayResults(documentReaderResults)
                        }
                    }
            } else {
                displayResults(documentReaderResults)
            }
        } else {
            //something happened before all results were ready
            if (i == DocReaderAction.CANCEL) {
                Toast.makeText(requireContext(), "Scanning was cancelled", Toast.LENGTH_LONG)
                    .show()
            } else if (i == DocReaderAction.ERROR) {
                Toast.makeText(requireContext(), "Error:$e", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayResults(results: DocumentReaderResults?) {
        if (results != null) {
            try {
                //Фамилия
                val surnameS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME)
                if (surnameS != null) {

                } else {

                }

                //Имя
                val namesS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES)
                if (namesS != null) {

                } else {

                }

                //Фамилия (Ныц)
                val surnameNationalS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME, LCID.RUSSIAN)
                if (surnameNationalS != null) {

                } else {

                }

                //Имя (Нац)
                val namesNationalS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES, LCID.RUSSIAN)
                if (namesNationalS != null) {

                } else {

                }

                //Отчество (Нац)
                val nameNationalityS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_FATHERS_NAME, LCID.RUSSIAN)
                if (nameNationalityS != null) {

                } else {

                }

                //Дата рождения
                val dataS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH)
                var sdf: SimpleDateFormat? = null
                if (dataS != null) {
                    sdf = if (dataS.length == 8) {
                        SimpleDateFormat("dd.MM.yy")
                    } else {
                        SimpleDateFormat("dd.MM.yyy")
                    }
                    val d1 = sdf.parse(dataS)
                    sdf.applyPattern("yyyy-MM-dd")

                } else {

                }

                //пол
                val sexS = results.getTextFieldValueByType(eVisualFieldType.FT_SEX)
                if (sexS != null) {

                } else {

                }

                // Место рождения
                val laceOfBirthS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_PLACE_OF_BIRTH)
                if (laceOfBirthS != null) {
                }

                //Код государтсва выдочи
                val placeCodeS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_CODE)
                if (placeCodeS != null) {

                } else {

                }

                //номер документа
                val documentNumberS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER)
                if (documentNumberS != null) {

                } else {

                }

                //Личный номер
                val personalNumberS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_PERSONAL_NUMBER)
                if (personalNumberS != null) {

                } else {

                }

                //возраст
                val ageS = results.getTextFieldValueByType(eVisualFieldType.FT_AGE)
                if (ageS != null) {
                }

                // оставшися срок
                val remainderTermS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_REMAINDER_TERM)
                if (remainderTermS != null) {

                } else {

                }

                // Дата окончания действия
                val dateExpiryS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_EXPIRY)
                if (dateExpiryS != null) {
                    sdf = if (dataS!!.length == 8) {
                        SimpleDateFormat("dd.MM.yy")
                    } else {
                        SimpleDateFormat("dd.MM.yyy")
                    }
                    val d1 = sdf.parse(dateExpiryS)
                    sdf.applyPattern("yyyy-MM-dd")

                } else {

                }

                // Орган выдачи
                val authorityS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_AUTHORITY)
                if (authorityS != null) {

                } else {

                }

                // Код типа документа
                val documentClassCodeS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_CLASS_CODE)
                if (documentClassCodeS != null) {

                } else {

                }

                // Название государтсво выдачи
                val issuingStateNameS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_NAME)
                if (issuingStateNameS != null) {
                }

                // Дата выпуска
                val date_of_IssueS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_ISSUE)
                if (date_of_IssueS != null) {
                    sdf = if (dataS!!.length == 8) {
                        SimpleDateFormat("dd.MM.yy")
                    } else {
                        SimpleDateFormat("dd.MM.yyy")
                    }
                    val d1 = sdf.parse(date_of_IssueS)
                    sdf.applyPattern("yyyy-MM-dd")

                } else {

                }

                // Тип mrz
                val mrzS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_TYPE)
                if (mrzS != null) {
                }

                // Строки mrz
                val MRZStringsS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS)
                if (MRZStringsS != null) {
                }

                // Дополонительные данные
                val optionalDataS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_OPTIONAL_DATA)
                if (optionalDataS != null) {
                }

                // Контрольная цифра номера документа
                val documentNumberCheckS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER_CHECK_DIGIT)
                if (documentNumberCheckS != null) {
                }

                // Контрольная цифра даты рождения
                val dateOfBirthS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH_CHECK_DIGIT)
                if (dateOfBirthS != null) {
                }

                // Общая контрольная цифра
                val finalCheckDigitS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_FINAL_CHECK_DIGIT)
                if (finalCheckDigitS != null) {
                }

                // Линея 2 дополниотельные данные
                val line2OptionalDataS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_LINE_2_OPTIONAL_DATA)
                if (line2OptionalDataS != null) {
                }

                // Лет с мамента выпуска
                val yearsSinceIssueS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_YEARS_SINCE_ISSUE)
                if (yearsSinceIssueS != null) {
                }

                // Нацанальность (Нац)
                val nationalityS = results.getTextFieldValueByType(
                    eVisualFieldType.FT_NATIONALITY,
                    LCID.KYRGYZ_CYRILICK
                )
                if (nationalityS != null) {
                }

                // Контрольная цифра даты окончания действия
                val expiryCheckDigitS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_EXPIRY_CHECK_DIGIT)
                if (expiryCheckDigitS != null) {
                }

                // Код национальности
                val nationalityCodeS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_NATIONALITY_CODE)
                if (nationalityCodeS != null) {
                }

                // Код национальности (Нац)
                val placeOfIssueS = results.getTextFieldValueByType(
                    eVisualFieldType.FT_PLACE_OF_ISSUE,
                    LCID.KYRGYZ_CYRILICK
                )
                if (placeOfIssueS != null) {
                    if (authorityS == null) {

                    } else {

                    }
                }

                // Адрес
                val addressS = results.getTextFieldValueByType(
                    eVisualFieldType.FT_ADDRESS,
                    LCID.KYRGYZ_CYRILICK
                )
                if (addressS != null) {
                }

                // Тип документа
                val documentClassNameS = results.documentType[0].name
                if (documentClassNameS != null) {
                }

                // through all text fields
                if (results.textResult != null && results.textResult!!.fields != null) {
                    for (textField in results.textResult!!.fields) {
                        val value =
                            results.getTextFieldValueByType(textField.fieldType, textField.lcid)
                        Log.d("MainActivity", """$value""".trimIndent())
                    }
                }
                portrait = results.getGraphicFieldImageByType(eGraphicFieldType.GF_PORTRAIT)
                if (portrait != null) {

                }

                documentImage = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE)
                if (documentImage != null) {
                    val aspectRatio = documentImage!!.width.toDouble() / documentImage!!.height.toDouble()
                    documentImage = Bitmap.createScaledBitmap(documentImage!!, (480 * aspectRatio).toInt(), 480, false)
                    russian_patent.setImageBitmap(documentImage)
                }

                documentImageTwo = results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE, eRPRM_ResultType.RPRM_RESULT_TYPE_RAW_IMAGE, 1)
                if (documentImageTwo != null) {
                    val aspectRatio = documentImageTwo!!.width.toDouble() / documentImageTwo!!.height.toDouble()
                    documentImage = Bitmap.createScaledBitmap(documentImage!!, (480 * aspectRatio).toInt(), 480, false)
                    russian_patent.setImageBitmap(documentImageTwo)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    // loads bitmap from uri
    private fun getBitmap(selectedImage: Uri, targetWidth: Int, targetHeight: Int): Bitmap? {
        val resolver = requireActivity().contentResolver
        var `is`: InputStream? = null
        try {
            `is` = resolver.openInputStream(selectedImage)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(`is`, null, options)

        //Re-reading the input stream to move it's pointer to start
        try {
            `is` = resolver.openInputStream(selectedImage)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(`is`, null, options)
    }

    // see https://developer.android.com/topic/performance/graphics/load-bitmap.html
    private fun calculateInSampleSize(options: BitmapFactory.Options, bitmapWidth: Int, bitmapHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > bitmapHeight || width > bitmapWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > bitmapHeight
                && halfWidth / inSampleSize > bitmapWidth
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
