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
import org.json.JSONException

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
        val params = buildPembinaParams(session)
        if (params.isEmpty()) {
            txtKosong.text = "Session id_pembina atau id_eskul belum tersedia"
            txtKosong.visibility = View.VISIBLE
            listView.visibility = View.GONE
            return
        }
        fetchPendaftaran(params)
    }

    private fun fetchPendaftaran(params: Map<String, String>) {
        val request = object : StringRequest(
            Method.POST, ApiConfig.GET_PENDAFTARAN,
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

    private fun updateStatusPendaftaran(item: Pendaftaran, status: String, alasan: String) {
        if (item.id == 0) {
            Toast.makeText(this, "ID pendaftaran tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.UPDATE_STATUS_PENDAFTARAN
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                val isSuccess = JsonUtils.isSuccessResponse(clean)

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
                return hashMapOf(
                    "id_pendaftaran" to item.id.toString(),
                    "status" to status
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun parsePendaftaran(response: String): ArrayList<Pendaftaran> {
        val result = ArrayList<Pendaftaran>()
        val jsonArray = JsonUtils.extractArray(response, "data", "pendaftaran", "result")

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                Pendaftaran(
                    id = obj.optInt("id_pendaftaran", obj.optInt("id", 0)),
                    namaSiswa = obj.optString("nama")
                        .ifBlank { obj.optString("nama_siswa") }
                        .ifBlank { obj.optString("nama_lengkap") }
                        .ifBlank { obj.optString("nama_user") }
                        .ifBlank { obj.optString("username") }
                        .ifBlank { "Tanpa nama" },
                    namaEskul = obj.optString("nama_eskul")
                        .ifBlank { obj.optString("eskul") },
                    status = obj.optString("status"),
                    alasan = obj.optString("alasan")
                        .ifBlank { obj.optString("alasan_tolak") }
                )
            )
        }

        return result
    }
}
