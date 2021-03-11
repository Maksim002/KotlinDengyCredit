package com.example.kotlincashloan.service.model.login

import com.example.kotlinscreenscanner.service.model.GeneralErrorModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveLoanModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("error")
    @Expose
    val error: GeneralErrorModel,

    @SerializedName("result")
    @Expose
    var result: SaveLoanResultModel,

    @SerializedName("reject")
    @Expose
    var reject: SaveLoanRejectModel
)