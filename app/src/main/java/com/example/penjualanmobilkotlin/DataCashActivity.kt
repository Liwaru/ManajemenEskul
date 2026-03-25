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

class DataCashActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private val listData = ArrayList<String>()

    private val kodeCashList = ArrayList<String>()
    private val ktpList = ArrayList<String>()
    private val mobilList = ArrayList<String>()
    private val tanggalList = ArrayList<String>()
    private val bayarList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_cash)

        val tombolTambah = findViewById<Button>(R.id.tomboltambah)

        tombolTambah.setOnClickListener {
            startActivity(Intent(this, TambahCashActivity::class.java))
        }

        listView = findViewById(R.id.listcash)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadData()

        listView.setOnItemClickListener { _, _, position, _ ->
            showOptionDialog(position)
        }
    }

    private fun loadData() {

        val url = "http://10.80.250.56/penjualanmobil/Tampilcash.php"

        val request = JsonArrayRequest(
            url,
            { response ->

                listData.clear()
                kodeCashList.clear()
                ktpList.clear()
                mobilList.clear()
                tanggalList.clear()
                bayarList.clear()

                for (i in 0 until response.length()) {

                    val obj = response.getJSONObject(i)

                    val kodeCash = obj.getString("kode_cash")
                    val ktp = obj.getString("ktp")
                    val mobil = obj.getString("kode_mobil")
                    val tanggal = obj.getString("cash_tgl")
                    val bayar = obj.getString("cash_bayar")

                    kodeCashList.add(kodeCash)
                    ktpList.add(ktp)
                    mobilList.add(mobil)
                    tanggalList.add(tanggal)
                    bayarList.add(bayar)

                    listData.add(
                        "Kode Cash : $kodeCash\n" +
                                "KTP : $ktp\n" +
                                "Kode Mobil : $mobil\n" +
                                "Tanggal : $tanggal\n" +
                                "Total Bayar : Rp $bayar\n"
                    )
                }

                adapter.notifyDataSetChanged()
            },
            {
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun showOptionDialog(position: Int) {

        val pilihan = arrayOf("Detail", "Edit", "Hapus")

        AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(pilihan) { _, which ->

                when (which) {

                    0 -> {
                        showDetail(position)
                    }

                    1 -> {

                        val intent = Intent(this, EditCashActivity::class.java)

                        intent.putExtra("kode_cash", kodeCashList[position])
                        intent.putExtra("cash_tgl", tanggalList[position])

                        startActivityForResult(intent, 1)
                    }

                    2 -> {
                        konfirmasiHapus(position)
                    }
                }
            }
            .show()
    }

    private fun showDetail(position: Int) {

        AlertDialog.Builder(this)
            .setTitle("Detail Pembelian Cash")
            .setMessage(listData[position])
            .setPositiveButton("OK", null)
            .show()
    }

    private fun konfirmasiHapus(position: Int) {

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus data ini?")
            .setPositiveButton("Hapus") { _, _ ->
                hapusData(kodeCashList[position])
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusData(kodeCash: String) {

        val url = "http://10.80.250.56/penjualanmobil/hapuscash.php"

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

                return hashMapOf("kode_cash" to kodeCash)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {

            loadData()
        }
    }
}