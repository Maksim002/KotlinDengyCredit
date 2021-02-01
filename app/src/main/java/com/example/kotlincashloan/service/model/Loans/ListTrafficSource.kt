package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class ListTrafficSource (
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null
){
    override fun toString(): String {
        return name.toString()
    }
}