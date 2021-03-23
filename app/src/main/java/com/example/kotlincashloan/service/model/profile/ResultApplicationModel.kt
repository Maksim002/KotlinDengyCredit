package com.example.kotlincashloan.service.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResultApplicationModel (
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("date")
    @Expose
    var date: String? = null,

    @SerializedName("review")
    @Expose
    var review: Boolean? = null,

    @SerializedName("detail")
    @Expose
    var detail: Boolean? = null
)