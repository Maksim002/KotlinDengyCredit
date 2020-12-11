package com.example.kotlincashloan.service.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class listGenderModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: List<listGenderResultModel>? = null
)