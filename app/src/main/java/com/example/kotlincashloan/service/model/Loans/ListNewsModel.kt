package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListNewsModel {
    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("result")
    @Expose
    var result: List<ListNewsResultModel>? = null

}