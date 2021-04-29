package com.timelysoft.tsjdomcom.service


import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val NAME = "TsjDom"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var passwordRecovery: String?
        get() = preferences.getString("passwordRecovery", "")
        set(value) = preferences.edit {
            it.putString("passwordRecovery", value)
        }

    var type: String?
        get() = preferences.getString("type", "")
        set(value) = preferences.edit {
            it.putString("type", value)
        }

    var nationality: String?
        get() = preferences.getString("nationality", "")
        set(value) = preferences.edit {
            it.putString("nationality", value)
        }


    var urlApi: String?
        get() = preferences.getString("urlApi", "")
        set(value) = preferences.edit {
            it.putString("urlApi", value)
        }

    var tokenApi: String?
        get() = preferences.getString("tokenApi", "")
        set(value) = preferences.edit {
            it.putString("tokenApi", value)
        }

    var applicationId: String?
        get() = preferences.getString("applicationId", "")
        set(value) = preferences.edit {
            it.putString("applicationId", value)
        }

    var refreshWindow: String?
        get() = preferences.getString("refreshWindow", "")
        set(value) = preferences.edit {
            it.putString("refreshWindow", value)
        }


    var loginRecovery: String?
        get() = preferences.getString("loginRecovery", "")
        set(value) = preferences.edit {
            it.putString("loginRecovery", value)
        }

    var token: String?
        get() = preferences.getString("token", "")
        set(value) = preferences.edit {
            it.putString("token", value)
        }

    var savePin: String?
        get() = preferences.getString("savePin", "")
        set(value) = preferences.edit {
            it.putString("savePin", value)
        }

    var numberCharacters: Int?
        get() = preferences.getInt("numberCharacters", 0)
        set(value) = preferences.edit {
            it.putInt("numberCharacters", value!!)
        }

    var login: String?
        get() = preferences.getString("login", "")
        set(value) = preferences.edit {
            it.putString("login", value)
        }

    var password: String?
        get() = preferences.getString("password", "")
        set(value) = preferences.edit {
            it.putString("password", value)
        }

    var resultPassword: String?
        get() = preferences.getString("resultPassword", "")
        set(value) = preferences.edit {
            it.putString("resultPassword", value)
        }

    var dataKey: String?
        get() = preferences.getString("dataKey", "")
        set(value) = preferences.edit {
            it.putString("dataKey", value)
        }

    var number: String?
        get() = preferences.getString("number", "")
        set(value) = preferences.edit {
            it.putString("number", value)
        }


    var boleanCode: Boolean
        get() = preferences.getBoolean("errorCode", false)
        set(value) = preferences.edit {
            it.putBoolean("errorCode", value)
        }

    var status: Boolean
        get() = preferences.getBoolean("status", false)
        set(value) = preferences.edit {
            it.putBoolean("status", value)
        }

    var applicationStatus: Boolean
        get() = preferences.getBoolean("applicationStatus", false)
        set(value) = preferences.edit {
            it.putBoolean("applicationStatus", value)
        }

    var isFormatMask: String?
        get() = preferences.getString("isFormatMask", "")
        set(value) = preferences.edit {
            it.putString("isFormatMask", value)
        }

    var receivedSms: String?
        get() = preferences.getString("receivedSms", "")
        set(value) = preferences.edit {
            it.putString("receivedSms", value)
        }

    var pushNotificationsId: String?
        get() = preferences.getString("pushNotificationsId", "")
        set(value) = preferences.edit {
            it.putString("pushNotificationsId", value)
        }

    var isLogined: Boolean
        get() = preferences.getBoolean("isLogined", false)
        set(value) = preferences.edit {
            it.putBoolean("isLogined", value)
        }

    var isPinCode: Boolean
        get() = preferences.getBoolean("isPinCode", false)
        set(value) = preferences.edit {
            it.putBoolean("isPinCode", value)
        }

    var observedInternet: Boolean
        get() = preferences.getBoolean("observedInternet", false)
        set(value) = preferences.edit {
            it.putBoolean("observedInternet", value)
        }

    var isRemember: Boolean
        get() = preferences.getBoolean("isRemember", false)
        set(value) = preferences.edit {
            it.putBoolean("isRemember", value)
        }

    var isTouchId: Boolean
        get() = preferences.getBoolean("isTouchId", false)
        set(value) = preferences.edit {
            it.putBoolean("isTouchId", value)
        }

    var isLoginCode: Boolean
        get() = preferences.getBoolean("isLoginCode", false)
        set(value) = preferences.edit {
            it.putBoolean("isLoginCode", value)
        }

    var isNumber: Boolean
        get() = preferences.getBoolean("isNumber", false)
        set(value) = preferences.edit {
            it.putBoolean("isNumber", value)
        }

    var inputsAnim: Int
        get() = preferences.getInt("inputsAnim", 0)
        set(value) = preferences.edit {
            it.putInt("inputsAnim", value)
        }

    var isValid: Boolean
        get() = preferences.getBoolean("isValid", false)
        set(value) = preferences.edit {
            it.putBoolean("isValid", value)
        }

    var id: Int
        get() = preferences.getInt("id", id)
        set(value) = preferences.edit {
            it.putInt("id", value)
        }

    var isSeekBar: Int?
        get() = preferences.getInt("isSeekBar", 0)
        set(value) = preferences.edit {
            if (value != null) {
                it.putInt("isSeekBar", value)
            }
        }

    fun clear() {
        preferences.edit {
            it.putString("token", "")
            it.putBoolean("isLogined", false)
            it.putString("email", "")
            it.putString("refresh_token", "")
            it.apply()
        }
    }

    fun clearLogin() {
        preferences.edit {
            it.putString("email", "")
            it.apply()
        }
    }

    var reviewCode: Int?
        get() = preferences.getInt("reviewCode", 0)
        set(value) = preferences.edit {
            if (value != null) {
                it.putInt("reviewCode", value)
            }
        }

    var reviewCodeAp: Int?
        get() = preferences.getInt("reviewCodeAp", 0)
        set(value) = preferences.edit {
            if (value != null) {
                it.putInt("reviewCodeAp", value)
            }
        }

    fun String.toFullPhone(): String {
        return "996" + this.replace(" ", "")
    }
}