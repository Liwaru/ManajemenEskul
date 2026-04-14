package com.example.penjualanmobilkotlin

data class Eskul(
    val id_eskul: Int,
    val nama_eskul: String,
    val nama_pembina: String,
    val deskripsi: String = "",
    val jam_mulai: String,
    val jam_selesai: String
)
