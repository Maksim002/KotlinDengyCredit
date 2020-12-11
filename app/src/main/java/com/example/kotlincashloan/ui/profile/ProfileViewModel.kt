package com.example.kotlincashloan.ui.profile

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.profile.ClientInfoResultModel
import com.example.kotlincashloan.service.model.profile.GetResultOperationModel
import com.example.kotlincashloan.service.model.profile.ResultOperationModel
import com.example.kotlincashloan.service.model.profile.listGenderResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel(){
    val handler = Handler()
    var refreshCode = false

    val errorListOperation = MutableLiveData<String>()
    var listListOperationDta = MutableLiveData<CommonResponse<ArrayList<ResultOperationModel>>>()

    fun listOperation(map: Map<String, String>){
        RetrofitService.apiService().listOperation(map).enqueue(object : Callback<CommonResponse<ArrayList<ResultOperationModel>>> {
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
//                handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                    HomeActivity.alert.hide()
//                },550)
            }
        })
    }

    val errorGetOperation = MutableLiveData<String>()
    var listGetOperationDta = MutableLiveData<CommonResponse<GetResultOperationModel>>()

    fun getOperation(map: Map<String, String>){
        RetrofitService.apiService().getOperation(map).enqueue(object : Callback<CommonResponse<GetResultOperationModel>> {
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
                    HomeActivity.alert.hide()
                },500)
            }
        })
    }

    val errorClientInfo = MutableLiveData<String>()
    var listClientInfoDta = MutableLiveData<CommonResponse<ClientInfoResultModel>>()

    fun clientInfo(map: Map<String, String>){
        RetrofitService.apiService().clientInfo(map).enqueue(object : Callback<CommonResponse<ClientInfoResultModel>> {
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
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    HomeActivity.alert.hide()
                },500)
            }
        })
    }

    val errorListGender = MutableLiveData<String>()
    var listGenderDta = MutableLiveData<CommonResponse<ArrayList<listGenderResultModel>>>()

    fun listGender(map: Map<String, String>){
        RetrofitService.apiService().listGender(map).enqueue(object : Callback<CommonResponse<ArrayList<listGenderResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<listGenderResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListGender.postValue( "601")
                }else{
                    errorListGender.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<listGenderResultModel>>>, response: Response<CommonResponse<ArrayList<listGenderResultModel>>>) {
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
                    HomeActivity.alert.hide()
                },500)
            }
        })
    }
}