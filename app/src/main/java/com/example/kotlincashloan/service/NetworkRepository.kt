package com.timelysoft.tsjdomcom.service

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
                }
                else -> {
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
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
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
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
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
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
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
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
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
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
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
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
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
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
                    emit(ResultStatus.error("Не известная ошибка"))
                }
            }
        } catch (e: Exception) {
            emit(ResultStatus.netwrok("Проблеммы с подключением интернета", null))
        }
    }
}