package com.example.kotlincashloan.service.model.general

class GeneralDialogModel (
    var name: String,
    var key: String,
    var position: Int,
    var id: Int? = null,
    var positionKey: String? = null
){
    override fun toString(): String {
        return name
    }
}