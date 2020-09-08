package com.timelysoft.tsjdomcom.service


data class ResultStatus<out T>(val status: Status, val data: T?, val msg: String?) {
    companion object {
        fun <T> success(data: T?,msg: String = ""): ResultStatus<T> {
            return ResultStatus(Status.SUCCESS, data, msg)
        }

        fun <T> error(msg: String = "", data: T? = null): ResultStatus<T> {
            return ResultStatus(Status.ERROR, data, msg)
        }

        fun <T> netwrok(msg: String = "", data: T? = null): ResultStatus<T> {
            return ResultStatus(Status.NETWORK, data, msg)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    NETWORK

}
