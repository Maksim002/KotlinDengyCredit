package com.example.kotlincashloan.service.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ClientInfoResultModel (
    @SerializedName("login")
    @Expose
    var login: String? = null,

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

    @SerializedName("second_phone")
    @Expose
    var secondPhone: String? = null,

    @SerializedName("question")
    @Expose
    var question: String? = null,

    @SerializedName("response")
    @Expose
    var response: String? = null,

    @SerializedName("first_phone_country_id")
    @Expose
    var phoneFirst: String? = null,

    @SerializedName("second_phone_country_id")
    @Expose
    var phoneSecond: String? = null
) : Serializable