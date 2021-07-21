package com.example.kotlincashloan.ui.loans

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.extension.generateUrl
import com.example.kotlincashloan.service.model.Loans.*
import com.example.kotlincashloan.service.model.login.SaveLoanModel
import com.example.kotlincashloan.service.model.login.SaveLoanRejectModel
import com.example.kotlincashloan.service.model.login.SaveLoanResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.MessageCode
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.example.kotlinscreenscanner.service.model.CommonResponseReject
import com.example.kotlinscreenscanner.service.model.ResultPhoneModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.ResultStatus
import com.timelysoft.tsjdomcom.service.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Url

class LoansViewModel: ViewModel() {
    private val repository = NetworkRepository()
    val handler = Handler()
    var refreshCode = ""

    val errorNews = MutableLiveData<String>()
    var listNewsDta = MutableLiveData<CommonResponse<ArrayList<ListNewsResultModel>>>()
    var listNewsId: String = ""

    fun listNews(map: Map<String, String>){
        RetrofitService.apiService().listNews(map, generateUrl("listNews")).enqueue(object :
            Callback<CommonResponse<ArrayList<ListNewsResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListNewsResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().getNews(map, generateUrl("getNews")).enqueue(object : Callback<CommonResponse<GetNewsResultModel>> {
            override fun onFailure(call: Call<CommonResponse<GetNewsResultModel>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
                    MainActivity.alert.hide()
                },400)
            }
        })
    }

    val errorLoanInfo = MutableLiveData<String>()
    var listLoanInfo = MutableLiveData<CommonResponse<LoanInfoResultModel>>()
    var listLoanId: String = ""

    fun getLoanInfo(map: Map<String, String>){
        RetrofitService.apiService().loanInfo(map, generateUrl("loanInfo")).enqueue(object : Callback<CommonResponse<LoanInfoResultModel>> {
            override fun onFailure(call: Call<CommonResponse<LoanInfoResultModel>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
                HomeActivity.alert.hide()
            }
        })
    }



    fun getLoanInfoDta(phone:  Map<String, String>): LiveData<ResultStatus<CommonResponseReject<LoanInResultModel>>> {
        return repository.getLoanInfoDta(phone)
    }

    //defaultList Стандартный список ответов.
    val errorDefaultList = MutableLiveData<String>()
    var getDefaultListDta = MutableLiveData<CommonResponse<ArrayList<DefaultListModel>>>()

    fun defaultList(map: Map<String, String>){
        HomeActivity.alert.show()
        RetrofitService.apiService().defaultList(map, generateUrl("defaultList")).enqueue(object : Callback<CommonResponse<ArrayList<DefaultListModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<DefaultListModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listFamilyStatus(map, generateUrl("listFamilyStatus")).enqueue(object : Callback<CommonResponse<ArrayList<ListFamilyStatusModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListFamilyStatusModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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


    //listIncome Список доходов.
    val errorListIncome = MutableLiveData<String>()
    var getListIncomeDta = MutableLiveData<CommonResponse<ArrayList<ListIncomeResultModel>>>()

    fun listIncome(map: Map<String, String>){
        RetrofitService.apiService().listIncome(map, generateUrl("listIncome")).enqueue(object : Callback<CommonResponse<ArrayList<ListIncomeResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListIncomeResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listNumbers(map, generateUrl("listNumbers")).enqueue(object : Callback<CommonResponse<ArrayList<ListNumbersResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListNumbersResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listTypeIncome(map, generateUrl("listTypeIncome")).enqueue(object : Callback<CommonResponse<ArrayList<ListTypeIncomeModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListTypeIncomeModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listYears(map, generateUrl("listYears")).enqueue(object : Callback<CommonResponse<ArrayList<ListYearsResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListYearsResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listFamily(map, generateUrl("listFamily")).enqueue(object : Callback<CommonResponse<ArrayList<ListFamilyResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListFamilyResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listWork(map, generateUrl("listWork")).enqueue(object : Callback<CommonResponse<ArrayList<ListWorkResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListWorkResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listTypeWork(map, generateUrl("listTypeWork")).enqueue(object : Callback<CommonResponse<ArrayList<ListTypeWorkModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListTypeWorkModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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
        RetrofitService.apiService().listCity(map, generateUrl("listCity")).enqueue(object : Callback<CommonResponse<ArrayList<ListCityResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<ListCityResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
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



    //saveLoan Отправка на сервер
    val errorSaveLoan = MutableLiveData<String>()
    var getSaveLoan = MutableLiveData<SaveLoanModel>()

    fun saveLoan(map: Map<String, String>){
        RetrofitService.apiService().saveLoan(map, generateUrl("saveLoan")).enqueue(object : Callback<SaveLoanModel> {
            override fun onFailure(call: Call<SaveLoanModel>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
                    errorSaveLoan.postValue( "601")
                }else{
                    errorSaveLoan.postValue( "600")
                }
            }
            override fun onResponse(call: Call<SaveLoanModel>, response: Response<SaveLoanModel>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getSaveLoan.postValue(response.body())
                    }else{
                        errorSaveLoan.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorSaveLoan.postValue(response.code().toString())
                }
//                GetLoanActivity.alert.hide()
            }
        })
    }

    fun saveLoans(phone:  Map<String, String>): LiveData<ResultStatus<CommonResponseReject<SaveLoanResultModel>>> {
        return repository.saveLoans(phone)
    }

    //saveLoanImg Сохронение на сервер картинок
    val errorSaveLoanImg = MutableLiveData<String>()
    var getSaveLoanImg = MutableLiveData<SaveLoanModel>()

    fun saveLoanImg(map: Map<String, String>){
        RetrofitService.apiService().saveLoanImg(map, generateUrl("saveLoan")).enqueue(object : Callback<SaveLoanModel> {
            override fun onFailure(call: Call<SaveLoanModel>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
                    errorSaveLoanImg.postValue( "601")
                }else{
                    errorSaveLoanImg.postValue( "600")
                }
            }
            override fun onResponse(call: Call<SaveLoanModel>, response: Response<SaveLoanModel>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        getSaveLoanImg.postValue(response.body())
                    }else{
                        errorSaveLoanImg.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorSaveLoanImg.postValue(response.code().toString())
                }
                GetLoanActivity.alert.hide()
            }
        })
    }

    // Список даступных стран
    val errorListAvailableSix = MutableLiveData<String>()
    var listAvailableSixDta = MutableLiveData<CommonResponse<ArrayList<SixNumResultModel>>>()

    fun listAvailableSix(map: Map<String, String>){
        RetrofitService.apiService().listAvailableSix(map, generateUrl("listAvailableCountry")).enqueue(object : Callback<CommonResponse<ArrayList<SixNumResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<SixNumResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
                    errorListAvailableSix.postValue( "601")
                }else{
                    errorListAvailableSix.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<SixNumResultModel>>>, response: Response<CommonResponse<ArrayList<SixNumResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listAvailableSixDta.postValue(response.body())
                    }else{
                        errorListAvailableSix.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListAvailableSix.postValue(response.raw().code.toString())
                }
            }
        })
    }

    //listEntryGoal Список целей въезда.
    val errorListEntryGoal = MutableLiveData<String>()
    var listEntryGoal = MutableLiveData<CommonResponse<ArrayList<EntryGoalResultModel>>>()

    fun listEntryGoal(map: Map<String, String>){
        RetrofitService.apiService().listEntryGoal(map, generateUrl("listEntryGoal")).enqueue(object : Callback<CommonResponse<ArrayList<EntryGoalResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<EntryGoalResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
                    errorListEntryGoal.postValue( "601")
                }else{
                    errorListEntryGoal.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<EntryGoalResultModel>>>, response: Response<CommonResponse<ArrayList<EntryGoalResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listEntryGoal.postValue(response.body())
                    }else{
                        errorListEntryGoal.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListEntryGoal.postValue(response.code().toString())
                }
            }
        })
    }

    //listTypeContract Список типов договора.
    val errorListTypeContract = MutableLiveData<String>()
    var listTypeContract = MutableLiveData<CommonResponse<ArrayList<TypeContractResultModel>>>()

    fun listTypeContract(map: Map<String, String>){
        RetrofitService.apiService().listTypeContract(map, generateUrl("listTypeContract")).enqueue(object : Callback<CommonResponse<ArrayList<TypeContractResultModel>>> {
            override fun onFailure(call: Call<CommonResponse<ArrayList<TypeContractResultModel>>>, t: Throwable) {
                if (t.localizedMessage == MessageCode.MESSAGE_1.toString() || t.localizedMessage == MessageCode.MESSAGE_2.toString()){
                    errorListTypeContract.postValue( "601")
                }else{
                    errorListTypeContract.postValue( "600")
                }
            }
            override fun onResponse(call: Call<CommonResponse<ArrayList<TypeContractResultModel>>>, response: Response<CommonResponse<ArrayList<TypeContractResultModel>>>) {
                if (response.isSuccessful) {
                    if (response.body()!!.code == 200){
                        listTypeContract.postValue(response.body())
                    }else{
                        errorListTypeContract.postValue(response.body()!!.code.toString())
                    }
                }else{
                    errorListTypeContract.postValue(response.code().toString())
                }
            }
        })
    }
}