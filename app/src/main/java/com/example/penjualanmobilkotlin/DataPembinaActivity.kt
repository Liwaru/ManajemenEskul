package com.example.penjualanmobilkotlin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DataPembinaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var eskulList: ArrayList<Eskul>
    private lateinit var adapter: EskulAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pembina)

        listView = findViewById(R.id.listPembina)
        eskulList = ArrayList()
        adapter = EskulAdapter(
            this,
            eskulList,
            onEditClick = { eskul ->
                val intent = Intent(this, TambahEskulActivity::class.java).apply {
                    putExtra("mode", "edit")
                    putExtra("id_eskul", eskul.id_eskul)
                    putExtra("nama_eskul", eskul.nama_eskul)
                    putExtra("nama_pembina", eskul.nama_pembina)
                    putExtra("deskripsi", eskul.deskripsi)
                    putExtra("jam_mulai", eskul.jam_mulai)
                    putExtra("jam_selesai", eskul.jam_selesai)
                    putExtra("gambar", eskul.gambar)
                }
                startActivity(intent)
            },
            onDeleteClick = { eskul ->
                confirmDeleteEskul(eskul)
            }
        )
        listView.adapter = adapter

        // Di DataPembinaActivity
        val btnTambahEskul = findViewById<Button>(R.id.btnTambahEskul)
        btnTambahEskul.setOnClickListener {
            startActivity(Intent(this, TambahEskulActivity::class.java))
        }

        val fabTambah = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabTambah)
        fabTambah.setOnClickListener {
            startActivity(Intent(this, TambahEskulActivity::class.java))
        }

        loadEskulData()
    }

    private fun loadEskulData() {
        val url = ApiConfig.GET_ALL_ESKUL

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(JsonUtils.cleanResponse(response))
                    eskulList.clear()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val eskul = Eskul(
                            id_eskul = obj.getInt("id_eskul"),
                            nama_eskul = obj.getString("nama_eskul"),
                            nama_pembina = obj.getString("nama_pembina"),
                            deskripsi = obj.optString("deskripsi", ""),
                            jam_mulai = obj.getString("jam_mulai"),
                            jam_selesai = obj.getString("jam_selesai"),
                            gambar = obj.optString("gambar")
                                .ifBlank { obj.optString("foto") }
                                .ifBlank { obj.optString("image") }
                        )
                        eskulList.add(eskul)
                    }
                    adapter.notifyDataSetChanged()
                    if (eskulList.isEmpty()) {
                        Toast.makeText(this, "Tidak ada data eskul", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Gagal parsing JSON: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {}
        Volley.newRequestQueue(this).add(request)
    }

    private fun confirmDeleteEskul(eskul: Eskul) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Eskul")
            .setMessage("Hapus eskul ${eskul.nama_eskul}?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                hapusEskul(eskul)
            }
            .show()
    }

    private fun hapusEskul(eskul: Eskul) {
        val url = ApiConfig.DELETE_ESKUL
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                if (JsonUtils.isSuccessResponse(clean)) {
                    Toast.makeText(this, "Eskul berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadEskulData()
                } else {
                    val message = if (clean.startsWith("{")) {
                        try {
                            JSONObject(clean).optString("message")
                        } catch (_: Exception) {
                            clean
                        }
                    } else {
                        clean
                    }
                    Toast.makeText(
                        this,
                        if (message.isBlank()) "Gagal menghapus eskul" else message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf<String, String>().apply {
                    put("id_eskul", eskul.id_eskul.toString())
                    put("id", eskul.id_eskul.toString())
                }
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    override fun onResume() {
        super.onResume()
        loadEskulData()
    }
}
