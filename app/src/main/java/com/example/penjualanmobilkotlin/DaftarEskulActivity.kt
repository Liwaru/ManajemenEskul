package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.penjualanmobilkotlin.adapter.DaftarEskulAdapter
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
            EskulItem(1, "Futsal", "Senin 15:00-17:00", R.drawable.futsal),
            EskulItem(2, "Basket", "Selasa 15:00-17:00", R.drawable.basket),
            EskulItem(3, "Voli", "Rabu 15:00-17:00", R.drawable.voli),
            EskulItem(4, "Badminton", "Kamis 15:00-17:00", R.drawable.badminton),
            EskulItem(5, "Musik", "Jumat 15:00-17:00", R.drawable.musik),
            EskulItem(6, "Catur", "Sabtu 15:00-17:00", R.drawable.catur)
        )

        val adapter = DaftarEskulAdapter(listEskul) { eskul ->
            daftarEskul(eskul.idEskul)
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
                try {
                    val json = JSONObject(response)
                    val status = json.optString("status", "")
                    val message = json.getString("message")

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                    // Jika pendaftaran berhasil, kembali ke dashboard
                    if (status == "success") {
                        // Kembali ke DashboardActivity (sesuaikan nama activity dashboard Anda)
                        val intent = Intent(this, BerandaSActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
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