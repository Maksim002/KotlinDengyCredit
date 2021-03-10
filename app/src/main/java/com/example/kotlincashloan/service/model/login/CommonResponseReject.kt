package com.example.kotlinscreenscanner.service.model

import com.example.kotlincashloan.service.model.login.SaveLoanRejectModel
import com.google.gson.annotations.SerializedName


class CommonResponseReject<T>(
    @SerializedName("result")
    val result: T,
    val error: GeneralErrorModel,
    val reject: SaveLoanRejectModel,
    val code: Int
)