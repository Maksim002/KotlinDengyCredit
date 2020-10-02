package com.example.kotlinscreenscanner.service.model

import com.google.gson.annotations.SerializedName

class GeneralErrorModel (
    @SerializedName("code")
    var code: Int? = null,

    @SerializedName("message")
    var message: String? = null
)