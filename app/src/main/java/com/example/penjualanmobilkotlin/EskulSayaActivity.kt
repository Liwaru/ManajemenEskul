package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class EskulSayaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var listData: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eskul_saya)

        listView = findViewById(R.id.listEskulSaya)

        listData = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = adapter

        loadEskulSaya()
    }

    private fun loadEskulSaya() {

        val session = SessionManager(this)
        val idUser = session.getIdUser()

        val url = "http://192.168.0.15/manajemeneskul/eskul_saya.php"

        val request = object : com.android.volley.toolbox.StringRequest(
            Method.POST, url,
            StringRequest@{ response ->

                try {
                    if (!response.trim().startsWith("[")) {
                        Toast.makeText(this, "Response error:\n$response", Toast.LENGTH_LONG).show()
                        return@StringRequest
                    }

                    val jsonArray = JSONArray(response)

                    listData.clear()

                    if (jsonArray.length() == 0) {
                        listData.add("Kamu belum mendaftar eskul")
                    } else {
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)

                            val nama = obj.getString("nama_eskul")
                            val pembina = obj.getString("nama_pembina")

                            listData.add("Eskul: $nama\nPembina: $pembina")
                        }
                    }

                    adapter.notifyDataSetChanged()

                } catch (e: Exception) {
                    Toast.makeText(this, "Parsing error:\n${e.message}", Toast.LENGTH_LONG).show()
                }

            },
            {
                Toast.makeText(this, "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "id_user" to idUser!!
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}