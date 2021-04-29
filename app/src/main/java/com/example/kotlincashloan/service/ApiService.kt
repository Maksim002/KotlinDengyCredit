package com.timelysoft.tsjdomcom.service


import android.net.Uri
import com.example.kotlincashloan.service.model.Loans.*
import com.example.kotlincashloan.service.model.Notification.ResultDetailNoticeModel
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
import com.example.kotlincashloan.service.model.login.SaveLoanModel
import com.example.kotlincashloan.service.model.login.SaveLoanResultModel
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
    @POST
    suspend fun auth(@FieldMap params: Map<String, String>, @Url string: String): Response<CommonResponse<ResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun numberPhone(@FieldMap params: Map<String, String>, @Url string: String): Response<CommonResponse<ResultPhoneModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun smsConfirmation(@FieldMap params: Map<String, Int>, @Url string: String): Response<CommonResponse<SmsResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun questionnaire(@FieldMap map: Map<String, String>, @Url string: String): Response<CommonResponse<QuestionnaireResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun listGender(@FieldMap params: Map<String, Int>, @Url string: String): Response<CommonResponse<ArrayList<ListGenderResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun listNationality(@FieldMap params: Map<String, Int>, @Url string: String): Response<CommonResponse<ArrayList<ListNationalityResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun listSecretQuestion(@FieldMap params: Map<String, Int>, @Url string: String): Response<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun listAvailableCountry(@FieldMap params: Map<String, Int>, @Url string: String): Response<CommonResponse<ArrayList<CounterResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun recoveryAccess(@FieldMap params: Map<String, String>, @Url string: String): Response<CommonResponse<RecoveryAccessResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun listSupportType(@FieldMap params: Map<String, Int>, @Url string: String): Response<CommonResponse<ArrayList<ListSupportTypeResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun supportTicket(@FieldMap params: Map<String, String>, @Url string: String): Response<CommonResponse<SupportTicketResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listFaq(@FieldMap params: Map<String, String>, @Url string: String): Call<CommonResponse<ArrayList<ListFaqResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listNews(@FieldMap params: Map<String, String>, @Url string: String): Call<CommonResponse<ArrayList<ListNewsResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun getNews(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<GetNewsResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun loanInfo(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<LoanInfoResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listNotice(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ResultListNoticeModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun getNotice(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ResultDetailNoticeModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listOperation(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ResultOperationModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun getOperation(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<GetResultOperationModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun clientInfo(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ClientInfoResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listGender(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListGenderResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun getListNationality(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListNationalityResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listAvailableCountry(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<CounterNumResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listAvailableSix(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<SixNumResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listSecretQuestion(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListSecretQuestionResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun saveProfile(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<SaveProfileResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun checkPassword(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<CheckPasswordResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun getImg(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<GetImgResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun getImgLoan(@FieldMap map: Map<String, String>, @Url string: String): Response<CommonResponse<GetImgResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun uploadImg(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<UploadImgResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun getLoanInfo(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<LoanInResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun defaultList(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<DefaultListModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listFamilyStatus(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListFamilyStatusModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listIncome(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListIncomeResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listNumbers(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListNumbersResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listTypeIncome(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListTypeIncomeModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listYears(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListYearsResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listFamily(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListFamilyResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listWork(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListWorkResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listTypeWork(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListTypeWorkModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listCity(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ListCityResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun listTrafficSource(@FieldMap map: Map<String, Int>, @Url string: String): Response<CommonResponse<ArrayList<ListTrafficSource>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun saveLoan(@FieldMap params: Map<String, String>, @Url string: String):Call<SaveLoanModel>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun saveLoanImg(@FieldMap params: Map<String, String>, @Url string: String):Call<SaveLoanModel>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listEntryGoal(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<EntryGoalResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listTypeContract(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<TypeContractResultModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun saveLoans(@FieldMap params: Map<String, String>, @Url string: String): Response<CommonResponseReject<SaveLoanResultModel>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    fun listLoan(@FieldMap params: Map<String, String>, @Url string: String):Call<CommonResponse<ArrayList<ResultApplicationModel>>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST
    suspend fun getLoan(@FieldMap params: Map<String, String>, @Url string: String): Response<CommonResponse<GetLoanModel>>
}

