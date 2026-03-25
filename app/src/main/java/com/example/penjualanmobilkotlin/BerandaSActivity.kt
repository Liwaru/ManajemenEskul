package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BerandaSActivity : AppCompatActivity() {

    private lateinit var btnDaftar: Button
    private lateinit var btnEskulSaya: Button
    private lateinit var btnAbsensi: Button
    private lateinit var btnProfil: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berandas)

        btnDaftar = findViewById(R.id.tomboldaftar)
        btnEskulSaya = findViewById(R.id.tomboleskulsaya)
        btnAbsensi = findViewById(R.id.tombolabsensi)
        btnProfil = findViewById(R.id.tombolprofil)
        btnLogout = findViewById(R.id.tombollogout)

        btnDaftar.setOnClickListener {
            startActivity(Intent(this, DaftarEskulActivity::class.java))
        }

        btnEskulSaya.setOnClickListener {
            startActivity(Intent(this, EskulSayaActivity::class.java))
        }

        btnAbsensi.setOnClickListener {
            startActivity(Intent(this, DataAbsensiActivity::class.java))
        }

        btnProfil.setOnClickListener {
            startActivity(Intent(this, ProfilSiswaActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val session = SessionManager(this)
            session.logout()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}