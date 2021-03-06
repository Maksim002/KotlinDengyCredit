package com.example.kotlincashloan.ui.support

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.extension.generateUrl
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.MessageCode
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportViewModel : ViewModel(){
    private val repository = NetworkRepository()
    val handler = Handler()
    var refreshCode = false

    val error = MutableLiveData<String>()
    var listFaqDta = MutableLiveData<CommonResponse<ArrayList<ListFaqResultModel>>>()

    fun listFaq(map: Map<String, String>){
        RetrofitService.apiService().listFaq(map, generateUrl("listFaq")).enqueue(object :
            Callback<CommonResponse<ArrayList<ListFaqResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListFaqResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
                    error.postValue( "601")
                }else{
                    error.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListFaqResultModel>>>, response: Response<CommonResponse<ArrayList<ListFaqResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listFaqDta.postValue(response.body())
                    }else{
                        error.postValue(response.body()!!.code.toString())
                    }
                }else{
                    error.postValue(response.raw().code.toString())
                }
            }
        })
    }
}