package com.example.kotlincashloan.ui.registration.recovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.kotlincashloan.service.model.recovery.ListSupportTypeResultModel
import com.example.kotlincashloan.service.model.recovery.RecoveryAccessResultModel
import com.example.kotlincashloan.service.model.recovery.SupportTicketResultModel
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.ResultStatus

class PasswordViewMode: ViewModel() {
    private val repository = NetworkRepository()

    fun recoveryAccess(map: Map<String, String>): LiveData<ResultStatus<CommonResponse<RecoveryAccessResultModel>>> {
        return repository.recoveryAccess(map)
    }

    fun listSupportType(map: Map<String, Int>): LiveData<ResultStatus<CommonResponse<ArrayList<ListSupportTypeResultModel>>>> {
        return repository.listSupportType(map)
    }

    fun supportTicket(map: Map<String, String>): LiveData<ResultStatus<CommonResponse<SupportTicketResultModel>>> {
        return repository.supportTicket(map)
    }
}