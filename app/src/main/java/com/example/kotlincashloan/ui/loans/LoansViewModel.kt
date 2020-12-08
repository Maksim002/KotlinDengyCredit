package com.example.kotlincashloan.ui.loans

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.Loans.GetNewsResultModel
import com.example.kotlincashloan.service.model.Loans.ListNewsResultModel
import com.example.kotlincashloan.service.model.Loans.LoanInfoResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoansViewModel: ViewModel() {
    private val repository = NetworkRepository()
    val handler = Handler()
    var refreshCode = false

    val errorNews = MutableLiveData<String>()
    var listNewsDta = MutableLiveData<CommonResponse<ArrayList<ListNewsResultModel>>>()
    var listNewsId: String = ""

    fun listNews(map: Map<String, String>){
        RetrofitService.apiService().listNews(map).enqueue(object :
            Callback<CommonResponse<ArrayList<ListNewsResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListNewsResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorNews.postValue( "601")
                }else{
                    errorNews.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListNewsResultModel>>>, response: Response<CommonResponse<ArrayList<ListNewsResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listNewsDta.postValue(response.body())
                        if (response.body()!!.error == null){
                            listNewsId = response.code().toString()
                        }else{
                            listNewsId = response.body()!!.error.toString()
                        }
                    }else{
                        errorNews.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorNews.postValue(response.code().toString())
                }
            }
        })
    }

    val errorGet = MutableLiveData<String>()
    var listGetDta = MutableLiveData<CommonResponse<GetNewsResultModel>>()

    fun getNews(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().getNews(map).enqueue(object : Callback<CommonResponse<GetNewsResultModel>> {
            override fun onFailure(call: Call<CommonResponse<GetNewsResultModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorGet.postValue( "601")
                }else{
                    errorGet.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<GetNewsResultModel>>, response: Response<CommonResponse<GetNewsResultModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listGetDta.postValue(response.body())
                    }else{
                        errorGet.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorGet.postValue(response.code().toString())
                }
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    HomeActivity.alert.hide()
                },400)
            }
        })
    }

    val errorLoanInfo = MutableLiveData<String>()
    var listLoanInfo = MutableLiveData<CommonResponse<LoanInfoResultModel>>()
    var listLoanId: String = ""

    fun getLoanInfo(map: Map<String, String>){
        RetrofitService.apiService().loanInfo(map).enqueue(object : Callback<CommonResponse<LoanInfoResultModel>> {
            override fun onFailure(call: Call<CommonResponse<LoanInfoResultModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorLoanInfo.postValue( "601")
                }else{
                    errorLoanInfo.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<LoanInfoResultModel>>, response: Response<CommonResponse<LoanInfoResultModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listLoanInfo.postValue(response.body())
                        if (response.body()!!.error == null){
                            listLoanId = response.code().toString()
                        }else{
                            listLoanId = response.body()!!.error.toString()
                        }
                    }else{
                        errorLoanInfo.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorLoanInfo.postValue(response.code().toString())
                }
            }
        })
    }
}