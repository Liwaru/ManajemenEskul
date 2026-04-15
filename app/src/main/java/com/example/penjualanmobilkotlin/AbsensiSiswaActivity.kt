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
        val params = buildPembinaParams(session)
        if (params.isEmpty()) {
            txtKosong.text = "Session id_pembina atau id_eskul belum tersedia"
            txtKosong.visibility = View.VISIBLE
            listView.visibility = View.GONE
            return
        }
        fetchAbsensiSiswa(params)
    }

    private fun fetchAbsensiSiswa(params: Map<String, String>) {
        val request = object : StringRequest(
            Method.POST, ApiConfig.GET_ABSENSI_SISWA,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    val items = parseAbsensi(clean)
                    listData.clear()
                    listData.addAll(items)
                    adapter.notifyDataSetChanged()

                    if (listData.isEmpty()) {
                        txtKosong.text = "Belum ada data absensi siswa"
                        txtKosong.visibility = View.VISIBLE
                        listView.visibility = View.GONE
                    } else {
                        txtKosong.visibility = View.GONE
                        listView.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    txtKosong.text = "Gagal parsing data absensi"
                    txtKosong.visibility = View.VISIBLE
                    listView.visibility = View.GONE
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                txtKosong.text = "Koneksi gagal ke data absensi siswa"
                txtKosong.visibility = View.VISIBLE
                listView.visibility = View.GONE
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun buildPembinaParams(session: SessionManager): Map<String, String> {
        val idEskul = session.getEskulId()
        if (idEskul != 0) {
            return hashMapOf("id_eskul" to idEskul.toString())
        }

        val idPembina = session.getPembinaId()
        if (idPembina.isNotBlank()) {
            return hashMapOf("id_pembina" to idPembina)
        }

        return emptyMap()
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
