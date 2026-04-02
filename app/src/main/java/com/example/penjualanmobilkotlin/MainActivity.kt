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
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var edituser: EditText
    private lateinit var editpass: EditText
    private lateinit var tombolsimpan: Button
    private val URL_LOGIN = "http://192.168.0.15/manajemeneskul/login.php"

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

        val stringRequest = object : StringRequest(
            Method.POST, URL_LOGIN,
            Response.Listener { response ->
                val clean = JsonUtils.cleanResponse(response)
                if (!clean.startsWith("{")) {
                    Toast.makeText(this, "Server error: $clean", Toast.LENGTH_LONG).show()
                    return@Listener
                }
                try {
                    val json = JSONObject(clean)
                    val success = json.optBoolean("success", false)
                    if (success) {
                        val level = json.optInt("level", 0)
                        val idUser = json.optString("id_user", "")
                        SessionManager(this).saveSession(idUser)

                        when (level) {
                            1 -> startActivity(Intent(this, BerandaSActivity::class.java))
                            2 -> startActivity(Intent(this, BerandaPActivity::class.java))
                            3 -> startActivity(Intent(this, BerandaAActivity::class.java))
                            else -> Toast.makeText(this, "Level tidak dikenali", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    } else {
                        Toast.makeText(this, json.optString("message", "Login gagal"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing data", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf("nama" to nama, "password" to password)
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }
}