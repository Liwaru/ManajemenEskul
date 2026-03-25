package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DataAnggotaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listData = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_anggota)

        listView = findViewById(R.id.listAnggota)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadData()
    }

    private fun loadData() {

        listData.clear()

        // 🔥 sementara dummy dulu
        listData.add("Budi")
        listData.add("Andi")
        listData.add("Siti")

        adapter.notifyDataSetChanged()
    }
}