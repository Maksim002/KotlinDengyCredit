package com.timelysoft.tsjdomcom.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers


class NetworkRepository {
    fun auth(params: Map<String, String>) = liveData(Dispatchers.IO) {
        try {
            val response = RetrofitService.apiService().auth(params)
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
            val response = RetrofitService.apiService().numberPhone(map)
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
            val response = RetrofitService.apiService().smsConfirmation(map)
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
            val response = RetrofitService.apiService().questionnaire(map)
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
            val response = RetrofitService.apiService().listGender(map)
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
            val response = RetrofitService.apiService().listNationality(map)
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
            val response = RetrofitService.apiService().listSecretQuestion(map)
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
            val response = RetrofitService.apiService().listAvailableCountry(map)
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
            val response = RetrofitService.apiService().recoveryAccess(map)
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
            val response = RetrofitService.apiService().listSupportType(map)
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
            val response = RetrofitService.apiService().supportTicket(map)
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
            val response = RetrofitService.apiService().listTrafficSource(map)
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
            val response = RetrofitService.apiService().saveLoans(map)
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
            val response = RetrofitService.apiService().getImgLoan(map)
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        emit(ResultStatus.success(response.body()))
//                        val imageBytes = Base64.decode(response.body()!!.result.data, Base64.DEFAULT)
//                            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                            val nh = (decodedImage.height * (512.0 / decodedImage.width)).toInt()
//                            val scaled = Bitmap.createScaledBitmap(decodedImage, 512, nh, true)
//                            mitmap.put(map["type_id"].toString(), scaled)
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
            val response = RetrofitService.apiService().getLoan(map)
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