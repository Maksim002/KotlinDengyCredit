package com.example.kotlincashloan.service.model.Notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DetailNoticeModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: ResultDetailNoticeModel? = null
)