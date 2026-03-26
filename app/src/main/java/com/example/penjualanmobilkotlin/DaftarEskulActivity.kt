package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DaftarEskulActivity : AppCompatActivity() {

    private lateinit var recyclerEskul: RecyclerView
    private val URL_DAFTAR = "http://192.168.0.15/manajemeneskul/daftareskul.php"

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

        val adapter = EskulAdapter(listEskul) { eskul ->
            daftarEskul(eskul.id)
        }

        recyclerEskul.adapter = adapter
    }

    private fun daftarEskul(idEskul: Int) {

        val session = SessionManager(this)
        val idUser = session.getIdUser()

        if (idUser == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val request = object : StringRequest(
            Method.POST, URL_DAFTAR,
            { response ->
                val json = JSONObject(response)
                val message = json.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "id_user" to idUser,
                    "id_eskul" to idEskul.toString()
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}