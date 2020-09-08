package com.example.myapplication.model


import com.google.gson.annotations.SerializedName

class ResultModel (
    @SerializedName("login")
    var login: String = "",

    @SerializedName("token")
    var token: String = ""
)