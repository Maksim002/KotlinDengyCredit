package com.example.kotlincashloan.ui.loans.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.loans.StepClickListener
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.loans.fragment.dialogue.StepBottomFragment
import com.example.kotlincashloan.ui.profile.ProfileViewModel
import com.example.kotlincashloan.utils.*
import com.regula.facesdk.Face.Instance
import com.regula.facesdk.enums.eInputFaceType
import com.regula.facesdk.results.LivenessResponse
import com.regula.facesdk.structs.Image
import com.regula.facesdk.structs.MatchFacesRequest
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_loan_step_face.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class LoanStepFaceFragment() : Fragment(), StepClickListener {
    private var viewModel = LoansViewModel()
    private var photoViewModel = ProfileViewModel()
    private lateinit var imageFace: Bitmap
    private lateinit var textViewLiveliness: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loan_step_face, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Instance().setServiceUrl("https://faceapi.molbulak.com")

        initRestart()
        initClick()
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            face_no_connection.visibility = View.VISIBLE
            layout_face.visibility = View.GONE
            face_technical_work.visibility = View.GONE
            face_access_restricted.visibility = View.GONE
            face_not_found.visibility = View.GONE
        } else {
            initResult()
        }
    }

    // Метод обробатывает клики
    private fun initClick() {
        bottom_loan_face.setOnClickListener {
            requestFace()
        }

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
    }

    private fun initResult() {
        // выгружает фотографию из сервира
        val mapPhoto = HashMap<String, String>()
        mapPhoto.put("login", AppPreferences.login.toString())
        mapPhoto.put("token", AppPreferences.token.toString())
        mapPhoto.put("type", "doc")
        mapPhoto.put("doc_id", "3")
        mapPhoto.put("type_id", "passport_photo")

        photoViewModel.getImg(mapPhoto)
        photoViewModel.listGetImgDta.observe(viewLifecycleOwner, Observer { result->
            val data = result.result
            val msg = result.error
            if (data != null){
                baseToBitmap(data.data.toString())
                layout_face.visibility = View.VISIBLE
                face_access_restricted.visibility = View.GONE
                face_no_connection.visibility = View.GONE
                face_technical_work.visibility = View.GONE
                face_not_found.visibility = View.GONE
            }else if (msg != null){
                listListResult(result.error.code!!.toInt(),face_technical_work as LinearLayout,face_no_connection as LinearLayout
                    ,layout_face,face_access_restricted as LinearLayout,face_not_found as LinearLayout, requireActivity())
            }
        })

        photoViewModel.errorGetImg.observe(viewLifecycleOwner, Observer { error->
            if (error != null){
                listListResult(error,face_technical_work as LinearLayout,face_no_connection as LinearLayout
                    ,layout_face,face_access_restricted as LinearLayout,face_not_found as LinearLayout, requireActivity())
            }
        })
    }

    private fun requestFace(){
        //Метод сканироет лицо проверяет на живность
        Instance().startLivenessMatching(requireContext(), 1) { livenessResponse: LivenessResponse? ->
            if (livenessResponse != null && livenessResponse.bitmap != null) {
                //Если сканирование прошло успешно
               if (livenessResponse.liveness == 0) {
                    imageFace = livenessResponse.bitmap!!
                   imageConverter(livenessResponse.bitmap!!)
                    comparingPhotos()
                }
            }
            GetLoanActivity.alert.show()
            Instance().stopLivenessProcessing(requireContext());
       }
    }

    // Метод сравнивает 2 фотографии imageView1 && imageView2
    private fun comparingPhotos() {
        if (setBitmapIm.sameAs(setBitmapIm) && imageFace.sameAs(imageFace)) {
            matchFaces(setBitmapIm, imageFace)
        }
    }

    //После сравнения 2 фотографий отоброжает сообщение сходства илим иначе
    private fun matchFaces(first: Bitmap, second: Bitmap) {
        val matchRequest = MatchFacesRequest()
        val firstImage = Image()
        firstImage.setImage(first)
        firstImage.imageType = (eInputFaceType.ift_DocumentPrinted)
        matchRequest.images.add(firstImage)

        val secondImage = Image()
        secondImage.setImage(second)
        secondImage.imageType = (eInputFaceType.ift_Live)
        matchRequest.images.add(secondImage)

        Instance().matchFaces(matchRequest) { matchFacesResponse ->
            if (matchFacesResponse?.matchedFaces!!.size != 0) {
                val similarity = matchFacesResponse.matchedFaces[0].similarity
                if (similarity.toString() == "NaN"){
                    //Если сравнивается живое лицо && придметом
                    textViewLiveliness = "0.0"
                }else{
                    //Если сравнивается живое лицо && фото
                    textViewLiveliness = String.format("%.2f", similarity * 100)
                    calculatingPercentages(textViewLiveliness)
                }
            }else{
                // если придстоит сравнить 2 непохожии фотографии
                val similarity = matchFacesResponse.unmatchedFaces[0].similarity
                if (similarity.toString() == "NaN"){
                    //Если сравнивается живое лицо && придметом
                    textViewLiveliness = "Сходство: 0.0%"
                }else{
                    //Если сравнивается живое лицо && фото
                    textViewLiveliness = String.format("%.2f", similarity * 100)
                    calculatingPercentages(textViewLiveliness)
                }
            }
        }
    }

    private fun calculatingPercentages(string: String){
        if (string >= "90.00"){
            initSaveImage()
        }else{
            Toast.makeText(requireContext(), "Отсканируйте лицо повторно", Toast.LENGTH_LONG).show()
            GetLoanActivity.alert.hide()
        }
    }

    //Сохронение картинок
    private fun initSaveImage() {
        val mapImage = mutableMapOf<String, String>()
        mapImage["login"] = AppPreferences.login.toString()
        mapImage["token"] = AppPreferences.token.toString()
        mapImage.put("id", AppPreferences.applicationId.toString())
        mapImage["live_photo_1"] = getBitmapIm
        mapImage.put("step", "0")

        viewModel.saveLoans(mapImage).observe(viewLifecycleOwner, Observer { result->
            val msg = result.msg
            val data = result.data
            when(result.status){
                Status.SUCCESS ->{
                    if (data!!.result != null){
                        initSaveLoan()
                    }else if (data.error.code != null){
                        if (data.error.code == 409) {
                            Toast.makeText(requireContext(), "Отсканируйте лицо повторно", Toast.LENGTH_LONG).show()
                            GetLoanActivity.alert.hide()
                        }else{
                            listListResult(data.error.code!!.toInt(), activity as AppCompatActivity)
                            GetLoanActivity.alert.hide()
                        }
                    }else if (data.reject != null){
                        initBottomSheet(data.reject.message!!)
                        GetLoanActivity.alert.hide()
                    }
                }
                Status.ERROR, Status.NETWORK ->{
                    if (msg != null){
                        listListResult(msg, activity as AppCompatActivity)
                        GetLoanActivity.alert.hide()
                    }
                }
            }
        })
    }

    //Сохронение Если совподение 99%
    private fun initSaveLoan(){
        val mapSaveLoan = HashMap<String, String>()
        mapSaveLoan.put("login", AppPreferences.login.toString())
        mapSaveLoan.put("token", AppPreferences.token.toString())
        mapSaveLoan.put("id", AppPreferences.applicationId.toString())
        mapSaveLoan.put("step", "7")

        viewModel.saveLoans(mapSaveLoan).observe(viewLifecycleOwner, Observer { result->
            val msg = result.msg
            val data = result.data
            when(result.status){
                Status.SUCCESS ->{
                   if (data!!.result != null){
                       (activity as GetLoanActivity?)!!.get_loan_view_pagers.currentItem = 8
                   }else if (data.error.code != null){
                       listListResult(data.error.code!!.toInt(), activity as AppCompatActivity)
                   }else if (data.reject != null){
                       initBottomSheet(data.reject.message!!)
                   }
                }
                Status.ERROR, Status.NETWORK ->{
                    if (msg != null){
                        listListResult(msg, activity as AppCompatActivity)
                    }
                }
            }
            GetLoanActivity.alert.hide()
        })
    }

    // если срабатывает reject
    private fun initBottomSheet(message: String) {
        val stepBottomFragment = StepBottomFragment(this, message)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    override fun onClickStepListener() {
        requireActivity().finish()
    }
}
