package com.example.penjualanmobilkotlin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DataMobilActivity : AppCompatActivity() {

    private lateinit var listview: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private val listData = ArrayList<String>()

    private val kodeList = ArrayList<String>()
    private val merkList = ArrayList<String>()
    private val typeList = ArrayList<String>()
    private val warnaList = ArrayList<String>()
    private val hargaList = ArrayList<String>()
    private val gambarList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_mobil)

        val tombolTambah = findViewById<Button>(R.id.tomboltambah)

        tombolTambah.setOnClickListener {
            startActivity(Intent(this, TambahMobilActivity::class.java))
        }

        listview = findViewById(R.id.listmobil)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listview.adapter = adapter

        loadData()

        listview.setOnItemClickListener { _, _, position, _ ->
            showOptionDialog(position)
        }
    }

    private fun loadData() {

        val url = "http://10.80.250.56/Penjualanmobil/Tampilmobil.php"

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

                listData.clear()
                kodeList.clear()
                merkList.clear()
                typeList.clear()
                warnaList.clear()
                hargaList.clear()
                gambarList.clear()

                for (i in 0 until response.length()) {

                    val obj = response.getJSONObject(i)

                    kodeList.add(obj.getString("kode_mobil"))
                    merkList.add(obj.getString("merk"))
                    typeList.add(obj.getString("type"))
                    warnaList.add(obj.getString("warna"))
                    hargaList.add(obj.getString("harga"))
                    gambarList.add(obj.getString("gambar"))

                    listData.add(
                        "Kode Mobil : ${obj.getString("kode_mobil")}\n" +
                                "Merk : ${obj.getString("merk")}\n" +
                                "Type : ${obj.getString("type")}\n" +
                                "Warna : ${obj.getString("warna")}\n" +
                                "Harga : ${obj.getString("harga")}\n"
                    )
                }

                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun showOptionDialog(position: Int) {

        val pilihan = arrayOf("Edit", "Hapus")

        AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(pilihan) { _, which ->

                when (which) {

                    0 -> {

                        val intent = Intent(this, EditMobilActivity::class.java)

                        intent.putExtra("kode_mobil", kodeList[position])
                        intent.putExtra("merk", merkList[position])
                        intent.putExtra("type", typeList[position])
                        intent.putExtra("warna", warnaList[position])
                        intent.putExtra("harga", hargaList[position])
                        intent.putExtra("gambar", gambarList[position])

                        startActivityForResult(intent, 2)
                    }

                    1 -> {
                        konfirmasiHapus(position)
                    }
                }
            }
            .show()
    }

    private fun konfirmasiHapus(position: Int) {

        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Yakin ingin menghapus mobil ini?")
            .setPositiveButton("Ya") { _, _ ->
                hapusData(kodeList[position])
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusData(kodeMobil: String) {

        val url = "http://10.80.250.56/Penjualanmobil/hapusmobil.php"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            { response ->

                val json = JSONObject(response)

                Toast.makeText(
                    this,
                    json.getString("message"),
                    Toast.LENGTH_SHORT
                ).show()

                loadData()
            },
            {

                Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show()
            }
        ) {

            override fun getParams(): MutableMap<String, String> {

                return hashMapOf("kode_mobil" to kodeMobil)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == RESULT_OK) {

            loadData()
        }
    }
}