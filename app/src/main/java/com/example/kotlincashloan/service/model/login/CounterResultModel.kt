package com.example.kotlinscreenscanner.service.model

import com.google.gson.annotations.SerializedName

class CounterResultModel(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("iso_code")
    var isoCode: String? = null,

    @SerializedName("phone_code")
    var phoneCode: String? = null,

    @SerializedName("phone_length")
    var phoneLength: String? = null,

    @SerializedName("phone_mask")
    var phoneMask: String? = null,

    @SerializedName("phone_mask_small")
    var phoneMaskSmall: String? = null
) {
    override fun toString(): String {
        return name.toString()
    }
}