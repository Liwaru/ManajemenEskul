package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.*

class DataAbsensiActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listData = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi_eskul)

        listView = findViewById(R.id.listAbsensi)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadEskulSaya()
    }

    private fun loadEskulSaya() {

        val session = SessionManager(this)
        val idUser = session.getIdUser()

        if (idUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.0.15/manajemeneskul/absensi_data.php"

        val request = object : StringRequest(
            Method.POST, url,
            { response ->

                val jsonArray = org.json.JSONArray(response)
                listData.clear()

                if (jsonArray.length() == 0) {
                    listData.add("Kamu belum mendaftar eskul")
                } else {
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val nama = obj.getString("nama_eskul")

                        listData.add("$nama (Klik untuk absen)")
                    }
                }

                adapter.notifyDataSetChanged()
            },
            {
                Toast.makeText(this, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("id_user" to idUser)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun absen(position: Int) {

        val eskul = EskulData.eskulDipilih[position]

        val tanggal = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        AbsensiData.listAbsensi.add(
            Absensi(
                eskul.namaEskul,
                tanggal,
                "Hadir"
            )
        )

        Toast.makeText(this, "Absen ${eskul.namaEskul} berhasil", Toast.LENGTH_SHORT).show()
    }
}