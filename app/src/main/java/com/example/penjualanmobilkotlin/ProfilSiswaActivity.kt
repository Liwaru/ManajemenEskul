package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ProfilSiswaActivity : AppCompatActivity() {

    private lateinit var txtNis: TextView
    private lateinit var txtNama: TextView
    private lateinit var edtPassword: EditText
    private lateinit var btnSimpanPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_siswa)

        txtNis = findViewById(R.id.txtNis)
        txtNama = findViewById(R.id.txtNama)
        edtPassword = findViewById(R.id.edtPassword)
        btnSimpanPassword = findViewById(R.id.btnSimpanPassword)

        btnSimpanPassword.setOnClickListener {
            updatePassword()
        }

        loadProfile()
    }

    private fun loadProfile() {
        val session = SessionManager(this)
        val userId = session.getUserId()
        if (userId.isEmpty()) {
            Toast.makeText(this, "Belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val url = ApiConfig.GET_PROFILE
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
                    val data = json.optJSONObject("data") ?: json
                    if (json.optBoolean("success", true)) {
                        txtNis.text = data.optString("nis")
                            .ifBlank { "-" }
                        txtNama.text = data.optString("nama")
                            .ifBlank { session.getUsername() }
                            .ifBlank { "-" }
                        edtPassword.setText(data.optString("password"))
                    } else {
                        Toast.makeText(
                            this,
                            json.optString("message", "Gagal ambil profil"),
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun updatePassword() {
        val session = SessionManager(this)
        val userId = session.getUserId()
        val passwordBaru = edtPassword.text.toString().trim()

        if (userId.isEmpty()) {
            Toast.makeText(this, "Belum login", Toast.LENGTH_SHORT).show()
            return
        }

        if (passwordBaru.isEmpty()) {
            edtPassword.error = "Password tidak boleh kosong"
            return
        }

        val url = ApiConfig.UPDATE_PROFILE
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                val isSuccess = if (clean.startsWith("{")) {
                    val json = JSONObject(clean)
                    json.optBoolean("success", false) ||
                        json.optString("status").equals("success", ignoreCase = true) ||
                        json.optString("status").equals("sukses", ignoreCase = true)
                } else {
                    val lower = clean.lowercase()
                    lower.contains("success") || lower.contains("berhasil") || lower.contains("sukses")
                }

                if (isSuccess) {
                    Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        if (clean.isBlank()) "Gagal mengubah password" else clean,
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Gagal koneksi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "id_user" to userId,
                    "password" to passwordBaru
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
