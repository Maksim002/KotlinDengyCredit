package com.example.kotlincashloan.ui.loans.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.media.ExifInterface
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
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.adapter.loans.StepClickListener
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.extension.changeImage
import com.example.kotlincashloan.extension.editUtils
import com.example.kotlincashloan.extension.errorImageRus
import com.example.kotlincashloan.extension.getListsFifth
import com.example.kotlincashloan.service.model.Loans.EntryGoalResultModel
import com.example.kotlincashloan.service.model.Loans.MyDataListModel
import com.example.kotlincashloan.service.model.Loans.MyImageModel
import com.example.kotlincashloan.service.model.Loans.TypeContractResultModel
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.service.model.profile.GetLoanModel
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.loans.SharedViewModel
import com.example.kotlincashloan.ui.loans.fragment.dialogue.StepBottomFragment
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
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
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_get_loan.*
import kotlinx.android.synthetic.main.fragment_loan_step_fifth.*
import kotlinx.android.synthetic.main.fragment_loan_step_six.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LoanStepFifthFragment(var statusValue: Boolean, var mitmap: HashMap<String, Bitmap>, var listLoan: GetLoanModel) : Fragment(), ListenerGeneralResult, DatePickerDialog.OnDateSetListener, StepClickListener {
    private var viewModel = LoansViewModel()
    private var imageViewModel = SharedViewModel()
    private var imageMap = HashMap<String, Bitmap>()
    private val IMAGE_PICK_CODE = 10
    private val CAMERA_PERM_CODE = 101
    private val REQUEST_BROWSE_PICTURE = 11
    private lateinit var currentPhotoPath: String
    private val imageString = HashMap<String, MyImageModel>()
    private var imageKey = ""
    private val doRfid = false
    private var documentImageTwo: Bitmap? = null
    private var portrait: Bitmap? = null
    private var documentImage: Bitmap? = null
    private val onCancel: DatePickerDialog.OnDateCancelListener? = null
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var data: String = ""
    private var keyData = ""
    private var countriesPhone = ""
    private var goalPhone = ""
    private var countriesList: ArrayList<MyDataListModel> = arrayListOf()
    private var goalList: ArrayList<MyDataListModel> = arrayListOf()
    private lateinit var calendar: GregorianCalendar
    private var hidingLayout = ""
    private var thread: Thread? = null
    private var listEntryError = ""
    private var listContractError = ""
    private var goalPosition = ""
    private var goalId = 0
    private var contractPosition = ""
    private var contractTypeId = 0
    private var imageScanId = ""
    private var textImA = ""
    private var textImB = ""
    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listEntryGoal: ArrayList<EntryGoalResultModel> = arrayListOf()
    private var listTypeContract: ArrayList<TypeContractResultModel> = arrayListOf()
    private var russianFederationA = false
    private var migrationCardA = false
    private var migrationCardB = false
    private var russianPatentA = false
    private var russianPatentB = false
    private var receiptPatent = false
    private var workPermitA = false
    private var workPermitB = false
    private var photo2NDFL = false
    private var lastPageOne = false
    private var lastPageTwo = false
    private var saveValidate = false
    private val mapSave = mutableMapOf<String, String>()
    private lateinit var idImage: ImageView
    private lateinit var idImageIc: ImageView

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
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        initRestart()
        initClick()
        iniData()
        initHidingFields()
        initTextValidation()
        initView()
        getLists()
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            six_ste_no_connection.visibility = View.VISIBLE
            layout_loan_six.visibility = View.GONE
            six_ste_technical_work.visibility = View.GONE
            six_ste_access_restricted.visibility = View.GONE
            six_ste_not_found.visibility = View.GONE
        } else {
            viewModel.errorSaveLoan.value = null
            initListEntryGoal()
            initTypeContract()
            if (fifth_potent.visibility != View.GONE) {
                val runnable = Runnable {
                    initDocumentReader()
                    thread!!.interrupt()
                }
                thread = Thread(runnable)
                thread!!.start()
            }
        }
    }

    //Получает данные на редактирование заёма
    private fun getLists() {
        if (statusValue) {
            fifth_cross_six.visibility = View.GONE
            if (mitmap.values != null) {
                russianFederationA = true
                migrationCardA = true
                migrationCardB = true
                russianPatentA = true
                russianPatentB = true
                receiptPatent = true
                workPermitA = true
                workPermitB = true
                photo2NDFL = true
                lastPageOne = true
                lastPageTwo = true
                getListsFifth(mitmap, russian_federationA, federationA_add_im,  imageViewModel,requireActivity(),"russian_federationA","reg_img_1")
                getListsFifth(mitmap, russian_federationB, federationB_add_im,  imageViewModel,requireActivity(),"russian_federationB","reg_img_2")
                getListsFifth(mitmap, migration_cardA, cardA_add_im,  imageViewModel,requireActivity(),"migration_cardA","entry_img_1")
                getListsFifth(mitmap, migration_cardB, cardB_add_im,  imageViewModel,requireActivity(),"migration_cardB","entry_img_2")
                getListsFifth(mitmap, receipt_patent, receipt_patent,  imageViewModel,requireActivity(),"receipt_patent","patent_invoice_img")
                getListsFifth(mitmap, work_permitA, permitA_add_im,  imageViewModel,requireActivity(),"work_permitA","work_permit_img_1")
                getListsFifth(mitmap, work_permitB, permitB_add_im,  imageViewModel,requireActivity(),"work_permitB","work_permit_img_2")
                getListsFifth(mitmap, photo_2NDFL, NDFL_add_im,  imageViewModel,requireActivity(),"photo_2NDFL","ndfl2_img")
                getListsFifth(mitmap, last_page_one, page_one_add_im,  imageViewModel,requireActivity(),"last_page_one","contract_img_1")
                getListsFifth(mitmap, last_page_two, page_two_add_im,  imageViewModel,requireActivity(),"last_page_two","contract_img_2")
                getListsFifth(mitmap, photo_RVP, RVP_add_im, imageViewModel,requireActivity(),"photo_RVP","rvp_img")
                getListsFifth(mitmap, photo_VNJ, VNJ_add_im, imageViewModel,requireActivity(),"photo_VNJ","vnzh_img")
                getListsText()
                imageViewModel.updateBitmaps(mitmap)
                mitmap.clear()
            }
        } else {
            dataImage()
        }
    }

    //Метод получает текствоые данные
    private fun getListsText() {
        if (statusValue){
        try {
            fifth_goal_name.text = MyUtils.toMyDate(listLoan.regDate!!)
            list_countries.setText( listEntryGoal.first { it.id == listLoan.entryGoal }.name.toString())
            goalPosition = listEntryGoal.first { it.id == listLoan.entryGoal }.name.toString()
            goalId = listLoan.entryGoal!!.toInt()
            date_entry.text = MyUtils.toMyDate(listLoan.entryDate!!)
            contract_type.setText(listTypeContract.first { it.id == listLoan.typeContract }.name.toString())
            contractPosition = listTypeContract.first { it.id == listLoan.typeContract }.name.toString()
            contractTypeId = listLoan.typeContract!!.toInt()
            contract_type.hint = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

override fun onStart() {
    super.onStart()
    if (fifth_goal_name.text.isNotEmpty()) {
        fifth_goal_name.hint = null
    }
    if (contract_type.text.isNotEmpty()) {
        contract_type.hint = null
    }
    initRestart()
}

    //Сохронение картинки на сервер
    private fun initSaveImage() {
        val mapImage = mutableMapOf<String, String>()
        mapImage["login"] = AppPreferences.login.toString()
        mapImage["token"] = AppPreferences.token.toString()

        if (statusValue == true){
            mapImage["id"] = listLoan.id.toString()
        }else{
            mapImage["id"] = AppPreferences.idApplications.toString()
        }
        mapImage["step"] = "0"
        mapImage["patent_img_1"] = textImA
        mapImage["patent_img_2"] = textImB

        if (imageString.size != 0) {
            if (imageString.containsKey("russian_federationA")) {
                mapImage["reg_img_1"] = imageString["russian_federationA"]!!.string
            }
            if (imageString.containsKey("russian_federationB")) {
                mapImage["reg_img_2"] = imageString["russian_federationB"]!!.string
            }
            if (imageString.containsKey("migration_cardA")) {
                mapImage["entry_img_1"] = imageString["migration_cardA"]!!.string
            }
            if (imageString.containsKey("migration_cardB")) {
                mapImage["entry_img_2"] = imageString["migration_cardB"]!!.string
            }
            if (imageString.containsKey("receipt_patent")) {
                mapImage["patent_invoice_img"] = imageString["receipt_patent"]!!.string
            }
            if (imageString.containsKey("work_permitA")) {
                mapImage["work_permit_img_1"] = imageString["work_permitA"]!!.string
            }
            if (imageString.containsKey("work_permitB")) {
                mapImage["work_permit_img_2"] = imageString["work_permitB"]!!.string
            }
            if (imageString.containsKey("photo_2NDFL")) {
                mapImage["ndfl2_img"] = imageString["photo_2NDFL"]!!.string
            }
            if (imageString.containsKey("last_page_one")) {
                mapImage["contract_img_1"] = imageString["last_page_one"]!!.string
            }
            if (imageString.containsKey("last_page_two")) {
                mapImage["contract_img_2"] = imageString["last_page_two"]!!.string
            }
            if (imageString.containsKey("photo_RVP")) {
                mapImage["rvp_img"] = imageString["photo_RVP"]!!.string
            }
            if (imageString.containsKey("photo_VNJ")) {
                mapImage["vnzh_img"] = imageString["photo_VNJ"]!!.string
            }

            if (contractPosition == "") {
                mapImage["type_contract"] = ""
            } else {
                mapImage["type_contract"] = contractTypeId.toString()
            }
        }

        GetLoanActivity.alert.show()
        viewModel.saveLoanImg(mapImage)
        viewModel.getSaveLoanImg.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                textImA = ""
                textImB = ""
            } else if (result.reject != null) {
                initBottomSheetError(result.reject.message.toString())
            } else {
                if (result.error.code == 409) {
                    Toast.makeText(requireContext(), "Отсканируйте документ повторно", Toast.LENGTH_LONG).show()
                } else {
                    listResult(result.error.code!!)
                    errorImageRus(idImage,idImageIc,imageString,imageKey, requireActivity())
                }
            }
            imageString.clear()
        })

        viewModel.errorSaveLoanImg.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                if (error == "409") { Toast.makeText(requireContext(), "Отсканируйте документ повторно", Toast.LENGTH_LONG).show()
                } else {
                    errorList(error)
                    errorImageRus(idImage,idImageIc,imageString,imageKey, requireActivity())
                }
            }
            imageString.clear()
        })
    }

    // TODO: 21-2-8 Сохронение доверительные контакты.
    private fun initSaveServer() {
        GetLoanActivity.alert.show()
        mapSave["login"] = AppPreferences.login.toString()
        mapSave["token"] = AppPreferences.token.toString()
        mapSave["id"] = AppPreferences.idApplications.toString()
        if (countriesPhone == ""){
            mapSave["reg_date"] = fifth_goal_name.text.toString()
        }else{
            mapSave["reg_date"] = countriesPhone
        }
        if (goalPhone == ""){
            mapSave["entry_date"] = date_entry.text.toString()
        }else{
            mapSave["entry_date"] = goalPhone
        }

        if (goalPosition == ""){
            mapSave["entry_goal"] = ""
        }else{
            mapSave["entry_goal"] = goalId.toString()
        }
        mapSave["step"] = "5"

        viewModel.saveLoans(mapSave).observe(viewLifecycleOwner, Observer { result ->
            val data = result.data
            val msg = result.msg
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        layout_fifth.visibility = View.VISIBLE
                        fifth_ste_technical_work.visibility = View.GONE
                        fifth_ste_no_connection.visibility = View.GONE
                        fifth_ste_access_restricted.visibility = View.GONE
                        fifth_ste_not_found.visibility = View.GONE
                        if (saveValidate) {
                            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.GONE
                            if (statusValue == true){
                                requireActivity().onBackPressed()
                            }else{
                                (activity as GetLoanActivity?)!!.get_loan_view_pagers.currentItem = 7
                            }
                        }
                    } else if (data.error.code != null) {
                        listResult(data.error.code!!)
                    } else if (data.reject != null) {
                        initBottomSheetError(data.reject.message.toString())
                    }
                }
                Status.ERROR -> {
                    errorList(msg!!)
                }
                Status.NETWORK -> {
                    errorList(msg!!)
                }
            }
            GetLoanActivity.alert.hide()
        })
    }

    //Скрытие полей
    private fun initHidingFields() {
//        hidingLayout = AppPreferences.nationality.toString()
        hidingLayout = "KGZ"

        if (hidingLayout == "UZB" || hidingLayout == "TJK") {
            fifth_potent.visibility = View.VISIBLE
            fifth_receipt.visibility = View.VISIBLE
            fifth_permission.visibility = View.VISIBLE
            fifth_2n.visibility = View.GONE
            layout_contract_type.visibility = View.GONE
            fifth_contract.visibility = View.GONE
        } else if (hidingLayout == "RUS") {
            fifth_2n.visibility = View.VISIBLE
            fifth_potent.visibility = View.VISIBLE
            fifth_receipt.visibility = View.VISIBLE
            fifth_permission.visibility = View.VISIBLE
            layout_contract_type.visibility = View.GONE
            fifth_contract.visibility = View.GONE
        } else if (hidingLayout == "KGZ") {
            contract_type.visibility = View.VISIBLE
            fifth_contract.visibility = View.VISIBLE
            fifth_potent.visibility = View.GONE
            fifth_receipt.visibility = View.GONE
            fifth_permission.visibility = View.GONE
            fifth_2n.visibility = View.GONE
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
                getListsText()
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
                listContractError = result.code.toString()
                getResultOk()
                listTypeContract = result.result
                getListsText()
            } else {
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListTypeContract.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                listContractError = error
                errorList(error)
            }
        })
    }

    private fun initClick() {

        access_restricted.setOnClickListener {
            initRestart()
        }

        no_connection_repeat.setOnClickListener {
            initRestart()
        }

        technical_work.setOnClickListener {
            initRestart()
        }

        not_found.setOnClickListener {
            initRestart()
        }

        bottom_loan_fifth.setOnClickListener {
            saveValidate = true
            if (validate()) {
                initSaveServer()
            }
        }

        fifth_cross_six.setOnClickListener {
            (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(5)
            hidingErrors()
        }

        list_countries.setOnClickListener {
            list_countries.isClickable = false
            list_countries.error = null
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listEntryGoal.size) {
                    if (i <= listEntryGoal.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listEntryGoal[i - 1].name.toString(),
                                "listEntryGoal",
                                i - 1,
                                listEntryGoal[i - 1].id!!.toInt(),
                                listEntryGoal[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, goalPosition, "Укажите цель въезда")
            }
        }

        contract_type.addTextChangedListener {
            fifth_contract.visibility = View.VISIBLE
        }


        contract_type.setOnClickListener {
            contract_type.isClickable = false
            contract_type.error = null
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listTypeContract.size) {
                    if (i <= listTypeContract.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listTypeContract[i - 1].name.toString(),
                                "listTypeContract",
                                i - 1,
                                listTypeContract[i - 1].id!!.toInt(),
                                listTypeContract[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(
                    itemDialog,
                    contractPosition,
                    "Выберите тип вашего трудового договора"
                )
            }
        }

        russian_federationA.setOnClickListener {
            imageKey = "russian_federationA"
            loadFiles()
        }

        russian_federationB.setOnClickListener {
            imageKey = "russian_federationB"
            loadFiles()
        }


        migration_cardA.setOnClickListener {
            imageKey = "migration_cardA"
            loadFiles()
        }

        migration_cardB.setOnClickListener {
            imageKey = "migration_cardB"
            loadFiles()
        }

        receipt_patent.setOnClickListener {
            imageKey = "receipt_patent"
            loadFiles()
        }

        work_permitA.setOnClickListener {
            imageKey = "work_permitA"
            loadFiles()
        }

        work_permitB.setOnClickListener {
            imageKey = "work_permitB"
            loadFiles()
        }

        photo_2NDFL.setOnClickListener {
            imageKey = "photo_2NDFL"
            loadFiles()
        }

        last_page_one.setOnClickListener {
            lastPageOne = true
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
            list_countries.isClickable = true
            list_countries.setText(listEntryGoal[model.position].name)
            goalPosition = listEntryGoal[model.position].name.toString()
            goalId = model.id!!
            list_countries.error = null
        }

        if (model.key == "listTypeContract") {
            contract_type.isClickable = true
            contract_type.setText(listTypeContract[model.position].name)
            contractPosition = listTypeContract[model.position].name.toString()
            contractTypeId = model.id!!
            contract_type.error = null
            contract_type.hint = null
        }
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    private fun initTextValidation() {
        if (contract_type.text.isEmpty()) {
            fifth_contract.visibility = View.GONE
        }
    }

    //Диалоговое окно с отоброжениеем ошибки reject
    private fun initBottomSheetError(message: String) {
        val stepBottomFragment = StepBottomFragment(this, message)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(
        list: ArrayList<GeneralDialogModel>,
        selectionPosition: String,
        title: String
    ) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title)
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    //Метод выгружает картинку с памяти телефона
    private fun loadFiles() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
        val dtoregDirectiry: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try {
            val files = File.createTempFile(file, ".jpg", dtoregDirectiry)
            currentPhotoPath = files.absolutePath
            val imagUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.kotlincashloan",
                files
            )
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
                var rotatedBitmap: Bitmap? = null
                val imageBitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val nh = (imageBitmap.height * (512.0 / imageBitmap.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(imageBitmap, 512, nh, true)
                val ei = ExifInterface(currentPhotoPath)
                val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(scaled, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(scaled, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(scaled, 270F)
                    ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = scaled
                    else -> rotatedBitmap = scaled
                }
                imageConverter(rotatedBitmap!!)
                if (imageKey == "russian_federationA") {
                    russian_federationA.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.russian_federationA)
                    idImageIc = requireView().findViewById(R.id.federationA_add_im)
                    russianFederationA = true
                    fifth_incorrectA.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "russian_federationB") {
                    russian_federationB.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.russian_federationB)
                    idImageIc = requireView().findViewById(R.id.federationB_add_im)
                    fifth_incorrectA.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "migration_cardA") {
                    migration_cardA.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.migration_cardA)
                    idImageIc = requireView().findViewById(R.id.cardA_add_im)
                    migrationCardA = true
                    fifth_incorrect_card.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "migration_cardB") {
                    migration_cardB.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.migration_cardB)
                    idImageIc = requireView().findViewById(R.id.cardB_add_im)
                    migrationCardB = true
                    fifth_incorrect_card.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "receipt_patent") {
                    receipt_patent.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.receipt_patent)
                    idImageIc = requireView().findViewById(R.id.patent_add_im)
                    receiptPatent = true
                    fifth_incorrect_receipt.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "work_permitA") {
                    work_permitA.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.work_permitA)
                    idImageIc = requireView().findViewById(R.id.permitA_add_im)
                    workPermitA = true
                    fifth_incorrect_work.visibility = View.GONE
                    fifth_potent.visibility = View.GONE
                    fifth_receipt.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "work_permitB") {
                    work_permitB.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.work_permitB)
                    idImageIc = requireView().findViewById(R.id.permitB_add_im)
                    workPermitB = true
                    fifth_incorrect_work.visibility = View.GONE
                    fifth_potent.visibility = View.GONE
                    fifth_receipt.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "photo_2NDFL") {
                    photo_2NDFL.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.photo_2NDFL)
                    idImageIc = requireView().findViewById(R.id.NDFL_add_im)
                    photo2NDFL = true
                    fifth_incorrect_2NDFL.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "last_page_one") {
                    last_page_one.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.last_page_one)
                    idImageIc = requireView().findViewById(R.id.page_one_add_im)
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                    fifth_incorrect_page.visibility = View.GONE
                } else if (imageKey == "last_page_two") {
                    last_page_two.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.last_page_two)
                    idImageIc = requireView().findViewById(R.id.page_two_add_im)
                    lastPageTwo = true
                    fifth_incorrect_page.visibility = View.GONE
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "photo_RVP") {
                    photo_RVP.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.photo_RVP)
                    idImageIc = requireView().findViewById(R.id.RVP_add_im)
                    imageViewModel.updateKey(imageKey)
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                } else if (imageKey == "photo_VNJ") {
                    photo_VNJ.setImageBitmap(rotatedBitmap)
                    idImage = requireView().findViewById(R.id.photo_VNJ)
                    idImageIc = requireView().findViewById(R.id.VNJ_add_im)
                    //Приниет ключ для проверки
                    imageViewModel.updateKey(imageKey)
                    // Принимает ключ и зоброжения и картинку
                    imageMap.put(imageKey, rotatedBitmap)
                    imageKey = ""
                }
                imageViewModel.updateBitmaps(imageMap)
                initSaveImage()
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

    //Делает картинку с камеры вертикальной
    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    //Сохроняте картинки во ViewModel
    private fun dataImage(){
        imageViewModel.getBitmaps().observe(viewLifecycleOwner, Observer { images ->
            russian_federationA.setImageBitmap(images["russian_federationA"])
            if (images.containsKey("russian_federationA")) {
                //Метод при немает id и сравнивет прикреплтно ли изоброжение true , false
                changeImage(federationA_add_im, true, requireActivity())
            }
            russian_federationB.setImageBitmap(images["russian_federationB"])
            if (images.containsKey("russian_federationB")) {
                changeImage(federationB_add_im, true, requireActivity())
            }
            migration_cardA.setImageBitmap(images["migration_cardA"])
            if (images.containsKey("migration_cardA")) {
                changeImage(cardA_add_im, true, requireActivity())
            }
            migration_cardB.setImageBitmap(images["migration_cardB"])
            if (images.containsKey("migration_cardB")) {
                changeImage(cardB_add_im, true, requireActivity())
            }
            receipt_patent.setImageBitmap(images["receipt_patent"])
            if (images.containsKey("receipt_patent")) {
                changeImage(patent_add_im, true, requireActivity())
            }
            work_permitA.setImageBitmap(images["work_permitA"])
            if (images.containsKey("work_permitA")) {
                changeImage(permitA_add_im, true, requireActivity())
            }
            work_permitB.setImageBitmap(images["work_permitB"])
            if (images.containsKey("work_permitB")) {
                changeImage(permitB_add_im, true, requireActivity())
            }
            photo_2NDFL.setImageBitmap(images["photo_2NDFL"])
            if (images.containsKey("photo_2NDFL")) {
                changeImage(NDFL_add_im, true, requireActivity())
            }
            last_page_one.setImageBitmap(images["last_page_one"])
            if (images.containsKey("last_page_one")) {
                changeImage(page_one_add_im, true, requireActivity())
            }
            last_page_two.setImageBitmap(images["last_page_two"])
            if (images.containsKey("last_page_two")) {
                changeImage(page_two_add_im, true, requireActivity())
            }
            photo_RVP.setImageBitmap(images["photo_RVP"])
            if (images.containsKey("photo_RVP")) {
                changeImage(RVP_add_im, true, requireActivity())
            }
            photo_VNJ.setImageBitmap(images["photo_VNJ"])
            if (images.containsKey("photo_VNJ")) {
                changeImage(VNJ_add_im, true, requireActivity())
            }
        })
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
        if (countriesList.size != 0) {
            if (countriesList[0].key == "fifth_goal_name") {
                val calendarDta = GregorianCalendar(
                    countriesList[0].year,
                    countriesList[0].monthOfYear,
                    countriesList[0].dayOfMonth
                )
                val calendar = simpleDateFormat.format(calendarDta.time)
                fifth_goal_name.setText(MyUtils.toMyDate(calendar))
                countriesPhone = calendar
            }
        }


        fifth_goal_name.setOnClickListener(View.OnClickListener { v: View? ->
            fifth_goal_name.error = null
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
                val calendarDta = GregorianCalendar(1990, 0, 1)
                val calendar = simpleDateFormat.format(calendarDta.time)
                countriesPhone = calendar
            }
        })

        if (goalList.size != 0) {
            if (goalList[0].key == "date_entry") {
                val calendarDta = GregorianCalendar(
                    goalList[0].year,
                    goalList[0].monthOfYear,
                    goalList[0].dayOfMonth
                )
                val calendar = simpleDateFormat.format(calendarDta.time)
                date_entry.setText(MyUtils.toMyDate(calendar))
                goalPhone = calendar
            }
        }

        date_entry.setOnClickListener {
            date_entry.error = null
            keyData = "date_entry"
            if (goalList.size != 0) {
                showDate(
                    goalList[0].year,
                    goalList[0].monthOfYear,
                    goalList[0].dayOfMonth,
                    R.style.DatePickerSpinner
                )
            } else {
                showDate(1990, 0, 1, R.style.DatePickerSpinner)
                val calendarDta = GregorianCalendar(1990, 0, 1)
                val calendar = simpleDateFormat.format(calendarDta.time)
                goalPhone = calendar
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
                        val goal = simpleDateFormat.format(calendar.time)
                        fifth_goal_name.setText(MyUtils.toMyDate(goal))
                        countriesPhone = goal
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
                val goal = simpleDateFormat.format(calendar.time)
                fifth_goal_name.setText(MyUtils.toMyDate(goal))
                countriesList.add(MyDataListModel("fifth_goal_name", year, monthOfYear, dayOfMonth))
            }
            fifth_goal_name.hint = null
        } else if (keyData == "date_entry") {
            if (goalList.size != 0) {
                for (i in 1..goalList.size) {
                    while (goalList[i - 1].key == "date_entry") {
                        goalList.removeAt(i - 1)
                        val entry = simpleDateFormat.format(calendar.time)
                        date_entry.setText(MyUtils.toMyDate(entry))
                        goalPhone = entry
                        goalList.add(MyDataListModel("date_entry", year, monthOfYear, dayOfMonth))
                        break
                    }
                }
            } else {
                val entry = simpleDateFormat.format(calendar.time)
                date_entry.setText(MyUtils.toMyDate(entry))
                goalList.add(MyDataListModel("date_entry", year, monthOfYear, dayOfMonth))
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
                text.setText("Ожидайте, идет загрузка...")
                fifth_potent.setClickable(false)


                //Reading the license from raw resource file
                try {
                    val licInput = resources.openRawResource(R.raw.regula)
                    val available = licInput.available()
                    val license = ByteArray(available)
                    licInput.read(license)


                    //preparing database files, it will be downloaded from network only one time and stored on user device
                    DocumentReader.Instance().prepareDatabase(requireContext(), "Full", object : IDocumentReaderPrepareCompletion {
                            override fun onPrepareProgressChanged(progress: Int) {
                                try {
                                    text.setText("Загрузка базы данных: $progress%")
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }
                            override fun onPrepareCompleted(p0: Boolean, p1: DocumentReaderException?) {
                                //Initializing the reader
                                DocumentReader.Instance().initializeReader(
                                    requireContext(),
                                    license
                                ) { success, error_initializeReader ->
                                    //добавил трай что бы непадал
                                    try {
                                        text.setText("Фото патента РФ:")
                                        fifth_potent.isClickable = true
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    DocumentReader.Instance().customization().edit()
                                        .setShowHelpAnimation(
                                            false
                                        ).apply()
                                    DocumentReader.Instance().functionality().edit()
                                        .setShowSkipNextPageButton(
                                            false
                                        ).apply()
                                    if (success) {
                                        //initialization successful
                                        fifth_potent.setOnClickListener(View.OnClickListener {
                                            //starting video processing
                                            DocumentReader.Instance().showScanner(
                                                requireContext(),
                                                completion
                                            )
                                            DocumentReader.Instance()
                                                .processParams().multipageProcessing = true
                                        })

                                        //getting current processing scenario and loading available scenarios to ListView
                                        var currentScenario =
                                            DocumentReader.Instance().processParams().scenario
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
                                        //добавил трай что бы непадал
                                        try {
                                            //Initialization was not successful
                                            Toast.makeText(
                                                requireContext(),
                                                "Init failed:$p1",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                }
                            }
                        })
                    licInput.close()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else {
                fifth_potent.setOnClickListener(View.OnClickListener {
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
                    mapSave["patent_last_name"] = surnameS
                } else {
                    mapSave["patent_last_name"] = ""
                }

                //Имя
                val namesS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_GIVEN_NAMES)
                if (namesS != null) {
                    mapSave["patent_first_name"] = namesS
                } else {
                    mapSave["patent_first_name"] = ""
                }

                //Отчество (Нац)
                val nameNationalityS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_FATHERS_NAME, LCID.RUSSIAN)
                if (nameNationalityS != null) {
                    mapSave["patent_second_name"] = nameNationalityS
                } else {
                    mapSave["patent_second_name"] = ""
                }

                //Дата рождения
                val dataS = results.getTextFieldValueByType(eVisualFieldType.FT_DATE_OF_BIRTH)
                var sdf: SimpleDateFormat? = null
                if (dataS != null) {
                    sdf = if (dataS.length == 8) {
                        SimpleDateFormat("dd.MM.yy")
                    } else {
                        SimpleDateFormat("dd.MM.yyy")
                    }
                    val d1 = sdf.parse(dataS)
                    sdf.applyPattern("yyyy-MM-dd")
                    mapSave["patent_u_date"] = sdf.format(d1)
                } else {
                    mapSave["patent_u_date"] = ""
                }

                //Код государтсва выдочи
                val placeCodeS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_ISSUING_STATE_CODE)
                if (placeCodeS != null) {
                    mapSave["patent_code_issue"] = placeCodeS
                } else {
                    mapSave["patent_code_issue"] = ""
                }

                //номер документа
                val documentNumberS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_DOCUMENT_NUMBER)
                if (documentNumberS != null) {
                    mapSave["patent_number"] = documentNumberS
                } else {
                    mapSave["patent_number"] = ""
                }

                // Регистрационный номер
                val registrationNumberS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_REG_CERT_REG_NUMBER)
                if (registrationNumberS != null) {
                    mapSave["patent_reg_number"] = registrationNumberS
                } else {
                    mapSave["patent_reg_number"] = ""
                }

                //Серия номер документа
                val seriesDocumentS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_SERIAL_NUMBER)
                if (seriesDocumentS != null) {
                    mapSave["patent_series"] = seriesDocumentS
                } else {
                    mapSave["patent_series"] = ""
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
                    mapSave["patent_authority"] = authorityS
                } else {
                    mapSave["patent_authority"] = ""
                }

                // Территориальное действие
                val territorialS =
                    results.getTextFieldValueByType(eVisualFieldType.FT_TERRITORIAL_VALIDITY)
                if (territorialS != null) {
                    mapSave["patent_territory"] = territorialS
                } else {
                    mapSave["patent_territory"] = ""
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
                    mapSave["patent_date_issue"] = sdf.format(d1)
                } else {
                    mapSave["patent_date_issue"] = ""
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
                    imageScanId = "imageA"
                    val aspectRatio = documentImage!!.width.toDouble() / documentImage!!.height.toDouble()
                    documentImage = Bitmap.createScaledBitmap(
                        documentImage!!,
                        (1200 * aspectRatio).toInt(),
                        1200,
                        false
                    )
                    fifth_incorrect_patent.visibility = View.GONE
                    fifth_permission.visibility = View.GONE
                    russianPatentA = true
                    changeImage(patentA_add_im, true, requireActivity())
                    russian_patentA.setImageBitmap(documentImage)
                    gotImageString(documentImage!!)
                }

                documentImageTwo = results.getGraphicFieldImageByType(
                    eGraphicFieldType.GF_DOCUMENT_IMAGE,
                    eRPRM_ResultType.RPRM_RESULT_TYPE_RAW_IMAGE,
                    1
                )
                if (documentImageTwo != null) {
                    imageScanId = "imageB"
                    val aspectRatio =
                        documentImageTwo!!.width.toDouble() / documentImageTwo!!.height.toDouble()
                    documentImageTwo = Bitmap.createScaledBitmap(
                        documentImageTwo!!, (1200 * aspectRatio).toInt(), 1200, false
                    )
                    russianPatentB = true
                    changeImage(patentB_add_im, true, requireActivity())
                    russian_patentB.setImageBitmap(documentImageTwo)
                    gotImageString(documentImageTwo!!)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    //encode image to base64 string
    private fun gotImageString(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        if (imageScanId == "imageA") {
            textImA = imageString
        } else if (imageScanId == "imageB") {
            textImB = imageString
        }

        if (textImA != "" && textImB != "") {
            initSaveImage()
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
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        bitmapWidth: Int,
        bitmapHeight: Int
    ): Int {
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

    private fun validate(): Boolean {
        var valid = true

        if (!russianFederationA) {
            fifth_incorrectA.text = "Добавьте фото документа"
            fifth_incorrectA.visibility = View.VISIBLE
            valid = false
        } else {
            fifth_incorrectA.visibility = View.GONE
        }

        if (fifth_goal_name.text.isEmpty()) {
            editUtils(fifth_goal_name, fifth_goal_name_error, "Заполните поле", true, null)
            valid = false
        }

        if (!migrationCardA || !migrationCardB) {
            fifth_incorrect_card.text = "Добавьте фото документа"
            fifth_incorrect_card.visibility = View.VISIBLE
            valid = false
        } else {
            fifth_incorrect_card.visibility = View.GONE
        }

        if (list_countries.text.isEmpty()) {
            editUtils(list_countries, list_countries_error, "Выберите из списка", true, null)
            valid = false
        }

        if (date_entry.text.isEmpty()) {
            editUtils(date_entry, date_entry_error, "Выберите дату", true, null)
            valid = false
        }


        if (fifth_potent.visibility != View.GONE) {
            if (!russianPatentA || !russianPatentB) {
                fifth_incorrect_patent.text = "Добавьте фото документа"
                fifth_incorrect_patent.visibility = View.VISIBLE
                valid = false
            } else {
                fifth_incorrect_patent.visibility = View.GONE
            }
        }

        if (fifth_receipt.visibility != View.GONE) {
            if (!receiptPatent) {
                fifth_incorrect_receipt.text = "Добавьте фото документа"
                fifth_incorrect_receipt.visibility = View.VISIBLE
                valid = false
            } else {
                fifth_incorrect_receipt.visibility = View.GONE
            }
        }

        if (fifth_permission.visibility != View.GONE) {
            if (!workPermitA || !workPermitB) {
                fifth_incorrect_work.text = "Добавьте фото документа"
                fifth_incorrect_work.visibility = View.VISIBLE
                valid = false
            } else {
                fifth_incorrect_work.visibility = View.GONE
            }
        }

        if (fifth_2n.visibility != View.GONE) {
            if (!photo2NDFL) {
                fifth_incorrect_2NDFL.text = "Добавьте фото документа"
                fifth_incorrect_2NDFL.visibility = View.VISIBLE
                valid = false
            } else {
                fifth_incorrect_2NDFL.visibility = View.GONE
            }
        }

        if (contract_type.visibility != View.GONE) {
            if (contract_type.text.length == 0) {
                if (contract_type.text.isEmpty()) {
                    editUtils(contract_type, contract_type_error, "Выберите из списка", true, null)
                    valid = false
                }
            }
        }

        if (fifth_contract.visibility != View.GONE) {
            if (!lastPageOne || !lastPageTwo) {
                fifth_incorrect_page.text = "Добавьте фото документа"
                fifth_incorrect_page.visibility = View.VISIBLE
                valid = false
            } else {
                fifth_incorrect_page.visibility = View.GONE
            }
        }

        return valid
    }

    private fun initView(){
        fifth_goal_name.addTextChangedListener {
            editUtils(fifth_goal_name, fifth_goal_name_error, "", false, null)
        }

        list_countries.addTextChangedListener {
            editUtils(list_countries, list_countries_error, "", false, null)
        }

        date_entry.addTextChangedListener {
            editUtils(date_entry, date_entry_error, "", false, null)
        }
        contract_type.addTextChangedListener {
            editUtils(contract_type, contract_type_error, "", false, null)
        }
    }

    override fun onClickStepListener() {
        requireActivity().finish()
    }

    //проверяет если был откат назад отключает ошибки
    private fun hidingErrors(){
        editUtils(fifth_goal_name, fifth_goal_name_error, "", false, null)
        editUtils(list_countries, list_countries_error, "", false, null)
        editUtils(date_entry, date_entry_error, "", false, null)
        editUtils(contract_type, contract_type_error, "", false, null)
        fifth_incorrectA.visibility = View.GONE
        fifth_incorrect_card.visibility = View.GONE
        fifth_incorrect_patent.visibility = View.GONE
        fifth_incorrect_receipt.visibility = View.GONE
        fifth_incorrect_work.visibility = View.GONE
        fifth_incorrect_2NDFL.visibility = View.GONE
        fifth_incorrect_page.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        initTextValidation()
    }
}

