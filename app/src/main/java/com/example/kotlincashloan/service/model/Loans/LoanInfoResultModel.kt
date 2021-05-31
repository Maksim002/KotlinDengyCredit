package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoanInfoResultModel (
    @SerializedName("client_status")
    @Expose
    var clientStatus: Int? = null,

    @SerializedName("process_active_loan")
    @Expose
    var processActiveLoan: Boolean? = null,

    @SerializedName("process_parallel_loan")
    @Expose
    var processParallelLoan: Boolean? = null,

    @SerializedName("get_active_loan")
    @Expose
    var getActiveLoan: Boolean? = null,

    @SerializedName("edit_active_loan")
    @Expose
    var editActiveLoan: Int? = null,

    @SerializedName("get_parallel_loan")
    @Expose
    var getParallelLoan: Boolean? = null,

    @SerializedName("edit_parallel_loan")
    @Expose
    var editParallelLoan: Int? = null,

    @SerializedName("active_loan")
    @Expose
    var activeLoan: ActiveLoanModel? = null,

    @SerializedName("parallel_loan")
    @Expose
    var parallelLoan: ParallelLoanModel? = null

)