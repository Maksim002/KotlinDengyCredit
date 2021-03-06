package com.example.kotlincashloan.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.text.method.PasswordTransformationMethod
import android.util.Base64
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.cookiebar.CookieBar
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.extension.*
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.service.model.profile.ClientInfoResultModel
import com.example.kotlincashloan.service.model.profile.CounterNumResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.ui.registration.recovery.ContactingServiceActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.service.model.ListSecretQuestionResultModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.AppPreferences.toFullPhone
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import kotlinx.android.synthetic.main.fragment_profile_setting.home_forget_password
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileSettingFragment : Fragment(), ListenerGeneralResult {
    private val IMAGE_PICK_CODE = 10
    val CAMERA_PERM_CODE = 101
    private var viewModel = ProfileViewModel()
    private var errorCodeGender = ""
    private var errorCodeNationality = ""
    private var errorListAvailableCountry = ""
    private var errorListSecretQuestion = ""
    private var errorSaveProfile = ""
    private var errorClientInfo = ""
    private val handler = Handler()
    private var clientResult = ClientInfoResultModel()
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var list: ArrayList<CounterNumResultModel> = arrayListOf()
    private var listClientInfo = ClientInfoResultModel()
    private var codeNationality = 0
    private var numberAvailable = 0
    private var checkNumber = 0
    private var codeMack = ""
    private var reView = false
    private var reNum = ""
    private var profileSettingAnim = false
    private var profileSettingAnimR = false
    private var phoneSecondId = ""
    private var questionId = ""
    private var textPasswordOne = ""
    private var textPasswordTwo = ""
    private lateinit var bitmap: Bitmap
    private var imageString: String = ""
    private lateinit var currentPhotoPath: String
    private var questionPosition = ""
    private var countriesPosition = ""
    private lateinit var myThread: Thread
    private lateinit var dialog: AlertDialog
    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var question: ArrayList<ListSecretQuestionResultModel> = arrayListOf()
    private var countries: ArrayList<CounterNumResultModel> = arrayListOf()
    private var addImage = false
    private var passwordTrue = ""
    private val mapNationality = HashMap<String, String>()
    private val mapGender = HashMap<String, String>()
    private val mapRegistration = HashMap<String, String>()
    private val mapQuestion = HashMap<String, String>()
    private val mapInfo = HashMap<String, String>()
    private var genAnim = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ???????????????? ???????????????? ???????????????? ??????????????
        if (!profileSettingAnim) {
            //profileAnim ???????????????? ?????? ???????????????? ?? ???????????? ?????????????? ?? ????????????
            TransitionAnimation(activity as AppCompatActivity).transitionRight(profile_setting_anim)
            profileSettingAnim = true
        }

        initPreloader()
        //?????????? ????????
        simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        setTitle("??????????????", resources.getColor(R.color.whiteColor))
        initClick()
        initView()
        initArgument()
        iniImageToServer()
    }

    private fun initPreloader() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.alert_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initArgument() {
        val sendPicture = try {
            requireArguments().getString("sendPicture")
        } catch (e: Exception) {
            ""
        }

        val getAddImage = try {
            requireArguments().getBoolean("addImage")
        } catch (e: Exception) {
            false
        }

        if (sendPicture != null) {
            if (!addImage) {
                if (!getAddImage) {
                    Glide
                        .with(this)
                        .load(sendPicture)
                        .into(profile_setting_image);
                }
            }
        }

        listClientInfo = try {
            requireArguments().getSerializable("client") as ClientInfoResultModel
        } catch (e: Exception) {
            listClientInfo
        }
    }

    private fun initRestart() {

        mapNationality.put("login", clientResult.gender.toString())
        mapNationality.put("id", listClientInfo.nationality.toString())

        mapGender.put("login", clientResult.nationality.toString())
        mapGender.put("id", listClientInfo.gender.toString())

        if (phoneSecondId != "") {
            mapRegistration.put("id", phoneSecondId)
        } else {
            mapRegistration.put("id", listClientInfo.phoneSecond.toString())
        }

        if (questionId != "") {
            mapQuestion.put("id", questionId)
        } else {
            mapQuestion.put("id", listClientInfo.question.toString())
        }

        mapInfo.put("login", AppPreferences.login.toString())
        mapInfo.put("token", AppPreferences.token.toString())
        //???????????????? ???? ????????????????
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
            viewModel.errorClientInfo.value = null
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            viewModel.errorListSecretQuestion.value = null
            viewModel.errorSaveProfile.value = null
        } else {
            if (viewModel.listGenderDta.value == null && viewModel.listGenderDta.value == null && viewModel.listNationalityDta.value == null
                && viewModel.listAvailableCountryDta.value == null && viewModel.listSecretQuestionDta.value == null && viewModel.listClientInfoDta.value == null
            ) {
                shimmer_profile_setting.startShimmerAnimation()
                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    clearingDate()
                    viewModel.refreshCode = false
                    viewModel.clientInfo(mapInfo)
                    viewModel.listGender(mapGender)
                    viewModel.getListNationality(mapNationality)
                    viewModel.listAvailableCountry(mapRegistration)
                    viewModel.listSecretQuestion(mapQuestion)
                    initResult()
                }, 500)
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    initResult()
                }, 500)
            }
        }
    }

    private fun initVisibilities() {
        shimmerStartProfile(shimmer_profile_setting, requireActivity())
        profile_s_access_restricted.visibility = View.GONE
        profile_s_no_connection.visibility = View.GONE
        profile_s_technical_work.visibility = View.GONE
        profile_s_not_found.visibility = View.GONE
        profile_setting.visibility = View.VISIBLE
    }

    private fun isRestart() {
        clearingDate()

        viewModel.clientInfo(mapInfo)
        viewModel.listGender(mapGender)
        viewModel.getListNationality(mapNationality)
        viewModel.listAvailableCountry(mapRegistration)
        viewModel.listSecretQuestion(mapRegistration)
        initResult()
    }

    private fun clearingDate() {
        errorCodeGender = ""
        errorCodeNationality = ""
        errorListAvailableCountry = ""
        errorListSecretQuestion = ""
        errorClientInfo = ""
        viewModel.errorListGender.value = null
        viewModel.listGenderDta.value = null
        viewModel.errorListNationality.value = null
        viewModel.listNationalityDta.value = null
        viewModel.errorListAvailableCountry.value = null
        viewModel.listAvailableCountryDta.value = null
        viewModel.errorListSecretQuestion.value = null
        viewModel.listSecretQuestionDta.value = null
        viewModel.listClientInfoDta.value = null
        viewModel.errorClientInfo.value = null
    }

    //?????????????????? ??????????
    private fun gettingFloors() {
        //???????????????? ???? ????????????????
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
            viewModel.errorClientInfo.value = null
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            viewModel.errorListSecretQuestion.value = null
            viewModel.errorSaveProfile.value = null
        } else {
            viewModel.listGenderDta.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { result ->
                    try {
                        if (result.result != null) {
                            profile_setting_gender.setText(result.result.first { it.id == clientResult.gender!!.toInt() }.name)
                            errorCodeGender = result.code.toString()
                            resultSuccessfully()
                        } else {
                            listListResult(result.error.code!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

            viewModel.errorListGender.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { error ->
                    if (error != null) {
                        errorCodeGender = error
                        errorList(error)
                    }
                })
        }
    }

    //?????????????????? ??????????????????????
    private fun obtainingCitizenship() {
        //???????????????? ???? ????????????????
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
            viewModel.errorClientInfo.value = null
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            viewModel.errorListSecretQuestion.value = null
            viewModel.errorSaveProfile.value = null
        } else {
            viewModel.listNationalityDta.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { result ->
                    try {
                        if (result.result != null) {
                            profile_s_nationality.setText(result.result.first { it.id == clientResult.nationality!!.toInt() }.name)
                            errorCodeNationality = result.code.toString()
                            resultSuccessfully()
                        } else {
                            listListResult(result.error.code!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

            viewModel.errorListNationality.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { error ->
                    try {
                        if (error != null) {
                            errorCodeNationality = error
                            errorList(error)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
        }
    }

    //  ???????????? ?????????????????? ??????????
    private fun listCountries() {
        //???????????????? ???? ????????????????
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
            viewModel.errorClientInfo.value = null
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            viewModel.errorListSecretQuestion.value = null
            viewModel.errorSaveProfile.value = null
        } else {
            viewModel.listAvailableCountryDta.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { result ->
                    try {
                        // ???????????? ??????????
                        if (result.result != null) {
                            //???????????????????????????? ????????????
                            countries = result.result
                            if (clientResult.phoneFirst != "") {
                                profile_setting_first.mask = null

                                val firstNationality = clientResult.phoneFirst!!.toInt()
                                numberAvailable = result.result[checkNumber].phoneLength!!.toInt()
                                profile_setting_first.mask =
                                    result.result.first { it.id == firstNationality }.phoneMask
                                MyUtils.toMask(
                                    clientResult.firstPhone.toString(), result.result.first
                                    { it.id == firstNationality }.phoneCode!!.length,
                                    result.result.first { it.id == firstNationality }.phoneLength!!.toInt()
                                )

                                profile_setting_first.setText(clientResult.firstPhone.toString())

                                list = result.result

                            }
                            // ???????????? ??????????
                            if (clientResult.secondPhone != "") {
                                profile_s_mask.isClickable = false
                                profile_setting_second_phone.mask = null

                                val secondNationality = clientResult.phoneSecond!!.toInt()

                                checkNumber = secondNationality

                                //???????????????????? ?????????????? ???? ???????????? ???? ?????? id
                                val id = result.result.first { it.id == secondNationality }.id
                                var position = -1
                                for (i in 0 until result.result.size) {
                                    if (result.result.get(i).id === id) {
                                        position = i
                                    }
                                }
                                codeNationality = position

                                countriesPosition = result.result[position].name.toString()

                                codeMack =
                                    result.result.first { it.id == secondNationality }.phoneCode.toString()

                                profile_setting_second_phone.mask =
                                    result.result.first { it.id == secondNationality }.phoneMaskSmall
                                profile_setting_second_phone.setText(
                                    MyUtils.toMask(
                                        clientResult.secondPhone.toString(),
                                        result.result.first { it.id == secondNationality }.phoneCode!!.length,
                                        result.result.first { it.id == secondNationality }.phoneLength!!.toInt()
                                    )
                                )
                                profile_s_mask.setText("+" + result.result.first { it.id == secondNationality }.phoneCode)
                            } else {
                                profile_setting_second_phone.text = null
                                codeMack = result.result[codeNationality].phoneCode.toString()
                                profile_s_mask.setText("+" + list[codeNationality].phoneCode)
                                countriesPosition = result.result[codeNationality].name.toString()
                            }


                            errorListAvailableCountry = result.code.toString()
                            resultSuccessfully()
                        } else {
                            listListResult(result.error.code!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

            viewModel.errorListAvailableCountry.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { error ->
                    if (error != null) {
                        errorListAvailableCountry = error
                        errorList(error)
                    }
                })
        }
    }

    // ???????????? ?????????????????? ????????????????
    private fun listQuestions() {
        //???????????????? ???? ????????????????
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
            viewModel.errorClientInfo.value = null
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            viewModel.errorListSecretQuestion.value = null
            viewModel.errorSaveProfile.value = null
        } else {
            viewModel.listSecretQuestionDta.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->
                    try {
                        if (result.result != null) {
                            question = result.result
                            profile_s_question.setText(result.result.first { it.id == clientResult.question!!.toInt() }.name)
                            var numberPosition = 0
                            if (questionId == "") {
                                numberPosition = clientResult.question!!.toInt()
                                questionId = numberPosition.toString()

                                //???????????????????? ?????????????? ???? ???????????? ???? ?????? id
                                val id = result.result.first { it.id == numberPosition }.id
                                var position = -1
                                for (i in 0 until result.result.size) {
                                    if (result.result.get(i).id === id) {
                                        position = i
                                    }
                                }
                                questionPosition = result.result[position].name.toString()
                            }

                            errorListSecretQuestion = result.code.toString()
                            resultSuccessfully()

                            if (genAnim){
                                shimmer_profile_setting.visibility = View.GONE
                            }
                            if (!genAnim) {
                                //???????????????????? ???????????????? ????????????????
                                animationGenerator(shimmer_profile_setting, handler, requireActivity())
                                genAnim = true
                            }
//                            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                                dialog.dismiss()
//                            }, 500)
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            profile_s_swipe.isRefreshing = false
                        } else {
                            listListResult(result.error.code!!)
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                                dialog.dismiss()
//                            }, 500)
                            profile_s_swipe.isRefreshing = false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                })

            viewModel.errorListSecretQuestion.observe(viewLifecycleOwner, androidx.lifecycle.Observer { error ->
                    if (error != null) {
                        errorListSecretQuestion = error
                        errorList(error)
//                        handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                            dialog.dismiss()
//                        }, 500)
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        profile_s_swipe.isRefreshing = false
                    }
                })
        }
    }

    private fun checkPassword(mapProfilePassword: HashMap<String, String>) {
        //???????????????? ???? ????????????????
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
            viewModel.errorClientInfo.value = null
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            viewModel.errorListSecretQuestion.value = null
            viewModel.errorSaveProfile.value = null
        } else {
            //???????????????? ?????????????? ????????????
            viewModel.checkPassword(mapProfilePassword).observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->
                    val msg = result.msg
                    val date = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            if (date!!.result != null) {
                                passwordTrue = date.result.code.toString()
                                if (isValidPassword()) {
                                    initPassword()
                                }
                            } else {
                                passwordTrue = date.error.code.toString()
                                if (date.error.code == 400) {
                                    isValidPassword()
                                } else {
                                    listListResult(date.error.code!!)
                                }
                            }
                        }
                        Status.NETWORK, Status.ERROR -> {
                            passwordTrue = msg.toString()
                            if (msg == "400") {
                                isValidPassword()
                            } else {
                                listListResult(msg!!.toInt())
                            }
                        }
                    }
                })
        }
    }

    // ???????????? ?????? ??????????????????????
    private fun initPassword() {
        reView = true
        if (isValid()) {
            if (textPasswordOne == textPasswordTwo) {
                if (textPasswordOne != "") {
                    AppPreferences.password = Hasher.hash(textPasswordOne, HashType.MD5)
                }
            }
            val mapProfile = HashMap<String, String>()
            mapProfile.put("login", AppPreferences.login.toString())
            mapProfile.put("token", AppPreferences.token.toString())
            mapProfile.put("password", textPasswordOne)
            mapProfile.put("second_phone", reNum)
            mapProfile.put("question", questionId)
            mapProfile.put("response", profile_s_response.text.toString())
            viewModel.saveProfile(mapProfile)
        }
    }

    private fun initResult() {
        try {
            //???????????????? ???? ????????????????
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                profile_s_no_connection.visibility = View.VISIBLE
                profile_s_technical_work.visibility = View.GONE
                profile_s_access_restricted.visibility = View.GONE
                profile_s_not_found.visibility = View.GONE
                profile_setting.visibility = View.GONE
                profile_s_swipe.visibility = View.GONE
                viewModel.errorClientInfo.value = null
                viewModel.errorListGender.value = null
                viewModel.errorListNationality.value = null
                viewModel.errorListAvailableCountry.value = null
                viewModel.errorListSecretQuestion.value = null
                viewModel.errorSaveProfile.value = null
            } else {
                //???????? ?????? ?????????????? ???????????????? ???????????????????? ?? ????????????????????????
                viewModel.listClientInfoDta.observe(viewLifecycleOwner, androidx.lifecycle.Observer { result ->
                        try {
                            if (result.result != null) {
                                clientResult = result.result
                                profile_setting_fio.setText(clientResult.firstName + " " + clientResult.lastName)
                                profile_setting_second_name.setText(clientResult.lastName)
                                profile_setting_first_name.setText(clientResult.firstName)
                                profile_setting_last_name.setText(clientResult.secondName)
                                profile_setting_data.setText(MyUtils.toMyDate(clientResult.uDate.toString()))
                                profile_s_response.setText(clientResult.response)
                                errorClientInfo = result.code.toString()
                                resultSuccessfully()

                                //?????????????????? ??????????
                                gettingFloors()

                                //?????????????????? ??????????????????????
                                obtainingCitizenship()

                                //???????????? ?????????????????? ??????????
                                listCountries()

                                //???????????? ?????????????????? ????????????????
                                listQuestions()

                            } else {
                                listListResult(result.error.code!!)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
//                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        profile_s_swipe.isRefreshing = false
                    })

                //listClientInfoDta ???????????????? ???? ????????????
                viewModel.errorClientInfo.observe(viewLifecycleOwner, androidx.lifecycle.Observer { error ->
                        try {
                            errorClientInfo = error
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (error != null) {
                            errorList(error)
                            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                                dialog.dismiss()
                            }, 500)
                        }
//                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        profile_s_swipe.isRefreshing = false
                    })

                //?????????????????? ?? ???????????????????? ????????????
                viewModel.listSaveProfileDta.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { result ->
                        try {
                            if (result.result != null) {
                                if (reView) {
                                    CookieBar.build(requireActivity())
                                        .setTitle("?????????????? ??????????????????")
                                        .setTitleColor(R.color.blackColor)
                                        .setDuration(5000)
                                        .setCookiePosition(Gravity.TOP)
                                        .show()
                                    AppPreferences.boleanCode = true
                                    findNavController().navigate(R.id.profile_navigation)
                                }
                                reView = false
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })

                viewModel.errorSaveProfile.observe(
                    viewLifecycleOwner,
                    androidx.lifecycle.Observer { error ->
                        if (error != null) {
                            errorSaveProfile = error
                            errorList(error)
                        }
                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //???????????? ?????? ?????????????? ????????????????????
    private fun closeKeyboard() {
        val view: View = requireActivity().currentFocus!!
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            try {
                val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                manager!!.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //?????????? ?????????????? ?????? ?????????????? ???? ????????????
    private fun initCleaningRoom() {
        if (profile_setting_second_phone.text.toString() != "") {
            val matchedResults =
                Regex(pattern = """\d+""").findAll(input = codeMack + profile_setting_second_phone.text.toString())
            val result = StringBuilder()
            for (matchedText in matchedResults) {
                reNum = result.append(matchedText.value).toString()
            }
        } else {
            reNum = ""
        }
    }

    // ???????????????? ???????????????????? ???? ????????????????
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listQuestions") {
            profile_s_question.isEnabled = true
            profile_s_question.setText(question[model.position].name)
            questionPosition = question[model.position].name.toString()
            questionId = question[model.position].id.toString()

        } else if (model.key == "listCountries") {
            profile_s_mask.isEnabled = true
            profile_setting_second_phone.mask = null
            profile_s_mask.setText("+" + countries[model.position].phoneCode)
            countriesPosition = countries[model.position].name.toString()
            numberAvailable = countries[model.position].phoneLength!!.toInt()
            codeNationality = model.position
            codeMack = countries[model.position].phoneCode.toString()
            profile_setting_second_phone.mask = countries[model.position].phoneMaskSmall
            profile_setting_second_phone.setText("")
            initCleaningRoom()
        }
    }

    //?????????????? ????????????
    private fun initClearList() {
        itemDialog.clear()
    }

    private fun initClick() {

        //Click ???????????? ?????????????????? ????????????????
        profile_s_question.setOnClickListener {
            profile_s_question.isEnabled = false
            initClearList()
            //?????????? ?????????????????? ???????????? ?????????????? ???? ??????????????
            if (itemDialog.size == 0) {
                for (i in 1..question.size) {
                    if (i <= question.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                question[i - 1].name.toString(),
                                "listQuestions",
                                i - 1,
                                question[i - 1].id,
                                question[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(
                    itemDialog,
                    questionPosition,
                    "???????????????? ?????????????????? ????????????",
                    profile_s_question
                )
            }
        }

        //Click ???????????? ?????????????????? ??????????
        profile_s_mask.setOnClickListener {
            profile_s_mask.isEnabled = false
            initClearList()
            //?????????? ?????????????????? ???????????? ?????????????? ???? ??????????????
            if (itemDialog.size == 0) {
                for (i in 1..countries.size) {
                    if (i <= countries.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                countries[i - 1].name.toString(),
                                "listCountries",
                                i - 1,
                                0,
                                countries[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(
                    itemDialog,
                    countriesPosition,
                    "???????????? ?????????????????? ??????????",
                    profile_s_mask
                )
            }
        }


        profile_s_swipe.setOnRefreshListener {
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                gettingFloors()
                viewModel.refreshCode = true
                profile_s_one_password.text = null
                profile_s_two_password.text = null
                isRestart()
                if (imageString != "") {
                    gitImage()
                }
            }, 500)
            profile_s_two_password.error = null
            profile_s_two_password.error = null
            profile_s_one_password.error = null
            profile_s_response.error = null
        }
        profile_s_swipe.setColorSchemeResources(android.R.color.holo_orange_dark)


        profile_s_one_password.addTextChangedListener {
            textPasswordOne = it.toString()
        }

        profile_s_one_password.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (profile_s_one_password.text.isNotEmpty()) {
                    profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
                }
            }
        }
        profile_s_two_password.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (profile_s_two_password.text.isNotEmpty()) {
                    profile_s_two_password.setSelection(profile_s_two_password.text!!.length);
                }
            }
        }

        profile_tube.setOnClickListener {
            profile_tube.isClickable = false
            //?????????????????? ??????????????
            initSuspendTime()
            loadFiles()
        }

        //???????????????????????????? ??????????
        profile_setting_second_phone.viewTreeObserver
            .addOnGlobalLayoutListener {
                try {
                    val r = Rect()
                    profile_setting_second_phone.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int = requireView().rootView.height - (r.bottom - r.top)
                    if (heightDiff > 100) {

                    } else {
                        profile_setting_second_phone.clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        //??????????
        profile_s_response.viewTreeObserver
            .addOnGlobalLayoutListener {
                try {
                    val r = Rect()
                    profile_s_response.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int = requireView().rootView.height - (r.bottom - r.top)
                    if (heightDiff > 100) {

                    } else {
                        clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        //?????????????? ???????????? ????????????
        profile_s_old_password.viewTreeObserver
            .addOnGlobalLayoutListener {
                try {
                    val r = Rect()
                    profile_s_old_password.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int = requireView().rootView.height - (r.bottom - r.top)
                    if (heightDiff > 100) {

                    } else {
                        clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        //?????????????? ?????????? ????????????
        profile_s_one_password.viewTreeObserver
            .addOnGlobalLayoutListener {
                try {
                    val r = Rect()
                    profile_s_one_password.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int = requireView().rootView.height - (r.bottom - r.top)
                    if (heightDiff > 100) {

                    } else {
                        clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        //?????????????????? ????????????
        profile_s_two_password.viewTreeObserver
            .addOnGlobalLayoutListener {
                try {
                    val r = Rect()
                    profile_s_two_password.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int = requireView().rootView.height - (r.bottom - r.top)
                    if (heightDiff > 100) {

                    } else {
                        clearFocus()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        click_s_old_password_image.setOnClickListener {
            profile_s_old_password.requestFocus()
            profile_s_old_password.setSelection(profile_s_old_password.text!!.length);
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_s_old_password, 0)
        }

        click_s_one_password.setOnClickListener {
            profile_s_one_password.requestFocus()
            profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_s_one_password, 0)
        }

        profile_s_two_password.addTextChangedListener {
            textPasswordTwo = it.toString()
        }

        click_s_two_password.setOnClickListener {
            profile_s_two_password.requestFocus()
            profile_s_two_password.setSelection(profile_s_two_password.text!!.length);
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_s_two_password, 0)
        }

        //?????????? ?????????????? ?????? ?????????????? ???? ????????????
        profile_setting_second_phone.addTextChangedListener {
            editUtils(
                layout_profile_setting_second,
                profile_setting_second_phone,
                profile_setting_second_error,
                "?????????????? ???????????????????? ??????????",
                false
            )
            profile_optional_number.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.blackColor
                )
            )
            initCleaningRoom()
        }

        // ?????????? ???????????? ?????? ??????
        var isValidPassword = false
        profile_s_old_password_image.setOnClickListener {
            if (!isValidPassword) {
                profile_s_old_password.transformationMethod = null
                profile_s_old_password.setSelection(profile_s_old_password.text!!.length);
                isValidPassword = true
            } else {
                profile_s_old_password.transformationMethod = PasswordTransformationMethod()
                profile_s_old_password.setSelection(profile_s_old_password.text!!.length);
                isValidPassword = false
            }
        }

        // ?????????? ???????????? ?????? ??????
        var isValidOne = false
        profile_s_one_password_show.setOnClickListener {
            if (!isValidOne) {
                profile_s_one_password.transformationMethod = null
                profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
                isValidOne = true
            } else {
                profile_s_one_password.transformationMethod = PasswordTransformationMethod()
                profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
                isValidOne = false
            }
        }
        // ?????????? ???????????? ?????? ??????
        var isValidTwo = false
        profile_s_two_password_show.setOnClickListener {
            if (!isValidTwo) {
                profile_s_two_password.transformationMethod = null
                profile_s_two_password.setSelection(profile_s_two_password.text!!.length);
                isValidTwo = true
            } else {
                profile_s_two_password.transformationMethod = PasswordTransformationMethod()
                profile_s_two_password.setSelection(profile_s_two_password.text!!.length);
                isValidTwo = false
            }
        }


        access_restricted.setOnClickListener {
            initVisibilities()
            isRestart()
        }

        no_connection_repeat.setOnClickListener {
            initVisibilities()
            isRestart()
        }

        technical_work.setOnClickListener {
            initVisibilities()
            isRestart()
        }

        not_found.setOnClickListener {
            initVisibilities()
            isRestart()
        }

        home_forget_password.setOnClickListener {
            val intent = Intent(context, ContactingServiceActivity::class.java)
            profileSettingAnimR = true
            intent.putExtra("number", "1")
            startActivity(intent)
        }


        profile_s_enter.setOnClickListener {
            if (profile_s_one_password.text.toString() != "" && profile_s_two_password.text.toString() != "") {
                if (profile_s_old_password.text.isNotEmpty()) {
                    val mapProfilePassword = HashMap<String, String>()
                    mapProfilePassword.put("login", AppPreferences.login.toString())
                    mapProfilePassword.put("password", profile_s_old_password.text.toString())
                    checkPassword(mapProfilePassword)
                } else {
                    isValidPassword()
                }
            } else {
                if (profile_s_old_password.text.isNotEmpty()) {
                    val mapProfilePassword = HashMap<String, String>()
                    mapProfilePassword.put("login", AppPreferences.login.toString())
                    mapProfilePassword.put("password", profile_s_old_password.text.toString())
                    checkPassword(mapProfilePassword)
                } else {
                    initPassword()
                }
            }
        }


        profile_setting_second_phone.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (profile_setting_second_phone.text!!.isEmpty()) {
                    profile_setting_second_phone.mask = null
                    profile_setting_second_phone.mask = list[codeNationality].phoneMaskSmall
                    profile_setting_second_phone.setSelection(profile_setting_second_phone.text!!.length);
                    profile_setting_second_phone.isFocusableInTouchMode = true
                }
            }

        click_s_response.setOnClickListener {
            profile_s_response.requestFocus()
            profile_s_response.setSelection(profile_s_response.text!!.length);
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_s_response, 0)
        }

        click_s_second.setOnClickListener {
            profile_setting_second_phone.requestFocus()
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_setting_second_phone, 0)
        }
    }

    private fun listListResult(result: Int) {
        if (result == 400 || result == 500 || result == 409 || result == 429) {
            profile_s_technical_work.visibility = View.VISIBLE
            profile_s_no_connection.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (result == 403) {
            profile_s_access_restricted.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (result == 404) {
            profile_s_not_found.visibility = View.VISIBLE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        }
        MainActivity.alert.hide()
    }

    private fun errorList(error: String) {
        if (error == "400" || error == "500" || error == "429" || error == "409" || error == "601") {
            profile_s_technical_work.visibility = View.VISIBLE
            profile_s_no_connection.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (error == "403") {
            profile_s_access_restricted.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (error == "404") {
            profile_s_not_found.visibility = View.VISIBLE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        }else if (error == "600") {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_setting.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        }else if (error == "401") {
            initAuthorized()
        }
        MainActivity.alert.hide()
    }

    // ???????????????? ???????????????? ???? ????????????
    private fun gitImage() {
        val mapUploadImg = HashMap<String, String>()
        mapUploadImg.put("login", AppPreferences.login.toString())
        mapUploadImg.put("token", AppPreferences.token.toString())
        mapUploadImg.put("type", "profile")
        mapUploadImg.put("doc_id", "0")
        mapUploadImg.put("type_id", "0")
        mapUploadImg.put("file", imageString)
        viewModel.uploadImg(mapUploadImg)
        myThread.interrupt()

    }

    //???????? ???????????????? ???? ???????????? ???????????????????? ??????????????
    private fun iniImageToServer() {
        viewModel.listUploadImgDta.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { result ->
                if (result.result != null) {
                    profile_setting_image.setImageBitmap(bitmap)
                    MainActivity.alert.hide()
                } else if (result.error != null) {
                    listListResultHome(result.error.code!!.toInt(), activity as AppCompatActivity)
                    MainActivity.alert.hide()
                }
            })

        viewModel.errorUploadImg.observe(viewLifecycleOwner, androidx.lifecycle.Observer { error ->
            if (error != null) {
                listListResultHome(error, activity as AppCompatActivity)
                MainActivity.alert.hide()
            }
        })
    }

    // ???????????????? ???????? errorCode ?? errorCodeClient == 200
    private fun resultSuccessfully() {
        if (errorCodeGender == "200" && errorCodeNationality == "200" && errorListAvailableCountry == "200" && errorListSecretQuestion == "200" && errorClientInfo == "200") {
            profile_setting.visibility = View.VISIBLE
            profile_s_swipe.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
        }
    }

    //?????????? ?????????????????? ???????????????? ?? ???????????? ????????????????
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
            Toast.makeText(context, "?????? ????????????????????", Toast.LENGTH_SHORT).show()
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
            val takePictureIntent = Intent(ACTION_IMAGE_CAPTURE)
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.type = "image/*"
            val chooser = Intent.createChooser(pickIntent, "Some text here")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagUri)
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
            startActivityForResult(chooser, IMAGE_PICK_CODE)

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
                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(scaled, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                        rotateImage(scaled, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                        rotateImage(scaled, 270F)
                    ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = scaled
                    else -> rotatedBitmap = scaled
                }
                imageBitmap(rotatedBitmap!!)
            } else {
                val bm: Bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().getApplicationContext().getContentResolver(), data.getData()
                );
                imageBitmap(bm)
            }
        }
    }

    //???????????? ???????????????? ?? ???????????? ????????????????????????
    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun imageBitmap(bm: Bitmap) {
        AppPreferences.updatingImage = true
        addImage = true
        val nh = (bm.height * (512.0 / bm.width)).toInt()
        val scaled = Bitmap.createScaledBitmap(bm, 512, nh, true)
        bitmap = scaled
        imageConverter(scaled)
        MainActivity.alert.show()
    }

    //encode image to base64 string
    private fun imageConverter(bitmap: Bitmap) {
        myThread = Thread() {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes: ByteArray = baos.toByteArray()
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            gitImage()
        }
        myThread.start()
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    //?????????? ?????????????????????? ???????? ?? ???????????????????????? ?????????????????????? ????????????.
    private fun initBottomSheet(list: ArrayList<GeneralDialogModel>, selectionPosition: String, title: String, id: AutoCompleteTextView) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title, id)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    override fun onResume() {
        super.onResume()
        profile_tube.isClickable = true
        profile_s_one_password.text = null
        profile_s_two_password.text = null
        profile_s_two_password.transformationMethod = PasswordTransformationMethod()
        profile_s_one_password.transformationMethod = PasswordTransformationMethod()
        profile_s_old_password.text = null
        profile_s_old_password.transformationMethod = PasswordTransformationMethod()
        if (viewModel.listGenderDta.value != null && viewModel.listGenderDta.value != null && viewModel.listNationalityDta.value != null
            && viewModel.listAvailableCountryDta.value != null && viewModel.listSecretQuestionDta.value != null && viewModel.listClientInfoDta.value != null) {
            if (errorCodeGender == "200" && errorCodeNationality == "200" && errorListAvailableCountry == "200" && errorListSecretQuestion == "200" && errorClientInfo == "200") {
                AppPreferences.reviewCode = 1
                initResult()
            } else {
                AppPreferences.reviewCode = 1
                profileSettingAnim = true
                initRestart()
            }
        } else {
            AppPreferences.reviewCode = 0
//            profileSettingAnim = false
            viewModel.refreshCode = false
            initRestart()
        }

        //???????????? ?????????? ?????????????????????????? ????????????
        ColorWindows(activity as AppCompatActivity).rollback()

        val backArrow = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        backArrow.setColorFilter(resources.getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        (activity as AppCompatActivity?)!!.getSupportActionBar()!!.setHomeAsUpIndicator(backArrow)
        profile_s_owner.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        //???????????????? ???????????????? ???? ???????????????? ???????????? ??????????????????
        if (profileSettingAnimR) {
            //profileAnim ???????????????? ?????? ???????????????? ?? ???????????? ?????????????? ?? ????????????
            TransitionAnimation(activity as AppCompatActivity).transitionLeft(profile_setting_anim)
            profileSettingAnimR = false
        }
        // ???????????????? ???????? ?? timer ???????????????? token null
        if (AppPreferences.token == "") {
            initAuthorized()
        }
    }

    //?????????????????? ?????????? ???? ????????????
    private fun clearFocus() {
        profile_s_old_password.clearFocus()
        profile_s_two_password.clearFocus()
        profile_s_one_password.clearFocus()
        profile_s_response.clearFocus()
        focus_prof.requestFocus()
    }

    private fun isValid(): Boolean {
        var valid = true
        if (profile_s_response.text!!.toString().isEmpty()) {
            editUtils(
                layout_profile_s_response,
                profile_s_response,
                profile_s_response_error,
                "?????????????????? ????????",
                true
            )
            text_answer.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
            valid = false
        }

        if (profile_setting_second_phone.text.toString() != "") {
            if (reNum.length != list[codeNationality].phoneLength!!.toInt()) {
                editUtils(layout_profile_setting_second, profile_setting_second_phone, profile_setting_second_error, "?????????????? ???????????????????? ??????????", true)
                profile_optional_number.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
                valid = false
            }
        }


        if (profile_s_one_password.text.toString()
                .isNotEmpty() && profile_s_two_password.text.toString().isNotEmpty()
        ) {
            if (profile_s_one_password.text.toString()
                    .toFullPhone() != profile_s_two_password.text.toString().toFullPhone()
            ) {
                editUtils(
                    layout_profile_s_two,
                    profile_s_two_password,
                    profile_s_two_error,
                    "???????????? ?????????? ??????????????????",
                    true
                )
                text_repeat.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
                profile_s_two_password_show.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorRed
                    ), PorterDuff.Mode.SRC_IN
                );
                valid = false
            }
        } else {
            if (profile_s_one_password.text.toString()
                    .isNotEmpty() && profile_s_two_password.text.toString().isEmpty()
            ) {
                editUtils(
                    layout_profile_s_two,
                    profile_s_two_password,
                    profile_s_two_error,
                    "?????????????????? ????????",
                    true
                )
                text_repeat.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
                profile_s_two_password_show.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorRed
                    ), PorterDuff.Mode.SRC_IN
                );
                valid = false
            }

            if (profile_s_one_password.text.toString()
                    .isEmpty() && profile_s_two_password.text.toString().isNotEmpty()
            ) {
                editUtils(
                    layout_profile_s_one,
                    profile_s_one_password,
                    profile_s_one_error,
                    "?????????????????? ????????",
                    true
                )
                text_new_phone.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorRed
                    )
                )
                profile_s_one_password_show.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorRed
                    ), PorterDuff.Mode.SRC_IN
                );
                valid = false
            }
        }

        return valid
    }

    private fun isValidPassword(): Boolean {
        var valid = true
        if (profile_s_old_password.text.toString().isEmpty()) {
            editUtils(
                layout_profile_s_old,
                profile_s_old_password,
                profile_s_old_error,
                "?????????????????? ????????",
                true
            )
            text_old.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
            profile_s_old_password_image.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorRed
                ), PorterDuff.Mode.SRC_IN
            );
            valid = false
        } else if (passwordTrue != "200") {
            editUtils(
                layout_profile_s_old,
                profile_s_old_password,
                profile_s_old_error,
                "???????????? ???????????? ??????????????",
                true
            )
            text_old.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorRed))
            profile_s_old_password_image.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorRed
                ), PorterDuff.Mode.SRC_IN
            );
            valid = false
        } else {
            editUtils(layout_profile_s_old, profile_s_old_password, profile_s_old_error, "", false)
            text_old.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackColor))
            profile_s_old_password_image.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blackColor
                ), PorterDuff.Mode.SRC_IN
            );
        }
        return valid
    }

    private fun initView() {
        profile_s_response.addTextChangedListener {
            editUtils(
                layout_profile_s_response,
                profile_s_response,
                profile_s_response_error,
                "?????????????????? ????????",
                false
            )
            text_answer.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackColor))
        }

        profile_s_old_password.addTextChangedListener {
            editUtils(
                layout_profile_s_old,
                profile_s_old_password,
                profile_s_old_error,
                "?????????????????? ????????",
                false
            )
            text_old.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackColor))
            profile_s_old_password_image.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blackColor
                ), PorterDuff.Mode.SRC_IN
            );
        }

        profile_s_one_password.addTextChangedListener {
            editUtils(
                layout_profile_s_one,
                profile_s_one_password,
                profile_s_one_error,
                "?????????????????? ????????",
                false
            )
            text_new_phone.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blackColor
                )
            )
            profile_s_one_password_show.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blackColor
                ), PorterDuff.Mode.SRC_IN
            );
        }

        profile_s_two_password.addTextChangedListener {
            editUtils(
                layout_profile_s_two,
                profile_s_two_password,
                profile_s_two_error,
                "?????????????????? ????????",
                false
            )
            text_repeat.setTextColor(ContextCompat.getColor(requireContext(), R.color.blackColor))
            profile_s_two_password_show.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.blackColor), PorterDuff.Mode.SRC_IN
            );
        }
    }
}