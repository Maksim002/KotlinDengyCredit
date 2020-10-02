package com.example.kotlinscreenscanner.service.model

import com.google.gson.annotations.SerializedName

class ListGenderResultModel (
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null

){
    override fun toString(): String {
        return name.toString()
    }
}