package com.example.penjualanmobilkotlin

import android.content.Context

class SessionManager(context: Context) {

    private val pref = context.getSharedPreferences("LOGIN", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ID_USER = "id_user"
        private const val KEY_ID_ESKUL = "id_eskul"
    }

    fun saveSession(idUser: String, idEskul: String) {
        val editor = pref.edit()
        editor.putString(KEY_ID_USER, idUser)
        editor.putString(KEY_ID_ESKUL, idEskul)
        editor.apply()
    }

    fun getIdUser(): String {
        return pref.getString(KEY_ID_USER, "") ?: ""
    }

    fun getIdEskul(): String {
        return pref.getString(KEY_ID_ESKUL, "") ?: ""
    }

    fun isLogin(): Boolean {
        return getIdUser().isNotEmpty()
    }

    fun logout() {
        val editor = pref.edit()
        editor.clear()
        editor.apply()
    }
}