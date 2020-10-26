package com.example.kotlincashloan.service.model.Notification

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultListNoticeModel (
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

    @SerializedName("review")
    @Expose
    var review: Boolean? = null,

    @SerializedName("detail")
    @Expose
    var detail: Boolean? = null
)