package com.example.penjualanmobilkotlin

data class Pendaftaran(
    val id: Int = 0,
    val namaSiswa: String,
    val namaEskul: String = "",
    val status: String = "",
    val alasan: String = ""
)
