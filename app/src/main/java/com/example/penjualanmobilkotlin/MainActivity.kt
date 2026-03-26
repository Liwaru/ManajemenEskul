package com.example.penjualanmobilkotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var edituser: EditText? = null
    private var editppass: EditText? = null
    private var tombolsimpan: Button? = null
    private val URL_LOGIN: String = "http://192.168.0.15/manajemeneskul/login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // PERBAIKAN: Inisialisasi View yang benar (menghapus blok .also yang salah)
        edituser = findViewById(R.id.edituser)
        editppass = findViewById(R.id.editpass)
        tombolsimpan = findViewById(R.id.tombolsimpan)

        // Menggunakan lambda untuk onClickListener agar lebih ringkas
        tombolsimpan?.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val nama = edituser?.text.toString().trim()
        val password = editppass?.text.toString().trim()

        if (nama.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "nama dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val stringRequest = object : StringRequest(
            Method.POST, URL_LOGIN,
            Response.Listener { response ->
                try {
                    val json = JSONObject(response)
                    val success = json.getBoolean("success")
                    if (success) {
                        val level = json.getInt("level")
                        val idUser = json.getString("id_user") // 🔥 TAMBAHAN

                        val session = SessionManager(this)
                        session.saveSession(idUser, "") // simpan id_user

                        // DEBUG (hapus nanti)
                        Toast.makeText(this, "Login sukses ID: $idUser", Toast.LENGTH_SHORT).show()
                        // Navigasi berdasarkan level user
                        when (level) {
                            1 -> {
                                startActivity(Intent(this, BerandaSActivity::class.java))
                                finish()
                            }
                            2 -> {
                                startActivity(Intent(this, BerandaPActivity::class.java))
                                finish()
                            }
                            else -> Toast.makeText(this, "Level tidak dikenali", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Koneksi gagal: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nama"] = nama
                params["password"] = password
                return params
            }
        }
        Volley.newRequestQueue(this).add(stringRequest)
    }
}