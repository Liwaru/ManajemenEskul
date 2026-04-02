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
        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId.isEmpty()) {
            Toast.makeText(this, "Belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.0.15/manajemeneskul/absensi_data.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                if (!clean.startsWith("[")) {
                    Toast.makeText(this, "Error: $clean", Toast.LENGTH_LONG).show()
                    return@Listener
                }
                try {
                    val jsonArray = JSONArray(clean)
                    listData.clear()
                    if (jsonArray.length() == 0) {
                        listData.add("Kamu belum mendaftar eskul")
                    } else {
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val nama = obj.optString("nama_eskul", "Unknown")
                            listData.add("$nama (Klik untuk absen)")
                        }
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Gagal ambil data: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("id_user" to userId)
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}