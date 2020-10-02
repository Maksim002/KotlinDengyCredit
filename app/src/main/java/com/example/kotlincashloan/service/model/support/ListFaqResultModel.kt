package com.example.kotlincashloan.service.model.support


import com.google.gson.annotations.SerializedName

class ListFaqResultModel(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("text")
    var text: String? = null,

    var clicked: Boolean = false
)