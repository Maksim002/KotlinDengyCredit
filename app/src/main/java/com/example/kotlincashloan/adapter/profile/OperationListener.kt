package com.example.kotlincashloan.adapter.profile

import com.example.kotlincashloan.service.model.profile.ResultOperationModel

interface OperationListener {
    fun operationClickListener(int: Int, item: ResultOperationModel)
}