package com.example.kotlincashloan.service.model.support

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListFaqModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: List<ListFaqResultModel>? = null
)