package com.example.kotlincashloan.service.model.Loans

import com.google.gson.annotations.SerializedName

class GetNewsResultModel {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("thumbnail")
    var thumbnail: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("text")
    var text: String? = null
}