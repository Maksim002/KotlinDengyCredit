package com.example.kotlincashloan.service.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SaveLoanRejectModel (
    @SerializedName("message")
    @Expose
    var message: String? = null

)