package com.example.penjualanmobilkotlin

import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {
    fun cleanResponse(response: String): String {
        var cleaned = response.trim()
        // Hapus BOM karakter jika ada
        if (cleaned.startsWith("\uFEFF")) cleaned = cleaned.substring(1)
        // Hapus <br> di awal
        cleaned = cleaned.replace(Regex("^(<br\\s*/?>)+"), "")
        return cleaned
    }

    fun isSuccessResponse(response: String): Boolean {
        val clean = cleanResponse(response)
        if (clean.isBlank()) return false

        if (!clean.startsWith("{")) {
            val lower = clean.lowercase()
            return lower.contains("success") ||
                lower.contains("berhasil") ||
                lower.contains("sukses")
        }

        val json = JSONObject(clean)
        val status = json.optString("status")
        val result = json.optString("result")

        return json.optBoolean("success", false) ||
            json.optInt("success", 0) == 1 ||
            json.optInt("value", 0) == 1 ||
            json.optInt("code", 0) == 200 ||
            (!json.optBoolean("error", false) && json.has("message")) ||
            status.equals("success", ignoreCase = true) ||
            status.equals("sukses", ignoreCase = true) ||
            status.equals("berhasil", ignoreCase = true) ||
            result.equals("success", ignoreCase = true) ||
            result.equals("sukses", ignoreCase = true)
    }

    fun extractArray(response: String, vararg keys: String): JSONArray {
        val clean = cleanResponse(response)
        return when {
            clean.startsWith("[") -> JSONArray(clean)
            clean.startsWith("{") -> {
                val jsonObject = JSONObject(clean)
                keys.firstNotNullOfOrNull { key -> jsonObject.optJSONArray(key) } ?: JSONArray()
            }
            else -> JSONArray()
        }
    }
}
