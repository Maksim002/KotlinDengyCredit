package com.example.kotlincashloan.ui.loans

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.Loans.*
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


    val errorGetLoanInfo = MutableLiveData<String>()
    var getLoanInfoDta = MutableLiveData<CommonResponse<LoanInResultModel>>()

    fun getInfo(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().getLoanInfo(map).enqueue(object : Callback<CommonResponse<LoanInResultModel>> {
            override fun onFailure(call: Call<CommonResponse<LoanInResultModel>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorGetLoanInfo.postValue( "601")
                }else{
                    errorGetLoanInfo.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<LoanInResultModel>>, response: Response<CommonResponse<LoanInResultModel>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getLoanInfoDta.postValue(response.body())
                    }else{
                        errorGetLoanInfo.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorGetLoanInfo.postValue(response.code().toString())
                }
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    HomeActivity.alert.hide()
                },400)
            }
        })
    }

    //defaultList Стандартный список ответов.
    val errorDefaultList = MutableLiveData<String>()
    var getDefaultListDta = MutableLiveData<CommonResponse<ArrayList<DefaultListModel>>>()

    fun defaultList(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().defaultList(map).enqueue(object : Callback<CommonResponse<ArrayList<DefaultListModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<DefaultListModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorDefaultList.postValue( "601")
                }else{
                    errorDefaultList.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<DefaultListModel>>>, response: Response<CommonResponse<ArrayList<DefaultListModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getDefaultListDta.postValue(response.body())
                    }else{
                        errorDefaultList.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorDefaultList.postValue(response.code().toString())
                }
            }
        })
    }


    //listFamilyStatus Список семейных положений.
    val errorListFamilyStatus = MutableLiveData<String>()
    var getListFamilyStatusDta = MutableLiveData<CommonResponse<ArrayList<ListFamilyStatusModel>>>()

    fun listFamilyStatus(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listFamilyStatus(map).enqueue(object : Callback<CommonResponse<ArrayList<ListFamilyStatusModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListFamilyStatusModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListFamilyStatus.postValue( "601")
                }else{
                    errorListFamilyStatus.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListFamilyStatusModel>>>, response: Response<CommonResponse<ArrayList<ListFamilyStatusModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListFamilyStatusDta.postValue(response.body())
                    }else{
                        errorListFamilyStatus.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListFamilyStatus.postValue(response.code().toString())
                }
            }
        })
    }


    //listFamilyStatus Список доходов.
    val errorListIncome = MutableLiveData<String>()
    var getListIncomeDta = MutableLiveData<CommonResponse<ArrayList<ListIncomeResultModel>>>()

    fun listIncome(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listIncome(map).enqueue(object : Callback<CommonResponse<ArrayList<ListIncomeResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListIncomeResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListIncome.postValue( "601")
                }else{
                    errorListIncome.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListIncomeResultModel>>>, response: Response<CommonResponse<ArrayList<ListIncomeResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListIncomeDta.postValue(response.body())
                    }else{
                        errorListIncome.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListIncome.postValue(response.code().toString())
                }
            }
        })
    }


    //listNumbers Список количеств.
    val errorListNumbers = MutableLiveData<String>()
    var getListNumbersDta = MutableLiveData<CommonResponse<ArrayList<ListNumbersResultModel>>>()

    fun listNumbers(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listNumbers(map).enqueue(object : Callback<CommonResponse<ArrayList<ListNumbersResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListNumbersResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListNumbers.postValue( "601")
                }else{
                    errorListNumbers.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListNumbersResultModel>>>, response: Response<CommonResponse<ArrayList<ListNumbersResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListNumbersDta.postValue(response.body())
                    }else{
                        errorListNumbers.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListNumbers.postValue(response.code().toString())
                }
            }
        })
    }


    //listTypeIncome Список типов доп. дохода.
    val errorListTypeIncome = MutableLiveData<String>()
    var getListTypeIncomeDta = MutableLiveData<CommonResponse<ArrayList<ListTypeIncomeModel>>>()

    fun listTypeIncome(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listTypeIncome(map).enqueue(object : Callback<CommonResponse<ArrayList<ListTypeIncomeModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListTypeIncomeModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListTypeIncome.postValue( "601")
                }else{
                    errorListTypeIncome.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListTypeIncomeModel>>>, response: Response<CommonResponse<ArrayList<ListTypeIncomeModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListTypeIncomeDta.postValue(response.body())
                    }else{
                        errorListTypeIncome.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListTypeIncome.postValue(response.code().toString())
                }
            }
        })
    }


    //listYears Список типов доп. дохода.
    val errorListYears = MutableLiveData<String>()
    var getListYearsDta = MutableLiveData<CommonResponse<ArrayList<ListYearsResultModel>>>()

    fun listYears(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listYears(map).enqueue(object : Callback<CommonResponse<ArrayList<ListYearsResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListYearsResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListYears.postValue( "601")
                }else{
                    errorListYears.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListYearsResultModel>>>, response: Response<CommonResponse<ArrayList<ListYearsResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListYearsDta.postValue(response.body())
                    }else{
                        errorListYears.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListYears.postValue(response.code().toString())
                }
            }
        })
    }


    //listFamily Список кем приходится.
    val errorListFamily = MutableLiveData<String>()
    var getListFamilyDta = MutableLiveData<CommonResponse<ArrayList<ListFamilyResultModel>>>()

    fun listFamily(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listFamily(map).enqueue(object : Callback<CommonResponse<ArrayList<ListFamilyResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListFamilyResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListFamily.postValue( "601")
                }else{
                    errorListFamily.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListFamilyResultModel>>>, response: Response<CommonResponse<ArrayList<ListFamilyResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListFamilyDta.postValue(response.body())
                    }else{
                        errorListFamily.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListFamily.postValue(response.code().toString())
                }
            }
        })
    }


    //listWork Список работа.
    val errorListWork = MutableLiveData<String>()
    var getListWorkDta = MutableLiveData<CommonResponse<ArrayList<ListWorkResultModel>>>()

    fun listWork(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listWork(map).enqueue(object : Callback<CommonResponse<ArrayList<ListWorkResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListWorkResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListWork.postValue( "601")
                }else{
                    errorListWork.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListWorkResultModel>>>, response: Response<CommonResponse<ArrayList<ListWorkResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListWorkDta.postValue(response.body())
                    }else{
                        errorListWork.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListWork.postValue(response.code().toString())
                }
            }
        })
    }


    //listTypeWork Список тип работы.
    val errorListTypeWork = MutableLiveData<String>()
    var getListTypeWorkDta = MutableLiveData<CommonResponse<ArrayList<ListTypeWorkModel>>>()

    fun listTypeWork(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listTypeWork(map).enqueue(object : Callback<CommonResponse<ArrayList<ListTypeWorkModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListTypeWorkModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListTypeWork.postValue( "601")
                }else{
                    errorListTypeWork.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListTypeWorkModel>>>, response: Response<CommonResponse<ArrayList<ListTypeWorkModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListTypeWorkDta.postValue(response.body())
                    }else{
                        errorListTypeWork.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListTypeWork.postValue(response.code().toString())
                }
            }
        })
    }


    //listCity Список городов.
    val errorListCity = MutableLiveData<String>()
    var getListCityDta = MutableLiveData<CommonResponse<ArrayList<ListCityResultModel>>>()

    fun listCity(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().listCity(map).enqueue(object : Callback<CommonResponse<ArrayList<ListCityResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListCityResultModel>>>, t: Throwable) {
                if (t.localizedMessage != "End of input at line 1 column 1 path \$"){
                    errorListCity.postValue( "601")
                }else{
                    errorListCity.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<ListCityResultModel>>>, response: Response<CommonResponse<ArrayList<ListCityResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getListCityDta.postValue(response.body())
                    }else{
                        errorListCity.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListCity.postValue(response.code().toString())
                }
            }
        })
    }
}