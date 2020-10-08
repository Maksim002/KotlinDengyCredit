package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ParallelLoanModel (
    @SerializedName("status")
    @Expose
    var status: Boolean? = null,

    @SerializedName("balance")
    @Expose
    var balance: Int? = null,

    @SerializedName("paid")
    @Expose
    var paid: Int? = null,

    @SerializedName("total")
    @Expose
    var total: Int? = null,

    @SerializedName("payment_sum")
    @Expose
    var paymentSum: Int? = null,

    @SerializedName("payment_date")
    @Expose
    var paymentDate: String? = null

)