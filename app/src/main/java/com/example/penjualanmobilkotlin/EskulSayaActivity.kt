package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class EskulSayaActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var txtKosong: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eskul_saya)

        listView = findViewById(R.id.listEskulSaya)
        txtKosong = findViewById(R.id.txtKosong)

        loadEskulSayaDummy()
    }

    private fun loadEskulSayaDummy() {
        // Data dummy: siswa ikut Badminton
        val dummyJson = """
            [
                {
                    "id_eskul": 4,
                    "nama_eskul": "Badminton",
                    "nama_pembina": "Dewi",
                    "jam_mulai": "11:00:00",
                    "jam_selesai": "11:45:00"
                }
            ]
        """.trimIndent()

        try {
            val jsonArray = JSONArray(dummyJson)
            val eskulList = ArrayList<Eskul>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                eskulList.add(
                    Eskul(
                        id_eskul = obj.getInt("id_eskul"),
                        nama_eskul = obj.getString("nama_eskul"),
                        nama_pembina = obj.getString("nama_pembina"),
                        jam_mulai = obj.getString("jam_mulai"),
                        jam_selesai = obj.getString("jam_selesai")
                    )
                )
            }

            if (eskulList.isEmpty()) {
                txtKosong.visibility = View.VISIBLE
                listView.visibility = View.GONE
            } else {
                txtKosong.visibility = View.GONE
                listView.visibility = View.VISIBLE

                // Pakai EskulAdapter tanpa tombol edit (mode view only)
                val adapter = EskulSayaAdapter(this, eskulList)
                listView.adapter = adapter
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}