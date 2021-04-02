package com.example.kotlincashloan.ui.loans.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.profile.ProfileViewModel
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.baseToBitmap
import com.example.kotlincashloan.utils.bitmapIm
import com.regula.facesdk.Face.Instance
import com.regula.facesdk.enums.eInputFaceType
import com.regula.facesdk.results.LivenessResponse
import com.regula.facesdk.results.MatchFacesResponse
import com.regula.facesdk.structs.Image
import com.regula.facesdk.structs.MatchFacesRequest
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_loan_step_face.*
import kotlinx.android.synthetic.main.fragment_loan_step_fifth.*
import kotlinx.android.synthetic.main.fragment_loan_step_six.*

class LoanStepFaceFragment : Fragment() {
    private var viewModel = LoansViewModel()
    private var photoViewModel = ProfileViewModel()
    private lateinit var imageFace: Bitmap
    private lateinit var imageServer: Bitmap
    private lateinit var textViewLiveliness: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loan_step_face, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    comparingPhotos()
                }
            }
            Instance().stopLivenessProcessing(requireContext());
        }
    }

    // Метод сравнивает 2 фотографии imageView1 && imageView2
    private fun comparingPhotos() {
        if (bitmapIm.sameAs(bitmapIm) && imageFace.sameAs(imageFace)) {
            matchFaces(bitmapIm, imageFace)
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
            if (matchFacesResponse?.matchedFaces != null) {
                val similarity = matchFacesResponse.matchedFaces[0].similarity
                if (similarity.toString() == "NaN"){
                    //Если сравнивается живое лицо && придметом
                    textViewLiveliness = "0.0"
                }else{
                    //Если сравнивается живое лицо && фото
                    textViewLiveliness = String.format("%.2f", similarity * 100)
                }
            }
        }
    }

    private fun initClick() {
        bottom_loan_face.setOnClickListener {
            requestFace()
        }
    }
}
