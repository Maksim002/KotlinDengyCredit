package com.example.kotlincashloan.extension

import com.timelysoft.tsjdomcom.service.AppPreferences

//Собирает url для запроса
fun generateUrl(method : String,url :String = "") :String{
    val baseUrl  = if (!AppPreferences.urlApi.isNullOrBlank()) AppPreferences.urlApi else url
    val token  = if (!AppPreferences.tokenApi.isNullOrBlank()) AppPreferences.tokenApi else ""
    return baseUrl + method + token
}