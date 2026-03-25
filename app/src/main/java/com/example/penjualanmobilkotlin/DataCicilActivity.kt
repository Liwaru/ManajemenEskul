package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class DataCicilActivity : AppCompatActivity() {

    private lateinit var btnTambah: Button
    private lateinit var listBayar: ListView

    private val listData = ArrayList<String>()

    private val URL_TAMPIL = "http://10.80.250.56/penjualanmobil/Tampilcicilan.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_cicil)

        btnTambah = findViewById(R.id.tomboltambah)
        listBayar = findViewById(R.id.listcicil)

        btnTambah.setOnClickListener {
            val intent = Intent(this, TambahCicilActivity::class.java)
            startActivity(intent)
        }

        loadData()
    }

    private fun loadData() {

        val request = StringRequest(
            Request.Method.GET,
            URL_TAMPIL,

            { response ->

                try {

                    val arr = JSONArray(response)
                    listData.clear()

                    for (i in 0 until arr.length()) {

                        val obj = arr.getJSONObject(i)

                        val kodeKredit = obj.getString("kode_kredit")
                        val namaPembeli = obj.getString("nama_pembeli")
                        val tanggal = obj.getString("tanggal_cicilan")
                        val cicilKe = obj.getString("cicilanke")
                        val jumlah = obj.getString("jumlah_cicilan")
                        val sisa = obj.getString("sisa_cicilan")

                        val item =
                            "Kode Kredit : $kodeKredit\n" +
                                    "Nama Pembeli : $namaPembeli\n" +
                                    "Tanggal Bayar : $tanggal\n" +
                                    "Cicilan ke-$cicilKe\n" +
                                    "Jumlah Bayar : Rp $jumlah\n" +
                                    "Sisa Cicilan : Rp $sisa"

                        listData.add(item)
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        listData
                    )

                    listBayar.adapter = adapter

                } catch (e: Exception) {

                    e.printStackTrace()

                    Toast.makeText(
                        this,
                        "Gagal memproses data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            { error ->

                Log.e("Volley", error.toString())

                Toast.makeText(
                    this,
                    "Gagal memuat data!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}