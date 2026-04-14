package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AbsensiSiswaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var txtKosong: TextView
    private lateinit var adapter: AbsensiAdapter
    private val listData = ArrayList<Absensi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi_siswa)

        listView = findViewById(R.id.listAbsensiSiswa)
        txtKosong = findViewById(R.id.txtKosongAbsensiSiswa)
        adapter = AbsensiAdapter(this, listData)
        listView.adapter = adapter

        loadData()
    }

    private fun loadData() {
        val session = SessionManager(this)
        val idEskul = session.getEskulId()
        if (idEskul == 0) {
            txtKosong.text = "Session id_eskul pembina belum tersedia"
            txtKosong.visibility = View.VISIBLE
            listView.visibility = View.GONE
            return
        }
        val url = "http://192.168.0.15/manajemeneskul/get_absensi_siswa.php"

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    val items = parseAbsensi(clean)
                    listData.clear()
                    listData.addAll(items)
                    adapter.notifyDataSetChanged()

                    if (listData.isEmpty()) {
                        txtKosong.visibility = View.VISIBLE
                        listView.visibility = View.GONE
                    } else {
                        txtKosong.visibility = View.GONE
                        listView.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf<String, String>().apply {
                    put("id_eskul", idEskul.toString())
                    put("eskul_id", idEskul.toString())
                }
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun parseAbsensi(response: String): ArrayList<Absensi> {
        val result = ArrayList<Absensi>()
        val jsonArray = when {
            response.startsWith("[") -> JSONArray(response)
            response.startsWith("{") -> {
                val jsonObject = JSONObject(response)
                when {
                    jsonObject.has("data") -> jsonObject.getJSONArray("data")
                    jsonObject.has("absensi") -> jsonObject.getJSONArray("absensi")
                    else -> JSONArray()
                }
            }
            else -> JSONArray()
        }

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                Absensi(
                    idAbsensi = obj.optInt("id_absensi", obj.optInt("id", 0)),
                    namaSiswa = obj.optString("nama_siswa")
                        .ifBlank { obj.optString("nama") }
                        .ifBlank { obj.optString("username") }
                        .ifBlank { "Tanpa nama" },
                    tanggalAbsensi = obj.optString("tanggal_absensi")
                        .ifBlank { obj.optString("tanggal") }
                        .ifBlank { "-" }
                )
            )
        }

        return result
    }
}
