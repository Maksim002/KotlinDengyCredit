package com.example.kotlincashloan.service.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveProfileResultModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null
)