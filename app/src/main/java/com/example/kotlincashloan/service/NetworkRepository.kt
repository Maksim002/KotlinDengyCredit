package com.timelysoft.tsjdomcom.service

import androidx.lifecycle.liveData
import com.example.kotlincashloan.extension.generateUrl
import kotlinx.coroutines.Dispatchers


class NetworkRepository {

    fun auth(params: Map<String, String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().auth(params, generateUrl("login"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Неверный логин или пароль"))
                    }
                }else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun numberPhone(map: Map<String,String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().numberPhone(map, generateUrl("checkPhone"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Ваш номер принят"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun smsConfirmation(map: Map<String,Int>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().smsConfirmation(map, generateUrl("checkCode"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Ваш sms код подтверждён"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun questionnaire(map: Map<String, String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().questionnaire(map, generateUrl("registration"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Регистрация прошла успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun listGender(map: Map<String, Int>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().listGender(map, generateUrl("listGender"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос на получение полов успешен"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun listNationality(map: Map<String, Int>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().listNationality(map, generateUrl("listNationality"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("600", null))
        }
    }

    fun listSecretQuestion(map: Map<String, Int>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().listSecretQuestion(map, generateUrl("listSecretQuestion"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun listAvailableCountry(map: Map<String, Int>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().listAvailableCountry(map, generateUrl("listAvailableCountry"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun recoveryAccess(map: Map<String, String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().recoveryAccess(map, generateUrl("recoveryAccess"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun listSupportType(map: Map<String, Int>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().listSupportType(map, generateUrl("listSupportType"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun supportTicket(map: Map<String, String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().supportTicket(map, generateUrl("supportTicket"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun listTrafficSource(map: Map<String, Int>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().listTrafficSource(map, generateUrl("listTrafficSource"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun saveLoans(map: Map<String,String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().saveLoans(map, generateUrl("saveLoan"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Ваш номер принят"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun getImgLoan(map: Map<String, String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().getImgLoan(map, generateUrl("getImg"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Запрос прошел успешно"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }


    fun getApplication(map: Map<String,String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().getLoan(map, generateUrl("getLoan"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Ваш номер принят"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }

    fun checkPassword(map: Map<String,String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().checkPassword(map, generateUrl("checkPassword"))
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
                    } else {
                        emit(ResultStatus.error("Ваш номер принят"))
                    }
                }
                else -> {
                    emit(ResultStatus.error(response.code().toString()))
                }
            }
        } catch (e: Exception) {
            if (e.localizedMessage != "End of input at line 1 column 1 path \$"){
                emit(ResultStatus.netwrok("601", null))
            }else{
                emit(ResultStatus.netwrok("600", null))
            }
        }
    }
}