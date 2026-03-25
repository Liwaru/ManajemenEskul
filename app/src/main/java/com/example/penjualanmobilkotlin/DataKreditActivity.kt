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

class DataKreditActivity : AppCompatActivity() {

    private lateinit var listview: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private val listData = ArrayList<String>()

    private val kodeKreditList = ArrayList<String>()
    private val ktpList = ArrayList<String>()
    private val namaList = ArrayList<String>()
    private val mobilList = ArrayList<String>()
    private val paketList = ArrayList<String>()
    private val tanggalList = ArrayList<String>()
    private val cicilanList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_kredit)

        val tombolTambah = findViewById<Button>(R.id.tomboltambah)

        tombolTambah.setOnClickListener {
            startActivity(Intent(this, TambahKreditActivity::class.java))
        }

        listview = findViewById(R.id.listkredit)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listview.adapter = adapter

        loadData()

        listview.setOnItemClickListener { _, _, position, _ ->
            showOptionDialog(position)
        }
    }

    private fun loadData() {

        val url = "http://10.80.250.56/penjualanmobil/TampilKredit.php"

        val request = JsonArrayRequest(
            url,
            { response ->

                listData.clear()
                kodeKreditList.clear()
                ktpList.clear()
                namaList.clear()
                mobilList.clear()
                paketList.clear()
                tanggalList.clear()
                cicilanList.clear()

                for (i in 0 until response.length()) {

                    val obj = response.getJSONObject(i)

                    val kode = obj.getString("kode_kredit")
                    val ktp = obj.getString("ktp")
                    val nama = obj.getString("nama_pembeli")

                    val mobil =
                        obj.getString("merk") + " " +
                                obj.getString("type") +
                                " (" + obj.getString("warna") + ")"

                    val paket =
                        "Paket " + obj.getString("kode_paket") +
                                " | DP: " + obj.getString("uang_muka") + "%" +
                                " | Tenor: " + obj.getString("tenor") + " bln" +
                                " | Bunga: " + obj.getString("bunga_cicilan") + "%"

                    val tanggal = obj.getString("tanggal_kredit")
                    val cicilan = obj.getString("bayar_kredit")

                    kodeKreditList.add(kode)
                    ktpList.add(ktp)
                    namaList.add(nama)
                    mobilList.add(mobil)
                    paketList.add(paket)
                    tanggalList.add(tanggal)
                    cicilanList.add(cicilan)

                    listData.add(
                        "Kode Kredit : $kode\n" +
                                "Pembeli : $ktp - $nama\n" +
                                "Mobil : $mobil\n" +
                                "$paket\n" +
                                "Tanggal : $tanggal\n" +
                                "Cicilan/bln : Rp$cicilan\n"
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

        val pilihan = arrayOf("Edit", "Hapus")

        AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(pilihan) { _, which ->

                when (which) {

                    0 -> {

                        val intent = Intent(this, EditKreditActivity::class.java)

                        intent.putExtra("kode_kredit", kodeKreditList[position])
                        intent.putExtra("ktp", ktpList[position])
                        intent.putExtra("nama", namaList[position])
                        intent.putExtra("mobil", mobilList[position])
                        intent.putExtra("paket", paketList[position])
                        intent.putExtra("tanggal", tanggalList[position])
                        intent.putExtra("cicilan", cicilanList[position])

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
            .setMessage("Yakin ingin menghapus data kredit ini?")
            .setPositiveButton("Ya") { _, _ ->
                hapusData(kodeKreditList[position])
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusData(kodeKredit: String) {

        val url = "http://10.80.250.56/penjualanmobil/hapusKredit.php"

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
                return hashMapOf("kode_kredit" to kodeKredit)
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