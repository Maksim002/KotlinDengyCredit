package com.example.kotlincashloan.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.extension.generateUrl
import com.example.kotlincashloan.service.model.Loans.SixNumResultModel
import com.example.kotlincashloan.service.model.login.SaveLoanResultModel
import com.example.kotlincashloan.service.model.profile.*
import com.example.kotlincashloan.ui.loans.SharedViewModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.service.model.*
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.ResultStatus
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel(){
    var repository = NetworkRepository()
    val handler = Handler()
    var refreshCode = false
//    var mitmap = HashMap<String, Bitmap>()

    val errorListOperation = MutableLiveData<String>()
    var listListOperationDta = MutableLiveData<CommonResponse<ArrayList<ResultOperationModel>>>()

    fun listOperation(map: Map<String, String>){
        RetrofitService.apiService().listOperation(map, generateUrl("listOperation")).enqueue(object : Callback<CommonResponse<ArrayList<ResultOperationModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ResultOperationModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListOperation.postValue( "601")
                }else{
                    errorListOperation.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ResultOperationModel>>>, response: Response<CommonResponse<ArrayList<ResultOperationModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listListOperationDta.postValue(response.body())
                    }else{
                        errorListOperation.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListOperation.postValue(response.raw().code.toString())
                }
            }
        })
    }


    val errorListApplication = MutableLiveData<String>()
    var listListApplicationDta = MutableLiveData<CommonResponse<ArrayList<ResultApplicationModel>>>()

    fun listApplication(map: Map<String, String>){
        RetrofitService.apiService().listLoan(map, generateUrl("listLoan")).enqueue(object : Callback<CommonResponse<ArrayList<ResultApplicationModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ResultApplicationModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListApplication.postValue( "601")
                }else{
                    errorListApplication.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ResultApplicationModel>>>, response: Response<CommonResponse<ArrayList<ResultApplicationModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listListApplicationDta.postValue(response.body())
                    }else{
                        errorListApplication.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListApplication.postValue(response.raw().code.toString())
                }
            }
        })
    }


    val errorGetOperation = MutableLiveData<String>()
    var listGetOperationDta = MutableLiveData<CommonResponse<GetResultOperationModel>>()

    fun getOperation(map: Map<String, String>){
        RetrofitService.apiService().getOperation(map, generateUrl("getOperation")).enqueue(object : Callback<CommonResponse<GetResultOperationModel>> {
            override fun onFailure(call: Call<CommonResponse<GetResultOperationModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorGetOperation.postValue( "601")
                }else{
                    errorGetOperation.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<GetResultOperationModel>>, response: Response<CommonResponse<GetResultOperationModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listGetOperationDta.postValue(response.body())
                    }else{
                        errorGetOperation.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorGetOperation.postValue(response.raw().code.toString())
                }
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    MainActivity.alert.hide()
                },500)
            }
        })
    }

    val errorClientInfo = MutableLiveData<String>()
    var listClientInfoDta = MutableLiveData<CommonResponse<ClientInfoResultModel>>()

    fun clientInfo(map: Map<String, String>){
        RetrofitService.apiService().clientInfo(map, generateUrl("clientInfo")).enqueue(object : Callback<CommonResponse<ClientInfoResultModel>> {
            override fun onFailure(call: Call<CommonResponse<ClientInfoResultModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorClientInfo.postValue( "601")
                }else{
                    errorClientInfo.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ClientInfoResultModel>>, response: Response<CommonResponse<ClientInfoResultModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listClientInfoDta.postValue(response.body())
                    }else{
                        errorClientInfo.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorClientInfo.postValue(response.raw().code.toString())
                }
//                handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                    HomeActivity.alert.hide()
//                },1200)
            }
        })
    }

    val errorListGender = MutableLiveData<String>()
    var listGenderDta = MutableLiveData<CommonResponse<ArrayList<ListGenderResultModel>>>()

    fun listGender(map: Map<String, String>){
        RetrofitService.apiService().listGender(map, generateUrl("listGender")).enqueue(object : Callback<CommonResponse<ArrayList<ListGenderResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListGenderResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListGender.postValue( "601")
                }else{
                    errorListGender.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListGenderResultModel>>>, response: Response<CommonResponse<ArrayList<ListGenderResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listGenderDta.postValue(response.body())
                    }else{
                        errorListGender.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListGender.postValue(response.raw().code.toString())
                }
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    MainActivity.alert.hide()
                },500)
            }
        })
    }

    val errorListNationality = MutableLiveData<String>()
    var listNationalityDta = MutableLiveData<CommonResponse<ArrayList<ListNationalityResultModel>>>()

    fun getListNationality(map: Map<String, String>){
        RetrofitService.apiService().getListNationality(map, generateUrl("listNationality")).enqueue(object : Callback<CommonResponse<ArrayList<ListNationalityResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListNationalityResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListNationality.postValue( "601")
                }else{
                    errorListNationality.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListNationalityResultModel>>>, response: Response<CommonResponse<ArrayList<ListNationalityResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listNationalityDta.postValue(response.body())
                    }else{
                        errorListNationality.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListNationality.postValue(response.raw().code.toString())
                }
            }
        })
    }

    val errorListAvailableCountry = MutableLiveData<String>()
    var listAvailableCountryDta = MutableLiveData<CommonResponse<ArrayList<CounterNumResultModel>>>()

    fun listAvailableCountry(map: Map<String, String>){
        RetrofitService.apiService().listAvailableCountry(map, generateUrl("listAvailableCountry")).enqueue(object : Callback<CommonResponse<ArrayList<CounterNumResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<CounterNumResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListAvailableCountry.postValue( "601")
                }else{
                    errorListAvailableCountry.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<CounterNumResultModel>>>, response: Response<CommonResponse<ArrayList<CounterNumResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listAvailableCountryDta.postValue(response.body())
                    }else{
                        errorListAvailableCountry.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListAvailableCountry.postValue(response.raw().code.toString())
                }
            }
        })
    }

    val errorListSecretQuestion = MutableLiveData<String>()
    var listSecretQuestionDta = MutableLiveData<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>()

    fun listSecretQuestion(map: Map<String, String>){
        RetrofitService.apiService().listSecretQuestion(map, generateUrl("listSecretQuestion")).enqueue(object : Callback<CommonResponse<ArrayList<ListSecretQuestionResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListSecretQuestion.postValue( "601")
                }else{
                    errorListSecretQuestion.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>, response: Response<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listSecretQuestionDta.postValue(response.body())
                    }else{
                        errorListSecretQuestion.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListSecretQuestion.postValue(response.raw().code.toString())
                }
            }
        })
    }

    val errorSaveProfile = MutableLiveData<String>()
    var listSaveProfileDta = MutableLiveData<CommonResponse<SaveProfileResultModel>>()

    fun saveProfile(map: Map<String, String>){
        RetrofitService.apiService().saveProfile(map, generateUrl("saveProfile")).enqueue(object : Callback<CommonResponse<SaveProfileResultModel>> {
            override fun onFailure(call: Call<CommonResponse<SaveProfileResultModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorSaveProfile.postValue( "601")
                }else{
                    errorSaveProfile.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<SaveProfileResultModel>>, response: Response<CommonResponse<SaveProfileResultModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listSaveProfileDta.postValue(response.body())
                    }else{
                        errorSaveProfile.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorSaveProfile.postValue(response.raw().code.toString())
                }
            }
        })
    }


    fun checkPassword(phone:  Map<String, String>): LiveData<ResultStatus<CommonResponse<CheckPasswordResultModel>>> {
        return repository.checkPassword(phone)
    }


//    val errorCheckPassword = MutableLiveData<String>()
//    var listCheckPasswordDta = MutableLiveData<CommonResponse<CheckPasswordResultModel>>()
//
//    fun checkPassword(map: Map<String, String>){
//        RetrofitService.apiService().checkPassword(map, generateUrl("checkPassword")).enqueue(object : Callback<CommonResponse<CheckPasswordResultModel>> {
//            override fun onFailure(call: Call<CommonResponse<CheckPasswordResultModel>>, t: Throwable) {
//                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
//                    errorCheckPassword.postValue( "601")
//                }else{
//                    errorCheckPassword.postValue( "600")
//                }
//            }
//            override fun onResponse(call: Call<CommonResponse<CheckPasswordResultModel>>, response: Response<CommonResponse<CheckPasswordResultModel>>) {
//                if (response.isSuccessful) {
//                    if (response.body()!!.code == 200){
//                        listCheckPasswordDta.postValue(response.body())
//                    }else{
//                        errorCheckPassword.postValue(response.body()!!.code.toString())
//                    }
//                }else{
//                    errorCheckPassword.postValue(response.raw().code.toString())
//                }
//            }
//        })
//    }

    val errorGetImg = MutableLiveData<String>()
    var listGetImgDta = MutableLiveData<CommonResponse<GetImgResultModel>>()

    fun getImg(map: Map<String, String>){
        RetrofitService.apiService().getImg(map, generateUrl("getImg")).enqueue(object : Callback<CommonResponse<GetImgResultModel>> {
            override fun onFailure(call: Call<CommonResponse<GetImgResultModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorGetImg.postValue( "601")
                }else{
                    errorGetImg.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<GetImgResultModel>>, response: Response<CommonResponse<GetImgResultModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listGetImgDta.postValue(response.body())
                    }else{
                        errorGetImg.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorGetImg.postValue(response.raw().code.toString())
                }
            }
        })
    }


    //Запрос на получение картинок для заёма
    fun getImgLoan(phone:  Map<String, String>): LiveData<ResultStatus<CommonResponse<GetImgResultModel>>> {
        return repository.getImgLoan(phone)
    }

    val errorUploadImg = MutableLiveData<String>()
    var listUploadImgDta = MutableLiveData<CommonResponse<UploadImgResultModel>>()

    fun uploadImg(map: Map<String, String>){
        RetrofitService.apiService().uploadImg(map, generateUrl("uploadImg")).enqueue(object : Callback<CommonResponse<UploadImgResultModel>> {
            override fun onFailure(call: Call<CommonResponse<UploadImgResultModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorUploadImg.postValue( "601")
                }else{
                    errorUploadImg.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<UploadImgResultModel>>, response: Response<CommonResponse<UploadImgResultModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listUploadImgDta.postValue(response.body())
                    }else{
                        errorUploadImg.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorUploadImg.postValue(response.raw().code.toString())
                }
            }
        })
    }



    fun getApplication(phone:  Map<String, String>): LiveData<ResultStatus<CommonResponse<GetLoanModel>>> {
        return repository.getApplication(phone)
    }


//    val errorGetApplication = MutableLiveData<String>()
//    var listGetApplicationDta = MutableLiveData<CommonResponse<GetLoanModel>>()
//
//    fun getApplication(map: Map<String, String>){
//        RetrofitService.apiService().getLoan(map).enqueue(object : Callback<CommonResponse<GetLoanModel>> {
//            override fun onFailure(call: Call<CommonResponse<GetLoanModel>>, t: Throwable) {
//                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
//                    errorGetApplication.postValue( "601")
//                }else{
//                    errorGetApplication.postValue( "600")
//                }
//            }
//            override fun onResponse(call: Call<CommonResponse<GetLoanModel>>, response: Response<CommonResponse<GetLoanModel>>) {
//                if (response.isSuccessful) {
//                    if (response.body()!!.code == 200){
//                        listGetApplicationDta.postValue(response.body())
//                    }else{
//                        errorGetApplication.postValue(response.body()!!.code.toString())
//                    }
//                }else{
//                    errorGetApplication.postValue(response.raw().code.toString())
//                }
//            }
//        })
//    }
}