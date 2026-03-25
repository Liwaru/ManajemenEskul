package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ProfilSiswaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_siswa)

        val txtUsername = findViewById<TextView>(R.id.txtUsername)
        val txtPassword = findViewById<TextView>(R.id.txtPassword)

        val session = SessionManager(this)
        val idUser = session.getIdUser()

        val url = "http://192.168.1.8/manajemeneskul/get_profil.php"

        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                val json = JSONObject(response)

                if (json.getBoolean("success")) {
                    txtUsername.text = "Username: " + json.getString("username")
                    txtPassword.text = "Password: " + json.getString("password")
                } else {
                    Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Gagal koneksi", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("id_user" to idUser!!)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}