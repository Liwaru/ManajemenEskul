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
import org.json.JSONObject

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
        val session = SessionManager(this)
        val params = buildPembinaParams(session)
        if (params.isEmpty()) {
            Toast.makeText(this, "Session id_pembina atau id_eskul belum tersedia", Toast.LENGTH_SHORT).show()
            return
        }
        fetchAnggota(params)
    }

    private fun fetchAnggota(params: Map<String, String>) {
        val request = object : StringRequest(
            Method.POST, ApiConfig.GET_ANGGOTA,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    val items = parseAnggota(clean)
                    listData.clear()
                    listData.addAll(items)
                    adapter.notifyDataSetChanged()

                    if (listData.isEmpty()) {
                        Toast.makeText(this, "Belum ada anggota eskul", Toast.LENGTH_SHORT).show()
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

    private fun parseAnggota(response: String): ArrayList<String> {
        val result = ArrayList<String>()
        val jsonArray = when {
            response.startsWith("[") -> JSONArray(response)
            response.startsWith("{") -> {
                val jsonObject = JSONObject(response)
                when {
                    jsonObject.has("data") -> jsonObject.getJSONArray("data")
                    jsonObject.has("anggota") -> jsonObject.getJSONArray("anggota")
                    jsonObject.has("siswa") -> jsonObject.getJSONArray("siswa")
                    else -> JSONArray()
                }
            }
            else -> JSONArray()
        }

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val namaSiswa = obj.optString("nama_siswa")
                .ifBlank { obj.optString("nama") }
                .ifBlank { obj.optString("username") }
                .ifBlank { "Tanpa nama" }
            val namaEskul = obj.optString("nama_eskul")
            val nis = obj.optString("nis")

            result.add(
                buildString {
                    append(namaSiswa)
                    if (nis.isNotBlank()) {
                        append("\nNIS: ")
                        append(nis)
                    }
                    if (namaEskul.isNotBlank()) {
                        append("\nEskul: ")
                        append(namaEskul)
                    }
                }
            )
        }

        return result
    }
}
