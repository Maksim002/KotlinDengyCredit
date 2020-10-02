package com.example.kotlincashloan.service.model.recovery

import com.google.gson.annotations.SerializedName

class ListSupportTypeResultModel (
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null
){
    override fun toString(): String {
        return name.toString()
    }
}