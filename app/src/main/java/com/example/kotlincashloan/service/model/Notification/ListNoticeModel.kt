package com.example.kotlincashloan.service.model.Notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListNoticeModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: ArrayList<ResultListNoticeModel>? = null
)