package com.example.kotlincashloan.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.Notification.ListNoticeModel
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationViewModel : ViewModel(){

    val errorNotice = MutableLiveData<String>()
    var listNoticeDta = MutableLiveData<CommonResponse<ArrayList<ResultListNoticeModel>>>()

    fun listNotice(map: Map<String, String>){
        RetrofitService.apiService().listNotice(map).enqueue(object : Callback<CommonResponse<ArrayList<ResultListNoticeModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ResultListNoticeModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorNotice.postValue( "601")
                }else{
                    errorNotice.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ResultListNoticeModel>>>, response: Response<CommonResponse<ArrayList<ResultListNoticeModel>>>) {
                if (response.isSuccessful) {
                    listNoticeDta.postValue(response.body())
                }else{
                    errorNotice.postValue(response.code().toString())
                }
            }
        })
    }
}