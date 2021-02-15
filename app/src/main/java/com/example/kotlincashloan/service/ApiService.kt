package com.timelysoft.tsjdomcom.service


import com.example.kotlincashloan.service.model.Loans.*
import com.example.kotlincashloan.service.model.Notification.ResultDetailNoticeModel
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
import com.example.kotlincashloan.service.model.login.SaveLoanModel
import com.example.kotlincashloan.service.model.profile.*
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.example.kotlincashloan.service.model.recovery.ListSupportTypeResultModel
import com.example.kotlincashloan.service.model.recovery.RecoveryAccessResultModel
import com.example.kotlincashloan.service.model.recovery.SupportTicketResultModel
import com.example.kotlinscreenscanner.service.model.*
import com.example.myapplication.model.ResultModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import kotlin.collections.ArrayList

interface ApiService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("login?token=oYyxhIFgJjAb")
    suspend fun auth(@FieldMap params: Map<String, String>): Response<CommonResponse<ResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("checkPhone?token=oYyxhIFgJjAb")
    suspend fun numberPhone(@FieldMap params: Map<String, String>): Response<CommonResponse<ResultPhoneModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("checkCode?token=oYyxhIFgJjAb")
    suspend fun smsConfirmation(@FieldMap params: Map<String, Int>): Response<CommonResponse<SmsResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("registration?token=oYyxhIFgJjAb")
    suspend fun questionnaire(@FieldMap map: Map<String, String>): Response<CommonResponse<QuestionnaireResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listGender?token=oYyxhIFgJjAb")
    suspend fun listGender(@FieldMap params: Map<String, Int>): Response<CommonResponse<ArrayList<ListGenderResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listNationality?token=oYyxhIFgJjAb")
    suspend fun listNationality(@FieldMap params: Map<String, Int>): Response<CommonResponse<ArrayList<ListNationalityResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listSecretQuestion?token=oYyxhIFgJjAb")
    suspend fun listSecretQuestion(@FieldMap params: Map<String, Int>): Response<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listAvailableCountry?token=oYyxhIFgJjAb")
    suspend fun listAvailableCountry(@FieldMap params: Map<String, Int>): Response<CommonResponse<ArrayList<CounterResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("recoveryAccess?token=oYyxhIFgJjAb")
    suspend fun recoveryAccess(@FieldMap params: Map<String, String>): Response<CommonResponse<RecoveryAccessResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listSupportType?token=oYyxhIFgJjAb")
    suspend fun listSupportType(@FieldMap params: Map<String, Int>): Response<CommonResponse<ArrayList<ListSupportTypeResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("supportTicket?token=oYyxhIFgJjAb")
    suspend fun supportTicket(@FieldMap params: Map<String, String>): Response<CommonResponse<SupportTicketResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listFaq?token=oYyxhIFgJjAb")
    fun listFaq(@FieldMap params: Map<String, String>): Call<CommonResponse<ArrayList<ListFaqResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listNews?token=oYyxhIFgJjAb")
    fun listNews(@FieldMap params: Map<String, String>): Call<CommonResponse<ArrayList<ListNewsResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("getNews?token=oYyxhIFgJjAb")
    fun getNews(@FieldMap params: Map<String, String>):Call<CommonResponse<GetNewsResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("loanInfo?token=oYyxhIFgJjAb")
    fun loanInfo(@FieldMap params: Map<String, String>):Call<CommonResponse<LoanInfoResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listNotice?token=oYyxhIFgJjAb")
    fun listNotice(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ResultListNoticeModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("getNotice?token=oYyxhIFgJjAb")
    fun getNotice(@FieldMap params: Map<String, String>):Call<CommonResponse<ResultDetailNoticeModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listOperation?token=oYyxhIFgJjAb")
    fun listOperation(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ResultOperationModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("getOperation?token=oYyxhIFgJjAb")
    fun getOperation(@FieldMap params: Map<String, String>):Call<CommonResponse<GetResultOperationModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("clientInfo?token=oYyxhIFgJjAb")
    fun clientInfo(@FieldMap params: Map<String, String>):Call<CommonResponse<ClientInfoResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listGender?token=oYyxhIFgJjAb")
    fun listGender(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListGenderResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listNationality?token=oYyxhIFgJjAb")
    fun getListNationality(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListNationalityResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listAvailableCountry?token=oYyxhIFgJjAb")
    fun listAvailableCountry(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<CounterNumResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listAvailableCountry?token=oYyxhIFgJjAb")
    fun listAvailableSix(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<SixNumResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listSecretQuestion?token=oYyxhIFgJjAb")
    fun listSecretQuestion(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("saveProfile?token=oYyxhIFgJjAb")
    fun saveProfile(@FieldMap params: Map<String, String>):Call<CommonResponse<SaveProfileResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("checkPassword?token=oYyxhIFgJjAb")
    fun checkPassword(@FieldMap params: Map<String, String>):Call<CommonResponse<CheckPasswordResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("getImg?token=oYyxhIFgJjAb")
    fun getImg(@FieldMap params: Map<String, String>):Call<CommonResponse<GetImgResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("uploadImg?token=oYyxhIFgJjAb")
    fun uploadImg(@FieldMap params: Map<String, String>):Call<CommonResponse<UploadImgResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("getLoanInfo?token=oYyxhIFgJjAb")
    fun getLoanInfo(@FieldMap params: Map<String, String>):Call<CommonResponse<LoanInResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("defaultList?token=oYyxhIFgJjAb")
    fun defaultList(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<DefaultListModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listFamilyStatus?token=oYyxhIFgJjAb")
    fun listFamilyStatus(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListFamilyStatusModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listIncome?token=oYyxhIFgJjAb")
    fun listIncome(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListIncomeResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listNumbers?token=oYyxhIFgJjAb")
    fun listNumbers(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListNumbersResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listTypeIncome?token=oYyxhIFgJjAb")
    fun listTypeIncome(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListTypeIncomeModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listYears?token=oYyxhIFgJjAb")
    fun listYears(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListYearsResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listFamily?token=oYyxhIFgJjAb")
    fun listFamily(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListFamilyResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listWork?token=oYyxhIFgJjAb")
    fun listWork(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListWorkResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listTypeWork?token=oYyxhIFgJjAb")
    fun listTypeWork(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListTypeWorkModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listCity?token=oYyxhIFgJjAb")
    fun listCity(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListCityResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listTrafficSource?token=oYyxhIFgJjAb")
    suspend fun listTrafficSource(@FieldMap map: Map<String, Int>): Response<CommonResponse<ArrayList<ListTrafficSource>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("saveLoan?token=oYyxhIFgJjAb")
    fun saveLoan(@FieldMap params: Map<String, String>):Call<SaveLoanModel>


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("listEntryGoal?token=oYyxhIFgJjAb")
    fun listEntryGoal(@FieldMap params: Map<String, String>):Call<CommonResponse<ArrayList<ListEntryGoalResult>>>
}

