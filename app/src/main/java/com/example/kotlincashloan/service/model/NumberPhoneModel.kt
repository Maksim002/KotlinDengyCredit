package com.example.kotlinscreenscanner.service.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NumberPhoneModel(
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: ResultPhoneModel? = null,

    @SerializedName("error")
    @Expose
    var generalError: GeneralErrorModel? = null
)