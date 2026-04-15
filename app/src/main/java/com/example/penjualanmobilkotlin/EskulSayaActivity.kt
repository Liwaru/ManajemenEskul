package com.example.penjualanmobilkotlin

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class EskulSayaActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var txtKosong: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eskul_saya)

        listView = findViewById(R.id.listEskulSaya)
        txtKosong = findViewById(R.id.txtKosong)

        loadEskulSaya()
    }

    private fun loadEskulSaya() {
        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId.isBlank()) {
            showEmptyState("Belum login")
            return
        }

        val url = ApiConfig.ESKUL_SAYA
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                try {
                    val eskulList = parseEskulSaya(clean)
                    if (eskulList.isEmpty()) {
                        showEmptyState("Kamu belum diterima di eskul mana pun")
                    } else {
                        txtKosong.visibility = View.GONE
                        listView.visibility = View.VISIBLE
                        listView.adapter = EskulSayaAdapter(this, eskulList)
                        listView.setOnItemClickListener { _, _, position, _ ->
                            showEskulPopup(eskulList[position])
                        }
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Gagal koneksi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf("id_user" to userId)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun parseEskulSaya(response: String): ArrayList<Eskul> {
        val result = ArrayList<Eskul>()
        val jsonArray = when {
            response.startsWith("[") -> JSONArray(response)
            response.startsWith("{") -> {
                val jsonObject = JSONObject(response)
                when {
                    jsonObject.has("data") -> jsonObject.getJSONArray("data")
                    jsonObject.has("eskul") -> jsonObject.getJSONArray("eskul")
                    jsonObject.has("pendaftaran") -> jsonObject.getJSONArray("pendaftaran")
                    else -> JSONArray()
                }
            }
            else -> JSONArray()
        }

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val status = obj.optString("status")
            if (status.isNotBlank() && !status.equals("diterima", ignoreCase = true)) {
                continue
            }

            val jadwal = obj.optString("jadwal")
            val jadwalParts = jadwal.split("-")
                .map { it.trim() }
                .filter { it.isNotBlank() }
            val jamMulai = readTime(obj, "jam_mulai", "mulai", "waktu_mulai", "jamMulai")
                .ifBlank { jadwalParts.getOrNull(0).orEmpty() }
                .ifBlank { "-" }
            val jamSelesai = readTime(obj, "jam_selesai", "selesai", "waktu_selesai", "jamSelesai")
                .ifBlank { jadwalParts.getOrNull(1).orEmpty() }
                .ifBlank { "-" }

            result.add(
                Eskul(
                    id_eskul = obj.optInt("id_eskul", obj.optInt("eskul_id", 0)),
                    nama_eskul = obj.optString("nama_eskul")
                        .ifBlank { obj.optString("eskul") }
                        .ifBlank { "-" },
                    nama_pembina = obj.optString("nama_pembina")
                        .ifBlank { obj.optString("pembina") }
                        .ifBlank { "-" },
                    deskripsi = obj.optString("deskripsi"),
                    jam_mulai = jamMulai,
                    jam_selesai = jamSelesai,
                    gambar = obj.optString("gambar")
                        .ifBlank { obj.optString("foto") }
                        .ifBlank { obj.optString("image") }
                )
            )
        }

        return result
    }

    private fun showEmptyState(message: String) {
        txtKosong.text = message
        txtKosong.visibility = View.VISIBLE
        listView.visibility = View.GONE
    }

    private fun showEskulPopup(eskul: Eskul) {
        val detail = buildString {
            appendLine("Nama Eskul: ${eskul.nama_eskul}")
            appendLine("Pembina: ${eskul.nama_pembina}")
            appendLine("Jam Mulai: ${eskul.jam_mulai}")
            appendLine("Jam Selesai: ${eskul.jam_selesai}")
            append("Deskripsi: ${if (eskul.deskripsi.isBlank()) "-" else eskul.deskripsi}")
        }

        AlertDialog.Builder(this)
            .setTitle("Detail Eskul")
            .setMessage(detail)
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun readTime(obj: JSONObject, vararg keys: String): String {
        return keys.firstNotNullOfOrNull { key ->
            obj.optString(key).takeIf { it.isNotBlank() }
        }.orEmpty()
    }
}
