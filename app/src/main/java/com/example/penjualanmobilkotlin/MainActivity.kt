package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tombolMobil=findViewById<Button>(R.id.tombolmobil)
        val tombolPembeli=findViewById<Button>(R.id.tombolpembeli)
        val tombolPaket=findViewById<Button>(R.id.tombolpaket)
        val tombolCash=findViewById<Button>(R.id.tombolbeli)
        val tombolKredit=findViewById<Button>(R.id.tombolkredit)
        val tombolCicil=findViewById<Button>(R.id.tombolcicilan)

        tombolMobil.setOnClickListener {
            startActivity(Intent(this, DataMobilActivity::class.java))
        }

        tombolPembeli.setOnClickListener {
            startActivity(Intent(this, DataPembeliActivity::class.java))
        }

        tombolPaket.setOnClickListener {
            startActivity(Intent(this, DataPaketActivity::class.java))
        }

        tombolCash.setOnClickListener {
            startActivity(Intent(this, DataCashActivity::class.java))
        }

        tombolKredit.setOnClickListener {
            startActivity(Intent(this, DataKreditActivity::class.java))
        }

        tombolCicil.setOnClickListener {
            startActivity(Intent(this, DataCicilActivity::class.java))
        }
    }
}