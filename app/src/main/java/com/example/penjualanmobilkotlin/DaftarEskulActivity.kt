package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class DaftarEskulActivity : AppCompatActivity() {

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
                if (!clean.startsWith("{")) {
                    Toast.makeText(this, "Error: $clean", Toast.LENGTH_LONG).show()
                    return@Listener
                }
                try {
                    val json = JSONObject(clean)
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
                    e.printStackTrace()
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