package com.example.penjualanmobilkotlin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DataPembeliActivity : AppCompatActivity() {
    private lateinit var listview: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val listData = ArrayList<String>()
    private val ktplist = ArrayList<String>()
    private val namalist = ArrayList<String>()
    private val alamatlist = ArrayList<String>()
    private val telplist = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pembeli)

        listview=findViewById(R.id.listpembeli)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listview.adapter = adapter

        loadData()

        listview.setOnItemClickListener { _, _, position, _ ->
            showOptionDialog(position)
        }
    }

    private fun loadData() {
        val url="http://192.168.0.15/penjualanmobil/Tampilpembeli.php"
        val request= JsonArrayRequest(url,
            {response->
                listData.clear()
                ktplist.clear()
                namalist.clear()
                alamatlist.clear()
                telplist.clear()

                for(i in 0 until response.length()){
                    val obj = response.getJSONObject(i)

                    ktplist.add(obj.getString("ktp"))
                    namalist.add(obj.getString("nama_pembeli"))
                    alamatlist.add(obj.getString("alamat_pembeli"))
                    telplist.add(obj.getString("telp_pembeli"))

                        listData.add(
                            "KTP : ${obj.getString("ktp")}\n"+
                            "Nama : ${obj.getString("nama_pembeli")}\n"+
                            "Alamat : ${obj.getString("alamat_pembeli")}\n"+
                            "Telp : ${obj.getString("telp_pembeli")}\n"
                    )
                }
                adapter.notifyDataSetChanged()
            },
            {
                Toast.makeText(this, "gagal memuat data", Toast.LENGTH_SHORT).show()
            })
        Volley.newRequestQueue(this).add(request)
    }

    private fun showOptionDialog(position: Int) {
        val pilihan = arrayOf("Edit", "Hapus")

        AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(pilihan) { _, which ->
                when (which) {
                    0 -> {
                        val intent = Intent(this, EditPembeliActivity::class.java)
                        intent.putExtra("ktp", ktplist[position])
                        intent.putExtra("nama", namalist[position])
                        intent.putExtra("alamat", alamatlist[position])
                        intent.putExtra("telp", telplist[position])

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
            .setMessage("yakin ingin menghapus data ini?")
            .setPositiveButton("ya") { _, _ ->
                hapusData(ktplist[position])
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun hapusData(ktp: String) {
        val url = "http://192.168.0.15/penjualanmobil/hapuspembeli.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                val json = JSONObject(response)
                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                loadData()
            },
            {
                Toast.makeText(this, "gagal koneksi", Toast.LENGTH_SHORT).show()
            }

        ){
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("ktp" to ktp)
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