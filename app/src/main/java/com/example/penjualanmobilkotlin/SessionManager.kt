package com.example.penjualanmobilkotlin

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveSession(idUser: String, token: String = "") {
        prefs.edit().putString("id_user", idUser).apply()
    }

    fun getUserId(): String {
        return prefs.getString("id_user", "") ?: ""
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}