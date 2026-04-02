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

class DataPembinaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var listData: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pembina)

        listView = findViewById(R.id.listPembina)
        listData = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadDataPembina()
    }

    private fun loadDataPembina() {
        val url = "http://10.0.2.2/manajemeneskul/get_pembina.php" // ganti IP sesuai
        val request = object : StringRequest(
            Method.GET, url,  // Gunakan GET dulu untuk testing
            Response.Listener { response ->
                android.util.Log.d("RAW_RESPONSE", response)
                Toast.makeText(this, "Response: $response", Toast.LENGTH_LONG).show()
                // ... proses JSON nanti jika sukses
            },
            Response.ErrorListener { error ->
                android.util.Log.e("VOLLEY_ERROR", error.toString())
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {}
        Volley.newRequestQueue(this).add(request)
    }
}