package com.example.kotlincashloan.service.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetImgResultModel (
    @SerializedName("data")
    @Expose
    var data: String? = null,

    @SerializedName("mime_type")
    @Expose
    var mimeType: String? = null
)