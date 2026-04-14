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
                                deskripsi = obj.optString("deskripsi", ""),
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
                    if (!clean.startsWith("{")) {
                        val lower = clean.lowercase()
                        val isSuccess = lower.contains("success") ||
                            lower.contains("berhasil") ||
                            lower.contains("sukses")
                        Toast.makeText(
                            this,
                            if (clean.isBlank()) "Pendaftaran berhasil" else clean,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (isSuccess) {
                            openBeranda()
                        }
                        return@Listener
                    }

                    val json = JSONObject(clean)
                    val isSuccess = json.optBoolean("success", false) ||
                        json.optString("status", "").equals("success", ignoreCase = true) ||
                        json.optString("status", "").equals("sukses", ignoreCase = true)
                    val message = json.optString("message").ifBlank {
                        if (isSuccess) "Pendaftaran berhasil" else "Pendaftaran gagal"
                    }

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (isSuccess) {
                        session.saveLastEskulId(idEskul)
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
                val idEskulString = idEskul.toString()
                return hashMapOf<String, String>().apply {
                    put("id_user", idUser)
                    put("id_siswa", idUser)
                    put("user_id", idUser)
                    put("id_eskul", idEskulString)
                    put("eskul_id", idEskulString)
                    put("status", "menunggu")
                }
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
