package com.example.kotlincashloan.ui.support

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.ResultStatus

class SupportViewModel : ViewModel(){
    private val repository = NetworkRepository()

    fun listFaq(phone:  Map<String, String>): LiveData<ResultStatus<CommonResponse<ArrayList<ListFaqResultModel>>>> {
        return repository.listFaq(phone)
    }
}