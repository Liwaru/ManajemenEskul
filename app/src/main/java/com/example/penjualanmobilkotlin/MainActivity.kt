package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var edituser: EditText
    private lateinit var editpass: EditText
    private lateinit var tombolsimpan: Button
    private val loginUrls = listOf(
        ApiConfig.LOGIN,
        "http://10.0.2.2/ManajemenEskulvscode/login.php"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edituser = findViewById(R.id.edituser)
        editpass = findViewById(R.id.editpass)
        tombolsimpan = findViewById(R.id.tombolsimpan)

        tombolsimpan.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val nama = edituser.text.toString().trim()
        val password = editpass.text.toString().trim()

        if (nama.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        attemptLogin(nama, password, 0)
    }

    private fun attemptLogin(nama: String, password: String, urlIndex: Int) {
        val loginUrl = loginUrls[urlIndex]

        val stringRequest = object : StringRequest(
            Method.POST, loginUrl,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                if (clean.isBlank()) {
                    if (urlIndex < loginUrls.lastIndex) {
                        attemptLogin(nama, password, urlIndex + 1)
                    } else {
                        Toast.makeText(this, "Server login tidak mengirim data", Toast.LENGTH_LONG).show()
                    }
                    return@Listener
                }

                try {
                    val json = parseLoginJson(clean)
                    val success = json.optBoolean("success", false) ||
                        json.optInt("success", 0) == 1 ||
                        json.optInt("value", 0) == 1 ||
                        json.optString("status").equals("success", ignoreCase = true)

                    if (success) {
                        val level = json.optInt(
                            "level",
                            json.optInt("role", json.optInt("hak_akses", 0))
                        )
                        val idUser = json.optString("id_user")
                            .ifBlank { json.optString("id") }
                            .ifBlank { json.optString("user_id") }
                        val idEskul = json.optInt("id_eskul", 0)
                        val idPembina = json.optString("id_pembina")
                            .ifBlank { json.optString("pembina_id") }
                            .ifBlank {
                                if (level == 2) idUser else ""
                            }

                        if (idUser.isBlank()) {
                            Toast.makeText(this, "Login berhasil tapi id_user tidak ditemukan", Toast.LENGTH_LONG).show()
                            return@Listener
                        }

                        SessionManager(this).saveSession(
                            idUser = idUser,
                            username = nama,
                            idEskul = idEskul,
                            userLevel = level,
                            idPembina = idPembina
                        )

                        when (level) {
                            1 -> startActivity(Intent(this, BerandaSActivity::class.java))
                            2 -> startActivity(Intent(this, BerandaPActivity::class.java))
                            3 -> startActivity(Intent(this, BerandaAActivity::class.java))
                            else -> {
                                Toast.makeText(this, "Level tidak dikenali", Toast.LENGTH_SHORT).show()
                                return@Listener
                            }
                        }
                        finish()
                    } else {
                        val message = json.optString("message")
                            .ifBlank { json.optString("pesan") }
                            .ifBlank { "Login gagal" }
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Response login tidak valid: $clean", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                if (urlIndex < loginUrls.lastIndex) {
                    attemptLogin(nama, password, urlIndex + 1)
                } else {
                    Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("nama" to nama, "password" to password)
            }
        }
        stringRequest.setShouldCache(false)
        val queue = Volley.newRequestQueue(this)
        queue.cache.clear()
        queue.add(stringRequest)
    }

    private fun parseLoginJson(response: String): JSONObject {
        return when {
            response.startsWith("{") -> JSONObject(response)
            response.startsWith("[") -> {
                val jsonArray = JSONArray(response)
                if (jsonArray.length() > 0) jsonArray.getJSONObject(0) else JSONObject()
            }
            else -> throw JSONException("Unexpected login response")
        }
    }
}
