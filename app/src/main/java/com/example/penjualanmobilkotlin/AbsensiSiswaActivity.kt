package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AbsensiSiswaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listData = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi_siswa)

        listView = findViewById(R.id.listAbsensiSiswa)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadData()
    }

    private fun loadData() {

        listData.clear()

        // 🔥 dummy dulu
        listData.add("Budi")
        listData.add("Andi")
        listData.add("Siti")

        adapter.notifyDataSetChanged()

        listView.setOnItemClickListener { _, _, position, _ ->
            absenSiswa(position)
        }
    }

    private fun absenSiswa(position: Int) {

        val nama = listData[position]
        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        Toast.makeText(this, "$nama berhasil diabsen ($tanggal)", Toast.LENGTH_SHORT).show()
    }
}