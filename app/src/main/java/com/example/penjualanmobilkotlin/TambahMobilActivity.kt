package com.example.penjualanmobilkotlin

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.IOException

class TambahMobilActivity : AppCompatActivity() {

    private lateinit var etKode: EditText
    private lateinit var etMerk: EditText
    private lateinit var etType: EditText
    private lateinit var etWarna: EditText
    private lateinit var etHarga: EditText

    private lateinit var btnSimpan: Button
    private lateinit var btnPilihGambar: Button
    private lateinit var ivPreview: ImageView

    private var bitmap: Bitmap? = null
    private var encodedImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_mobil)

        etKode = findViewById(R.id.editkode)
        etMerk = findViewById(R.id.editmerk)
        etType = findViewById(R.id.edittype)
        etWarna = findViewById(R.id.editwarna)
        etHarga = findViewById(R.id.editharga)

        btnSimpan = findViewById(R.id.tombolsimpan)
        btnPilihGambar = findViewById(R.id.tombolpilihgambar)

        ivPreview = findViewById(R.id.imagepreview)

        btnPilihGambar.setOnClickListener {
            pilihGambar()
        }

        btnSimpan.setOnClickListener {

            val kode = etKode.text.toString()
            val merk = etMerk.text.toString()
            val type = etType.text.toString()
            val warna = etWarna.text.toString()
            val harga = etHarga.text.toString()

            if (kode.isEmpty() ||
                merk.isEmpty() ||
                type.isEmpty() ||
                warna.isEmpty() ||
                harga.isEmpty() ||
                encodedImage.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Harap isi semua data dan pilih gambar!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                simpanData(kode, merk, type, warna, harga)
            }
        }
    }

    private fun pilihGambar() {

        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == RESULT_OK &&
            data?.data != null
        ) {

            val filePath = data.data

            try {

                bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    filePath
                )

                ivPreview.setImageBitmap(bitmap)

                bitmap?.let {
                    encodeBitmapToBase64(it)
                }

            } catch (e: IOException) {

                e.printStackTrace()

            }
        }
    }

    private fun encodeBitmapToBase64(bmp: Bitmap) {

        val baos = ByteArrayOutputStream()

        bmp.compress(
            Bitmap.CompressFormat.JPEG,
            80,
            baos
        )

        val imageBytes = baos.toByteArray()

        encodedImage = Base64.encodeToString(
            imageBytes,
            Base64.DEFAULT
        )
    }

    private fun simpanData(
        kode: String,
        merk: String,
        type: String,
        warna: String,
        harga: String
    ) {

        val request = object : StringRequest(
            Method.POST,
            URL,

            { response ->

                Log.d("Response", response)

                Toast.makeText(
                    this,
                    response,
                    Toast.LENGTH_LONG
                ).show()

                if (response.contains("Data berhasil disimpan")) {

                    startActivity(
                        Intent(
                            this,
                            DataMobilActivity::class.java
                        )
                    )

                    finish()
                }

            },

            { error ->

                Toast.makeText(
                    this,
                    "Gagal Menyimpan Data!",
                    Toast.LENGTH_SHORT
                ).show()

                Log.e("Volley Error", error.toString())
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                val params = HashMap<String, String>()

                params["kode_mobil"] = kode
                params["merk"] = merk
                params["type"] = type
                params["warna"] = warna
                params["harga"] = harga
                params["foto"] = encodedImage

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    companion object {

        private const val PICK_IMAGE_REQUEST = 1

        private const val URL =
            "http://10.208.184.71/Penjualanmobilkotlinvscode/Tambahmobil.php"
    }
}