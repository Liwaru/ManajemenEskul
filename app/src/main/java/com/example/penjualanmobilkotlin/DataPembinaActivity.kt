package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.penjualanmobilkotlin.adapter.EskulAdapter
import org.json.JSONArray

class DataPembinaActivity : AppCompatActivity() {

    private lateinit var rvEskul: RecyclerView
    private lateinit var btnTambahEskul: Button

    private val sessionIdPembina = 1 // Contoh ID pembina dari session login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pembina)

        rvEskul = findViewById(R.id.rvEskul)
        btnTambahEskul = findViewById(R.id.btnTambahEskul)

        rvEskul.layoutManager = LinearLayoutManager(this)

        btnTambahEskul.setOnClickListener {
            Toast.makeText(this, "Tambah Eskul clicked", Toast.LENGTH_SHORT).show()
            // Implementasi tambah eskul di sini
        }

        loadDataPembina()
    }

    private fun loadDataPembina() {
        val url = "http://192.168.0.15/manajemeneskul/data_pembina.php"

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                val jsonArray = JSONArray(response)
                val eskulList = mutableListOf<Eskul>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val eskul = Eskul(
                        idEskul = obj.getInt("id_eskul"),
                        namaEskul = obj.getString("nama_eskul"),
                        namaPembina = obj.getString("nama_pembina"),
                        idPembina = obj.getInt("id_pembina"),
                        jamMulai = obj.getString("jam_mulai"),
                        jamSelesai = obj.getString("jam_selesai"),
                        gambar = obj.getString("gambar")
                    )
                    eskulList.add(eskul)
                }

                val adapter = EskulAdapter(eskulList, sessionIdPembina) { eskul ->
                    Toast.makeText(this, "Edit ${eskul.namaEskul}", Toast.LENGTH_SHORT).show()
                    // Implementasi edit eskul di sini
                }
                rvEskul.adapter = adapter

            }, {
                Toast.makeText(this, "Gagal ambil data pembina", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(this).add(request)
    }
}