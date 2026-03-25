package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BerandaPActivity : AppCompatActivity() {

    private lateinit var btnAnggota: Button
    private lateinit var btnPendaftaran: Button
    private lateinit var btnAbsensi: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berandap)

        btnAnggota = findViewById(R.id.btnAnggota)
        btnPendaftaran = findViewById(R.id.btnPendaftaran)
        btnAbsensi = findViewById(R.id.btnAbsensi)
        btnLogout = findViewById(R.id.btnLogout)

        btnAnggota.setOnClickListener {
            startActivity(Intent(this, DataAnggotaActivity::class.java))
        }

        btnPendaftaran.setOnClickListener {
            startActivity(Intent(this, ListPendaftaranActivity::class.java))
        }

        btnAbsensi.setOnClickListener {
            startActivity(Intent(this, AbsensiSiswaActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val session = SessionManager(this)
            session.logout()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}