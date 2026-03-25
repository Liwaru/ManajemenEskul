package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DataAbsensiActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listData = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi_eskul)

        listView = findViewById(R.id.listAbsensi)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadEskulSaya()
    }

    private fun loadEskulSaya() {

        listData.clear()

        // ambil dari eskul yang sudah didaftarkan
        for (eskul in EskulData.eskulDipilih) {
            listData.add("${eskul.nama} (Klik untuk absen)")
        }

        adapter.notifyDataSetChanged()

        listView.setOnItemClickListener { _, _, position, _ ->
            absen(position)
        }
    }

    private fun absen(position: Int) {

        val eskul = EskulData.eskulDipilih[position]

        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        AbsensiData.listAbsensi.add(
            Absensi(
                eskul.nama,
                tanggal,
                "Hadir"
            )
        )

        Toast.makeText(this, "Absen ${eskul.nama} berhasil", Toast.LENGTH_SHORT).show()
    }
}