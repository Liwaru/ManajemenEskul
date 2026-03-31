package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BerandaAActivity : AppCompatActivity() {

    private lateinit var btnPembina: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berandaa)

        btnPembina = findViewById(R.id.btnTambahPembina)
        btnLogout = findViewById(R.id.btnLogout)

        btnPembina.setOnClickListener {
            startActivity(Intent(this, DataPembinaActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val session = SessionManager(this)
            session.logout()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}