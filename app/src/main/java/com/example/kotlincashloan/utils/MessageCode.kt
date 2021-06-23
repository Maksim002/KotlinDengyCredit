package com.example.kotlincashloan.utils

enum class MessageCode(private val stringValue: String) {
    MESSAGE_1("Unable to resolve host \"crm-api-dev.molbulak2.ru\": No address associated with hostname"),
    MESSAGE_2("End of input at line 1 column 1 path \$");
    override fun toString(): String {
        return stringValue
    }
}