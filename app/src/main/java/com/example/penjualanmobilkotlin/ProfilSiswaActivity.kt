package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ProfilSiswaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_siswa)

        val txtUsername = findViewById<TextView>(R.id.txtUsername)
        val txtPassword = findViewById<TextView>(R.id.txtPassword)

        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId.isEmpty()) {
            Toast.makeText(this, "Belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.0.15/manajemeneskul/Get_profile.php"
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
                    if (json.optBoolean("success", false)) {
                        txtUsername.text = "Username: " + json.optString("username", "")
                        txtPassword.text = "Password: " + json.optString("password", "")
                    } else {
                        Toast.makeText(this, json.optString("message", "Gagal ambil profil"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Gagal koneksi: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("id_user" to userId)
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}