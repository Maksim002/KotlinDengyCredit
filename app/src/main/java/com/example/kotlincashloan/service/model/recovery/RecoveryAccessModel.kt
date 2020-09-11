package com.example.kotlincashloan.service.model.recovery

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RecoveryAccessModel (
    @SerializedName("code")
    @Expose
    var code: Int? = null,

    @SerializedName("result")
    @Expose
    var result: RecoveryAccessResultModel? = null
)