package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanInResultModel (
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("min_count")
    @Expose
    var minCount: String? = null,

    @SerializedName("max_count")
    @Expose
    var maxCount: String? = null,

    @SerializedName("min_sum")
    @Expose
    var minSum: String? = null,

    @SerializedName("max_sum")
    @Expose
    var maxSum: String? = null,

    @SerializedName("rate")
    @Expose
    var rate: String? = null,

    @SerializedName("equ")
    @Expose
    var equ: String? = null

)