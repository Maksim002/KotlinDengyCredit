package com.example.kotlincashloan.service.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetLoanModel(
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("date")
    @Expose
    var date: String? = null,

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null,

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null,

    @SerializedName("second_name")
    @Expose
    var secondName: String? = null,

    @SerializedName("u_date")
    @Expose
    var uDate: String? = null,

    @SerializedName("gender")
    @Expose
    var gender: String? = null,

    @SerializedName("nationality")
    @Expose
    var nationality: String? = null,

    @SerializedName("first_phone")
    @Expose
    var firstPhone: String? = null,

    @SerializedName("loan_type")
    @Expose
    var loanType: String? = null,

    @SerializedName("loan_term")
    @Expose
    var loanTerm: String? = null,

    @SerializedName("loan_sum")
    @Expose
    var loanSum: String? = null,

    @SerializedName("city")
    @Expose
    var city: String? = null,

    @SerializedName("address")
    @Expose
    var address: String? = null,

    @SerializedName("family_status")
    @Expose
    var familyStatus: String? = null,

    @SerializedName("count_family")
    @Expose
    var countFamily: String? = null,

    @SerializedName("count_family_work")
    @Expose
    var countFamilyWork: String? = null,

    @SerializedName("live_in_ru")
    @Expose
    var liveInRu: String? = null,

    @SerializedName("bank_card")
    @Expose
    var bankCard: String? = null,

    @SerializedName("second_phone")
    @Expose
    var secondPhone: String? = null,

    @SerializedName("type_work")
    @Expose
    var typeWork: String? = null,

    @SerializedName("work_exp_ru")
    @Expose
    var workExpRu: String? = null,

    @SerializedName("work_exp_last")
    @Expose
    var workExpLast: String? = null,

    @SerializedName("income")
    @Expose
    var income: String? = null,

    @SerializedName("sub_income_id")
    @Expose
    var subIncomeId: String? = null,

    @SerializedName("sub_income_sum")
    @Expose
    var subIncomeSum: String? = null,

    @SerializedName("place_work")
    @Expose
    var placeWork: String? = null,

    @SerializedName("reg_date")
    @Expose
    var regDate: String? = null,

    @SerializedName("entry_date")
    @Expose
    var entryDate: String? = null,

    @SerializedName("entry_goal")
    @Expose
    var entryGoal: String? = null,

    @SerializedName("re_last_name")
    @Expose
    var reLastName: String? = null,

    @SerializedName("re_first_name")
    @Expose
    var reFirstName: String? = null,

    @SerializedName("re_second_name")
    @Expose
    var reSecondName: String? = null,

    @SerializedName("re_type")
    @Expose
    var reType: String? = null,

    @SerializedName("re_phone")
    @Expose
    var rePhone: String? = null,

    @SerializedName("step")
    @Expose
    var step: String? = null,

    @SerializedName("type_contract")
    @Expose
    var typeContract: String? = null,

    @SerializedName("docs")
    @Expose
    var docs: ArrayList<String>? = null
) : Serializable