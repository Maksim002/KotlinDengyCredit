package com.example.kotlincashloan.service.model.recovery

import com.google.gson.annotations.SerializedName

class SupportTicketResultModel (
    @SerializedName("code")
    var code: Int? = null,

    @SerializedName("message")
    var message: String? = null
)