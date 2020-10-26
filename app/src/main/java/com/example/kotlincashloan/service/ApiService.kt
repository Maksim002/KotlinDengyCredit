package com.timelysoft.tsjdomcom.service


import com.example.kotlincashloan.service.model.Loans.GetNewsResultModel
import com.example.kotlincashloan.service.model.Loans.ListNewsResultModel
import com.example.kotlincashloan.service.model.Loans.LoanInfoResultModel
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
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
}

