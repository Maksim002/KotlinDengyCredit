package com.example.kotlincashloan.ui.Loans

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.Loans.GetNewsResultModel
import com.example.kotlincashloan.service.model.Loans.ListNewsModel
import com.example.kotlincashloan.service.model.Loans.ListNewsResultModel
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.ResultStatus

class LoansViewModel: ViewModel() {
    private val repository = NetworkRepository()

    fun listNews(map: Map<String, String>): LiveData<ResultStatus<CommonResponse<ArrayList<ListNewsResultModel>>>> {
        return repository.listNews(map)
    }

    fun getNews(map: Map<String, String>): LiveData<ResultStatus<CommonResponse<GetNewsResultModel>>> {
        return repository.getNews(map)
    }
}