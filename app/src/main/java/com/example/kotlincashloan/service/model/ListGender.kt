package com.example.kotlinscreenscanner.service.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListGender (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: List<ListGenderResultModel>? = null
)