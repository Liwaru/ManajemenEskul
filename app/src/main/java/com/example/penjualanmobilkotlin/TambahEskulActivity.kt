package com.example.penjualanmobilkotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Base64

class TambahEskulActivity : AppCompatActivity() {

    private lateinit var etNamaEskul: TextInputEditText
    private lateinit var etNamaPembina: TextInputEditText
    private lateinit var etJamMulai: TextInputEditText
    private lateinit var etJamSelesai: TextInputEditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnPilihGambar: Button
    private lateinit var btnSimpan: Button

    private var imageBase64: String? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_eskul)

        etNamaEskul = findViewById(R.id.etNamaEskul)
        etNamaPembina = findViewById(R.id.etNamaPembina)
        etJamMulai = findViewById(R.id.etJamMulai)
        etJamSelesai = findViewById(R.id.etJamSelesai)
        imgPreview = findViewById(R.id.imgPreview)
        btnPilihGambar = findViewById(R.id.btnPilihGambar)
        btnSimpan = findViewById(R.id.btnSimpan)

        btnPilihGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSimpan.setOnClickListener {
            if (validateInput()) {
                simpanEskul()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (etNamaEskul.text.isNullOrEmpty()) {
            etNamaEskul.error = "Nama eskul harus diisi"
            return false
        }
        if (etNamaPembina.text.isNullOrEmpty()) {
            etNamaPembina.error = "Nama pembina harus diisi"
            return false
        }
        if (etJamMulai.text.isNullOrEmpty()) {
            etJamMulai.error = "Jam mulai harus diisi"
            return false
        }
        if (etJamSelesai.text.isNullOrEmpty()) {
            etJamSelesai.error = "Jam selesai harus diisi"
            return false
        }
        if (imageBase64 == null) {
            Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imgPreview.setImageURI(selectedImageUri)
            imageBase64 = convertUriToBase64(selectedImageUri)
        }
    }

    private fun convertUriToBase64(uri: Uri?): String? {
        uri ?: return null
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return bytes?.let { Base64.getEncoder().encodeToString(it) }
    }

    private fun simpanEskul() {
        val url = "http://192.168.0.15/manajemeneskul/tambah_eskul.php"  // endpoint PHP

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getBoolean("success")) {
                        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        finish() // kembali ke activity sebelumnya
                    } else {
                        Toast.makeText(this, "Gagal: ${json.getString("message")}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Volley Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["nama_eskul"] = etNamaEskul.text.toString()
                params["nama_pembina"] = etNamaPembina.text.toString()
                params["jam_mulai"] = etJamMulai.text.toString()
                params["jam_selesai"] = etJamSelesai.text.toString()
                params["gambar"] = imageBase64 ?: ""
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }
}