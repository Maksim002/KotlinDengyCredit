package com.example.kotlincashloan.ui.support

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportViewModel : ViewModel(){
    private val repository = NetworkRepository()

    val error = MutableLiveData<String>()
    var listFaqDta = MutableLiveData<CommonResponse<ArrayList<ListFaqResultModel>>>()

    fun listFaq(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listFaq(map).enqueue(object :
            Callback<CommonResponse<ArrayList<ListFaqResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListFaqResultModel>>>, t: Throwable) {
                error.postValue( "600")
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListFaqResultModel>>>, response: Response<CommonResponse<ArrayList<ListFaqResultModel>>>) {
                if (response.isSuccessful) {
                    listFaqDta.postValue(response.body())
                }else{
                    error.postValue(response.code().toString())
                }
                HomeActivity.alert.hide()
            }
        })
    }
}