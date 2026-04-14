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

class ListPendaftaranActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var txtKosong: TextView
    private lateinit var adapter: PendaftaranAdapter
    private val listData = ArrayList<Pendaftaran>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_pendaftaran)

        listView = findViewById(R.id.listPendaftaran)
        txtKosong = findViewById(R.id.txtKosongPendaftaran)
        adapter = PendaftaranAdapter(this, listData) { item, status, alasan ->
            updateStatusPendaftaran(item, status, alasan)
        }
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
        val url = "http://192.168.0.15/manajemeneskul/get_pendaftaran.php"

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    val items = parsePendaftaran(clean)
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
                    put("status", "menunggu")
                }
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun updateStatusPendaftaran(item: Pendaftaran, status: String, alasan: String) {
        if (item.id == 0) {
            Toast.makeText(this, "ID pendaftaran tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val session = SessionManager(this)
        val idEskul = session.getEskulId()
        val url = "http://192.168.0.15/manajemeneskul/update_status_pendaftaran.php"
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
                    Toast.makeText(this, "Status ${item.namaSiswa} menjadi $status", Toast.LENGTH_SHORT).show()
                    loadData()
                } else {
                    Toast.makeText(
                        this,
                        if (clean.isBlank()) "Gagal update status" else clean,
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf<String, String>().apply {
                    put("id_pendaftaran", item.id.toString())
                    put("id", item.id.toString())
                    put("id_eskul", idEskul.toString())
                    put("eskul_id", idEskul.toString())
                    put("status", status)
                    put("alasan", alasan)
                    put("alasan_tolak", alasan)
                }
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun parsePendaftaran(response: String): ArrayList<Pendaftaran> {
        val result = ArrayList<Pendaftaran>()
        val jsonArray = when {
            response.startsWith("[") -> JSONArray(response)
            response.startsWith("{") -> {
                val jsonObject = JSONObject(response)
                when {
                    jsonObject.has("data") -> jsonObject.getJSONArray("data")
                    jsonObject.has("pendaftaran") -> jsonObject.getJSONArray("pendaftaran")
                    else -> JSONArray()
                }
            }
            else -> JSONArray()
        }

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                Pendaftaran(
                    id = obj.optInt("id_pendaftaran", obj.optInt("id", 0)),
                    namaSiswa = obj.optString("nama_siswa")
                        .ifBlank { obj.optString("nama_user") }
                        .ifBlank { obj.optString("username") }
                        .ifBlank { obj.optString("nama") }
                        .ifBlank { "Tanpa nama" },
                    namaEskul = obj.optString("nama_eskul"),
                    status = obj.optString("status"),
                    alasan = obj.optString("alasan")
                        .ifBlank { obj.optString("alasan_tolak") }
                )
            )
        }

        return result
    }
}
