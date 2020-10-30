package com.example.kotlincashloan.service.model.Notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultDetailNoticeModel (
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("date")
    @Expose
    var date: String? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("description")
    @Expose
    var description: String? = null,

    @SerializedName("text")
    @Expose
    var text: String? = null
)