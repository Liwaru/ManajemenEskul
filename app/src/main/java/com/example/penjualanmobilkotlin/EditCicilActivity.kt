package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class EditCicilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pembeli)

        val editKtp = findViewById<EditText>(R.id.editktp)
        val editNama = findViewById<EditText>(R.id.editnama)
        val editAlamat = findViewById<EditText>(R.id.editalamat)
        val editNoHp = findViewById<EditText>(R.id.editnohp)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)

        editKtp.setText(intent.getStringExtra("ktp"))
        editNama.setText(intent.getStringExtra("nama"))
        editAlamat.setText(intent.getStringExtra("alamat"))
        editNoHp.setText(intent.getStringExtra("telp"))

        btnUpdate.setOnClickListener {

            val ktp = editKtp.text.toString()
            val nama = editNama.text.toString()
            val alamat = editAlamat.text.toString()
            val nohp = editNoHp.text.toString()

            updateData(ktp,nama,alamat,nohp)

        }
    }

    private fun updateData(ktp:String,nama:String,alamat:String,nohp:String){

        val url = "http://10.208.184.71/Penjualanmobilkotlinvscode/editpembeli.php"

        val request = object : StringRequest(
            Method.POST,url,

            { response ->

                val json = JSONObject(response)
                val message = json.getString("message")

                Toast.makeText(this,message,Toast.LENGTH_SHORT).show()

                if(json.getInt("status") == 1){
                    setResult(RESULT_OK)
                    finish()
                }

            },

            {
                Toast.makeText(this,"Gagal koneksi",Toast.LENGTH_SHORT).show()
            }

        ){

            override fun getParams(): MutableMap<String,String>{

                val params = HashMap<String,String>()

                params["ktp"] = ktp
                params["nama_pembeli"] = nama
                params["alamat_pembeli"] = alamat
                params["telp_pembeli"] = nohp

                return params
            }

        }

        Volley.newRequestQueue(this).add(request)

    }

}