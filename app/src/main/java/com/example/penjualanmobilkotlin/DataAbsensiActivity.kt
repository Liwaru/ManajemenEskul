package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataAbsensiActivity : AppCompatActivity() {
    private lateinit var btnAbsensi: Button
    private lateinit var txtKosong: TextView
    private lateinit var listView: ListView
    private lateinit var adapter: AbsensiAdapter
    private val listData = ArrayList<Absensi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi_eskul)

        btnAbsensi = findViewById(R.id.btnAbsensiSaya)
        txtKosong = findViewById(R.id.txtKosongAbsensi)
        listView = findViewById(R.id.listAbsensi)
        adapter = AbsensiAdapter(this, listData)
        listView.adapter = adapter

        btnAbsensi.setOnClickListener {
            simpanAbsensi()
        }

        loadAbsensiSaya()
    }

    private fun loadAbsensiSaya() {
        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId.isBlank()) {
            showEmptyState("Belum login")
            return
        }

        val url = ApiConfig.GET_ABSENSI
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
                        showEmptyState("Belum ada data absensi")
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
                return hashMapOf("id_user" to userId)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun simpanAbsensi() {
        val session = SessionManager(this)
        val userId = session.getUserId()
        val idEskul = session.getLastEskulId().takeIf { it != 0 } ?: session.getEskulId()
        if (userId.isBlank()) {
            Toast.makeText(this, "Belum login", Toast.LENGTH_SHORT).show()
            return
        }
        if (idEskul == 0) {
            Toast.makeText(this, "ID eskul belum tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        val tanggalAbsensi = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val url = ApiConfig.ABSEN
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                val isSuccess = if (clean.startsWith("{")) {
                    val json = JSONObject(clean)
                    json.optBoolean("success", false) ||
                        json.optString("status").equals("success", ignoreCase = true) ||
                        json.optString("status").equals("sukses", ignoreCase = true)
                } else {
                    val lower = clean.lowercase()
                    lower.contains("success") || lower.contains("berhasil") || lower.contains("sukses")
                }

                if (isSuccess) {
                    Toast.makeText(this, "Absensi berhasil disimpan", Toast.LENGTH_SHORT).show()
                    loadAbsensiSaya()
                } else {
                    Toast.makeText(
                        this,
                        if (clean.isBlank()) "Gagal simpan absensi" else clean,
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "id_user" to userId,
                    "id_eskul" to idEskul.toString(),
                    "tanggal_absensi" to tanggalAbsensi
                )
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
                        .ifBlank { sessionNameFallback() },
                    tanggalAbsensi = obj.optString("tanggal_absensi")
                        .ifBlank { obj.optString("tanggal") }
                        .ifBlank { "-" }
                )
            )
        }

        return result
    }

    private fun sessionNameFallback(): String {
        return SessionManager(this).getUsername().ifBlank { "Siswa" }
    }

    private fun showEmptyState(message: String) {
        txtKosong.text = message
        txtKosong.visibility = View.VISIBLE
        listView.visibility = View.GONE
    }
}
