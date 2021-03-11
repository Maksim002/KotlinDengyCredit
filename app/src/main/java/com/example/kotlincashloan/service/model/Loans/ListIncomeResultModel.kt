package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListIncomeResultModel (
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null
){
    override fun toString(): String {
        return name.toString()
    }
}