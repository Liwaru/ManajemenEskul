package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class DaftarEskulActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_eskul)

        listView = findViewById(R.id.listViewEskul)
        loadDaftarEskul()
    }

    private fun loadDaftarEskul() {
        val url = "http://192.168.0.15/manajemeneskul/get_eskul.php"

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    // ✅ Parse sebagai JSONArray, bukan JSONObject
                    val jsonArray = JSONArray(clean)
                    val eskulList = ArrayList<Eskul>()


                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        eskulList.add(
                            Eskul(
                                id_eskul = obj.getInt("id_eskul"),
                                nama_eskul = obj.getString("nama_eskul"),
                                nama_pembina = obj.optString("nama_pembina", "-"),
                                jam_mulai = obj.optString("jam_mulai", "-"),
                                jam_selesai = obj.optString("jam_selesai", "-")
                            )
                        )
                    }

// Ganti EskulAdapter → DaftarEskulAdapter
                    val adapter = DaftarEskulAdapter(this, eskulList) { eskul ->
                        daftarEskul(eskul.id_eskul)
                    }
                    listView.adapter = adapter

                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {}

        Volley.newRequestQueue(this).add(request)
    }

    private fun daftarEskul(idEskul: Int) {
        val session = SessionManager(this)
        val idUser = session.getUserId()
        if (idUser.isEmpty()) {
            Toast.makeText(this, "Belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.0.15/manajemeneskul/daftar_eskul.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    val json = org.json.JSONObject(clean)
                    val status = json.optString("status", "")
                    val message = json.optString("message", "")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (status == "success") {
                        val intent = Intent(this, BerandaSActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("id_user" to idUser, "id_eskul" to idEskul.toString())
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}