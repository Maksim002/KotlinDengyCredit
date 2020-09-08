package com.example.kotlinscreenscanner.service.model

import com.google.gson.annotations.SerializedName


class CommonResponse<T>(
    @SerializedName("result")
    val result: T,
    val error: GeneralErrorModel,
    val code: Int
)