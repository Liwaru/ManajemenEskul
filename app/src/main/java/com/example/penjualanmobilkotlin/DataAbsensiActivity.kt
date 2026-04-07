package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class DataAbsensiActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var listData: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi_eskul) // sesuaikan layout

        listView = findViewById(R.id.listAbsensi) // pastikan ID benar
        listData = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadEskulSaya()
    }

    private fun loadEskulSaya() {
        listData.clear()

        val format = java.text.SimpleDateFormat("yyyy-MM-dd")

        for (i in 0..4) {
            val calendar = java.util.Calendar.getInstance()
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -i)

            val tanggal = format.format(calendar.time)

            listData.add("Tanggal: $tanggal - Hadir")
        }

        adapter.notifyDataSetChanged()
    }
}