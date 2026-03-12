package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class TambahPaketActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_paket)

        val editkode = findViewById<EditText>(R.id.editkodepaket)
        val edituang = findViewById<EditText>(R.id.edituang)
        val edittenor = findViewById<EditText>(R.id.edittenor)
        val editbunga = findViewById<EditText>(R.id.editbunga)
        val btnSimpan = findViewById<Button>(R.id.tombolsimpan)

        btnSimpan.setOnClickListener {
            val kode = editkode.text.toString()
            val uang = edituang.text.toString()
            val tenor = edittenor.text.toString()
            val bunga = editbunga.text.toString()

            if (kode.isEmpty() || uang.isEmpty() || tenor.isEmpty() || bunga.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                simpanData(kode, uang, tenor, bunga)
            }
        }
    }

    private fun simpanData(
        kode: String,
        uang: String,
        tenor: String,
        bunga: String
    ) {
        val url = "http://10.208.184.71/Penjualanmobil/Tambahpaket.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                val json = JSONObject(response)
                val success = json.getBoolean("success")
                val message = json.getString("message")

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                if(success){
                    setResult(RESULT_OK)
                }
            },
            { error ->
                Toast.makeText(this, "Gagal kembali ke server", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["kode_paket"]=kode
                params["uang_muka"]=uang
                params["tenor"]=tenor
                params["bunga_cicilan"]=bunga
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
