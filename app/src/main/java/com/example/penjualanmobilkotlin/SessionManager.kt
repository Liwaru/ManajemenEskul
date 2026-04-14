package com.example.penjualanmobilkotlin

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveSession(idUser: String, username: String = "", idEskul: Int = 0) {
        prefs.edit()
            .putString("id_user", idUser)
            .putString("username", username)
            .putInt("id_eskul", idEskul)
            .apply()
    }

    fun saveLastEskulId(idEskul: Int) {
        prefs.edit().putInt("last_id_eskul", idEskul).apply()
    }

    fun getUserId(): String {
        return prefs.getString("id_user", "") ?: ""
    }

    fun getUsername(): String {
        return prefs.getString("username", "") ?: ""
    }

    fun getEskulId(): Int {
        return prefs.getInt("id_eskul", 0)
    }

    fun getLastEskulId(): Int {
        return prefs.getInt("last_id_eskul", 0)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
