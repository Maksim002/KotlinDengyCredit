package com.example.kotlincashloan.service.model.recovery

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SupportTicketModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: SupportTicketResultModel? = null

)