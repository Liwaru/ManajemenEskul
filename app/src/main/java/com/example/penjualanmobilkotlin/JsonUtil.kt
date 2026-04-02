package com.example.penjualanmobilkotlin

object JsonUtils {
    fun cleanResponse(response: String): String {
        var cleaned = response.trim()
        // Hapus BOM karakter jika ada
        if (cleaned.startsWith("\uFEFF")) cleaned = cleaned.substring(1)
        // Hapus <br> di awal
        cleaned = cleaned.replace(Regex("^(<br\\s*/?>)+"), "")
        return cleaned
    }
}