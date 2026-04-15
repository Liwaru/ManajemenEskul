package com.example.penjualanmobilkotlin

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveSession(
        idUser: String,
        username: String = "",
        idEskul: Int = 0,
        userLevel: Int = 0,
        idPembina: String = ""
    ) {
        prefs.edit()
            .putString("id_user", idUser)
            .putString("username", username)
            .putInt("id_eskul", idEskul)
            .putInt("user_level", userLevel)
            .putString("id_pembina", idPembina)
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

    fun saveEskulId(idEskul: Int) {
        prefs.edit().putInt("id_eskul", idEskul).apply()
    }

    fun getLastEskulId(): Int {
        return prefs.getInt("last_id_eskul", 0)
    }

    fun getUserLevel(): Int {
        return prefs.getInt("user_level", 0)
    }

    fun getPembinaId(): String {
        return prefs.getString("id_pembina", "") ?: ""
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
