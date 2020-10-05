package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanInfoResultModel (
    @SerializedName("client_status")
    @Expose
    var clientStatus: Int? = null,

    @SerializedName("get_active_loan")
    @Expose
    var getActiveLoan: Boolean? = null,

    @SerializedName("get_parallel_loan")
    @Expose
    var getParallelLoan: Boolean? = null,

    @SerializedName("active_loan")
    @Expose
    var activeLoan: ActiveLoanModel? = null,

    @SerializedName("parallel_loan")
    @Expose
    var parallelLoan: ParallelLoanModel? = null

)