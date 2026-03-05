package com.example.penjualanmobilkotlin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class EditPembeliActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pembeli)
        val editKtp=findViewById<EditText>(R.id.editktp)
        val editNama=findViewById<EditText>(R.id.editnama)
        val editAlamat=findViewById<EditText>(R.id.editalamat)
        val editNoHp=findViewById<EditText>(R.id.editnohp)
        val btnUpdate=findViewById<Button>(R.id.btnUpdate)

        editKtp.setText(intent.getStringExtra("ktp"))
        editNama.setText(intent.getStringExtra("nama"))
        editAlamat.setText(intent.getStringExtra("alamat"))
        editNoHp.setText(intent.getStringExtra("telp"))

        btnUpdate.setOnClickListener {
            updateData(
                editKtp.text.toString(),
                editNama.text.toString(),
                editAlamat.text.toString(),
                editNoHp.text.toString(),
            )
        }
    }

    private fun updateData(ktp:String,nama:String,alamat:String,nohp:String){
        val url="http://192.168.0.15/penjualanmobil/editpembeli.php";
        val request=object : StringRequest(
            Method.POST,url,{
            response->
            val json = JSONObject(response)
            Toast.makeText(this,json.getString("message"), Toast.LENGTH_SHORT).show()
            if(json.getBoolean("success")){
                setResult(RESULT_OK)
                finish()
            }
        },
        {
            Toast.makeText(this, "gagal koneksi", Toast.LENGTH_SHORT).show()
        })
        {
            override fun getParams(): MutableMap<String, String>{
                return hashMapOf(
                    "ktp" to ktp,
                    "nama_pembeli" to nama,
                    "alamat_pembeli" to alamat,
                    "telp_pembeli" to nohp
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}