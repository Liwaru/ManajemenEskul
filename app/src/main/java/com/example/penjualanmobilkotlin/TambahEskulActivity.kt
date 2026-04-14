package com.example.penjualanmobilkotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.Base64

class TambahEskulActivity : AppCompatActivity() {

    private lateinit var tvJudulEskul: TextView
    private lateinit var etNamaEskul: TextInputEditText
    private lateinit var etNamaPembina: TextInputEditText
    private lateinit var etDeskripsi: TextInputEditText
    private lateinit var etJamMulai: TextInputEditText
    private lateinit var etJamSelesai: TextInputEditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnPilihGambar: Button
    private lateinit var btnSimpan: Button

    private var imageBase64: String? = null
    private var isEditMode = false
    private var idEskul = 0

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_eskul)

        tvJudulEskul = findViewById(R.id.tvJudulEskul)
        etNamaEskul = findViewById(R.id.etNamaEskul)
        etNamaPembina = findViewById(R.id.etNamaPembina)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        etJamMulai = findViewById(R.id.etJamMulai)
        etJamSelesai = findViewById(R.id.etJamSelesai)
        imgPreview = findViewById(R.id.imgPreview)
        btnPilihGambar = findViewById(R.id.btnPilihGambar)
        btnSimpan = findViewById(R.id.btnSimpan)

        setupMode()

        btnPilihGambar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSimpan.setOnClickListener {
            if (validateInput()) {
                if (isEditMode) {
                    updateEskul()
                } else {
                    simpanEskul()
                }
            }
        }
    }

    private fun setupMode() {
        isEditMode = intent.getStringExtra("mode").equals("edit", ignoreCase = true)
        idEskul = intent.getIntExtra("id_eskul", 0)

        if (isEditMode) {
            tvJudulEskul.text = "Edit Eskul"
            btnSimpan.text = "Update"

            etNamaEskul.setText(intent.getStringExtra("nama_eskul").orEmpty())
            etNamaPembina.setText(intent.getStringExtra("nama_pembina").orEmpty())
            etDeskripsi.setText(intent.getStringExtra("deskripsi").orEmpty())
            etJamMulai.setText(intent.getStringExtra("jam_mulai").orEmpty())
            etJamSelesai.setText(intent.getStringExtra("jam_selesai").orEmpty())
            imgPreview.setImageResource(getEskulDrawable(idEskul))
        } else {
            tvJudulEskul.text = "Tambah Eskul"
            btnSimpan.text = "Tambah"
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
        if (!isEditMode && imageBase64 == null) {
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
        val url = "http://192.168.0.15/manajemeneskul/tambah_eskul.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                handleEskulResponse(response, "Data berhasil ditambahkan")
            },
            { error ->
                Toast.makeText(this, "Volley Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return buildEskulParams(includeImage = true)
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun updateEskul() {
        val url = "http://192.168.0.15/manajemeneskul/update_eskul.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                handleEskulResponse(response, "Data berhasil diupdate")
            },
            { error ->
                Toast.makeText(this, "Volley Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return buildEskulParams(includeImage = imageBase64 != null).toMutableMap().apply {
                    put("id_eskul", idEskul.toString())
                    put("id", idEskul.toString())
                }
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun buildEskulParams(includeImage: Boolean): Map<String, String> {
        return HashMap<String, String>().apply {
            put("nama_eskul", etNamaEskul.text.toString())
            put("nama_pembina", etNamaPembina.text.toString())
            put("deskripsi", etDeskripsi.text.toString())
            put("jam_mulai", etJamMulai.text.toString())
            put("jam_selesai", etJamSelesai.text.toString())
            if (includeImage) {
                put("gambar", imageBase64 ?: "")
            }
        }
    }

    private fun handleEskulResponse(response: String, successMessage: String) {
        val clean = JsonUtils.cleanResponse(response)
        try {
            if (clean.startsWith("{")) {
                val json = JSONObject(clean)
                val isSuccess = json.optBoolean("success", false) ||
                    json.optString("status").equals("success", ignoreCase = true) ||
                    json.optString("status").equals("sukses", ignoreCase = true)

                if (isSuccess) {
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        json.optString("message", "Gagal menyimpan data"),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }

            val lower = clean.lowercase()
            if (lower.contains("success") || lower.contains("berhasil") || lower.contains("sukses")) {
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, if (clean.isBlank()) "Gagal menyimpan data" else clean, Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: $clean", Toast.LENGTH_LONG).show()
        }
    }

    private fun getEskulDrawable(idEskul: Int): Int {
        return when (idEskul) {
            1 -> R.drawable.futsal
            2 -> R.drawable.basket
            3 -> R.drawable.voli
            4 -> R.drawable.badminton
            5 -> R.drawable.musik
            6 -> R.drawable.catur
            else -> R.drawable.ic_default_eskul
        }
    }
}
