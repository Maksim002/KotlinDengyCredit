package com.example.kotlincashloan.service.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveLoanResultModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null
)