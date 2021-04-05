package com.example.kotlincashloan.adapter.profile

import com.example.kotlincashloan.service.model.profile.ResultApplicationModel

interface ApplicationListener {
    fun applicationListener(int: Int, item: ResultApplicationModel)
}