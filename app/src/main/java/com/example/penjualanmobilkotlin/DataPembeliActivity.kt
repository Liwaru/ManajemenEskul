package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley

class DataPembeliActivity : AppCompatActivity() {
    private lateinit var listview: ListView
    private val listData= ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pembeli)
        listview=findViewById(R.id.listpembeli)

        loadData()
    }

    private fun loadData() {
        val url="http://192.168.0.15/penjualanmobil/Tampilpembeli.php"
        val request= JsonArrayRequest(url,
            {response->
                listData.clear()
                for(i in 0 until response.length()){
                    val obj = response.getJSONObject(i)
                    val tampil = "KTP : ${obj.getString("ktp")}\n"+
                            "Nama : ${obj.getString("nama_pembeli")}\n"+
                            "Alamat : ${obj.getString("alamat_pembeli")}\n"+
                            "Telepon : ${obj.getString("telp_pembeli")}\n"
                    listData.add(tampil)
                }
                val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,listData)
                listview.adapter=adapter
            },
            {
                it.printStackTrace()
            })
        Volley.newRequestQueue(this).add(request)
    }
}