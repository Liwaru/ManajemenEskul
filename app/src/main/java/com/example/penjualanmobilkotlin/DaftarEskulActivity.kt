package com.example.penjualanmobilkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DaftarEskulActivity : AppCompatActivity() {

    private lateinit var recyclerEskul: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_eskul)

        recyclerEskul = findViewById(R.id.recyclerEskul)
        recyclerEskul.layoutManager = LinearLayoutManager(this)

        val listEskul = listOf(
            Eskul(1, "Futsal", "Senin 15:00-17:00", R.drawable.futsal),
            Eskul(2, "Basket", "Selasa 15:00-17:00", R.drawable.basket),
            Eskul(3, "Voli", "Rabu 15:00-17:00", R.drawable.voli),
            Eskul(4, "Badminton", "Kamis 15:00-17:00", R.drawable.badminton),
            Eskul(5, "Musik", "Jumat 15:00-17:00", R.drawable.musik),
            Eskul(6, "Catur", "Sabtu 15:00-17:00", R.drawable.catur)
        )

        val adapter = EskulAdapter(listEskul)
        recyclerEskul.adapter = adapter
    }
}