package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ListPendaftaranActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: PendaftaranAdapter
    private val listData = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_pendaftaran)

        listView = findViewById(R.id.listPendaftaran)

        adapter = PendaftaranAdapter(this, listData)
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
    }
}