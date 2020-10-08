package com.example.kotlincashloan.ui.Loans

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
import java.io.IOException

class LoansViewModel: ViewModel() {
    private val repository = NetworkRepository()

    val errorNews = MutableLiveData<String>()
    var listNewsDta = MutableLiveData<CommonResponse<ArrayList<ListNewsResultModel>>>()
    var listNewsId: String = ""

    fun listNews(map: Map<String, String>){
        RetrofitService.apiService().listNews(map).enqueue(object :
            Callback<CommonResponse<ArrayList<ListNewsResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListNewsResultModel>>>, t: Throwable) {
                if (t is IOException) {
                    println()
                }else{
                    errorNews.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListNewsResultModel>>>, response: Response<CommonResponse<ArrayList<ListNewsResultModel>>>) {
                if (response.isSuccessful) {
                    listNewsDta.postValue(response.body())
                    listNewsId = response.code().toString()
                }else{
                    errorNews.postValue(response.code().toString())
                }
            }
        })
    }




    val errorGet = MutableLiveData<String>()
    var listGetDta = MutableLiveData<CommonResponse<GetNewsResultModel>>()

    fun getNews(map: Map<String, String>){
        RetrofitService.apiService().getNews(map).enqueue(object : Callback<CommonResponse<GetNewsResultModel>> {
            override fun onFailure(call: Call<CommonResponse<GetNewsResultModel>>, t: Throwable) {
                errorGet.postValue( "600")
            }
            override fun onResponse(call: Call<CommonResponse<GetNewsResultModel>>, response: Response<CommonResponse<GetNewsResultModel>>) {
                if (response.isSuccessful) {
                    listGetDta.postValue(response.body())
                }else{
                    errorGet.postValue(response.code().toString())
                }
            }
        })
    }

    val errorLoanInfo = MutableLiveData<String>()
    var listLoanInfo = MutableLiveData<CommonResponse<LoanInfoResultModel>>()
    var listLoanId: String = ""

    fun getLoanInfo(map: Map<String, String>){
        RetrofitService.apiService().loanInfo(map).enqueue(object : Callback<CommonResponse<LoanInfoResultModel>> {
            override fun onFailure(call: Call<CommonResponse<LoanInfoResultModel>>, t: Throwable) {
                errorLoanInfo.postValue( "600")
            }
            override fun onResponse(call: Call<CommonResponse<LoanInfoResultModel>>, response: Response<CommonResponse<LoanInfoResultModel>>) {
                if (response.isSuccessful) {
                    listLoanInfo.postValue(response.body())
                    listLoanId = response.code().toString()
                }else{
                    errorLoanInfo.postValue(response.code().toString())
                }
            }
        })
    }
}