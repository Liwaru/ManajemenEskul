package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class DaftarEskulActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_eskul)

        listView = findViewById(R.id.listViewEskul)
        loadDaftarEskul()
    }

    private fun loadDaftarEskul() {
        val url = ApiConfig.GET_ALL_ESKUL

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    if (clean.isBlank()) {
                        listView.adapter = DaftarEskulAdapter(this, arrayListOf()) { }
                        Toast.makeText(this, "Data eskul kosong", Toast.LENGTH_LONG).show()
                        return@Listener
                    }

                    val jsonArray = JsonUtils.extractArray(clean, "data", "eskul", "result")
                    val eskulList = ArrayList<Eskul>()

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        eskulList.add(
                            Eskul(
                                id_eskul = obj.getInt("id_eskul"),
                                nama_eskul = obj.getString("nama_eskul"),
                                nama_pembina = obj.optString("nama_pembina", "-"),
                                deskripsi = obj.optString("deskripsi", ""),
                                jam_mulai = obj.optString("jam_mulai", "-"),
                                jam_selesai = obj.optString("jam_selesai", "-"),
                                gambar = obj.optString("gambar")
                                    .ifBlank { obj.optString("foto") }
                                    .ifBlank { obj.optString("image") }
                            )
                        )
                    }

                    if (eskulList.isEmpty()) {
                        val message = JsonUtils.extractMessage(clean, "message", "pesan", "error")
                        Toast.makeText(
                            this,
                            if (message.isBlank()) "Belum ada data eskul" else message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

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

        request.setShouldCache(false)
        val queue = Volley.newRequestQueue(this)
        queue.cache.clear()
        queue.add(request)
    }

    private fun daftarEskul(idEskul: Int) {
        val session = SessionManager(this)
        val idUser = session.getUserId()
        if (idUser.isEmpty()) {
            Toast.makeText(this, "Belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.DAFTAR_ESKUL
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    val isSuccess = JsonUtils.isSuccessResponse(clean)
                    val message = if (clean.startsWith("{")) {
                        JSONObject(clean).optString("message").ifBlank {
                            JSONObject(clean).optString("pesan")
                        }
                    } else {
                        clean
                    }.ifBlank {
                        if (isSuccess) "Pendaftaran berhasil" else "Pendaftaran gagal"
                    }

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (isSuccess) {
                        session.saveLastEskulId(idEskul)
                        openBeranda()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "id_user" to idUser,
                    "id_eskul" to idEskul.toString()
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun openBeranda() {
        val intent = Intent(this, BerandaSActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
