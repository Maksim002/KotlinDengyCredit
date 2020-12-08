package com.example.kotlincashloan.ui.notification

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.Notification.ListNoticeModel
import com.example.kotlincashloan.service.model.Notification.ResultDetailNoticeModel
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationViewModel : ViewModel(){
    val handler = Handler()
    var refreshCode = false

    val errorNotice = MutableLiveData<String>()
    var listNoticeDta = MutableLiveData<CommonResponse<ArrayList<ResultListNoticeModel>>>()

    fun listNotice(map: Map<String, String>){
        if (refreshCode != true){
            HomeActivity.alert.show()
        }
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
                    if (response.body()!!.code == 200){
                        listNoticeDta.postValue(response.body())
                    }else{
                        errorNotice.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorNotice.postValue(response.code().toString())
                }
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    HomeActivity.alert.hide()
                },400)
            }
        })
    }


    val errorDetailNotice = MutableLiveData<String>()
    var listNoticeDetailDta = MutableLiveData<CommonResponse<ResultDetailNoticeModel>>()

    fun getNotice(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().getNotice(map).enqueue(object : Callback<CommonResponse<ResultDetailNoticeModel>> {
            override fun onFailure(call: Call<CommonResponse<ResultDetailNoticeModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorDetailNotice.postValue( "601")
                }else{
                    errorDetailNotice.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ResultDetailNoticeModel>>, response: Response<CommonResponse<ResultDetailNoticeModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listNoticeDetailDta.postValue(response.body())
                    }else{
                        errorDetailNotice.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorDetailNotice.postValue(response.code().toString())
                }
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    HomeActivity.alert.hide()
                },500)
            }
        })
    }
}