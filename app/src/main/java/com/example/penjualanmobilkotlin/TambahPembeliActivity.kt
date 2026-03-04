package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest

class TambahPembeliActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pembeli)
        val editKtp=findViewById<EditText>(R.id.editktp)
        val editNama=findViewById<EditText>(R.id.editnama)
        val editAlamat=findViewById<EditText>(R.id.editalamat)
        val editNoHp=findViewById<EditText>(R.id.editnohp)
        val btnSimpan=findViewById<EditText>(R.id.tombolsimpan)

        btnSimpan.setOnClickListener {
            val ktp=editKtp.text.toString()
            val nama=editNama.text.toString()
            val alamat=editAlamat.text.toString()
            val nohp=editNoHp.text.toString()

            if(ktp.isEmpty() || nama.isEmpty() || alamat.isEmpty() || nohp.isEmpty()){
                Toast.makeText(this, "semua data wajib di isi", Toast.LENGTH_SHORT).show()
            }
            else {
                simpanData(ktp,nama,alamat,nohp)
            }
        }
        private fun simpanData{
        ktp: String,
        nama: String,
        alamat: String,
        nohp: String
        }{
            val url = "http://192.168.0.15/penjualanmobil/Tambahpembeli.php"

            val request=object : StringRequest{
            Request.Method.POST}
        }
        }
    }