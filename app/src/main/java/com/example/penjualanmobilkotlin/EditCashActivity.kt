package com.example.penjualanmobilkotlin

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class EditCashActivity : AppCompatActivity() {
    private var etTanggal: EditText? = null
    private var spinnerKtp: Spinner? = null
    private var spinnerMobil: Spinner? = null
    private var btnUpdate: Button? = null
    private var btnBatal: Button? = null
    private var kodeCash: String? = null

    private val listPembeli = ArrayList<String>()
    private val listMobil = ArrayList<String>()
    private val listKtpValue = ArrayList<String>()
    private val listMobilValue = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_beli_cash)

        etTanggal = findViewById(R.id.edittanggal)
        spinnerKtp = findViewById(R.id.spinnerktp)
        spinnerMobil = findViewById(R.id.spinnerkodemobil)
        btnUpdate = findViewById(R.id.tblupdate)
        btnBatal = findViewById(R.id.tblbatal)

        kodeCash = intent.getStringExtra("kode_cash")
        etTanggal?.setText(intent.getStringExtra("cash_tgl"))

        loadPembeli()
        loadMobil()

        etTanggal?.setOnClickListener { showDatePicker() }

        btnUpdate?.setOnClickListener {
            val tanggal = etTanggal?.text.toString().trim()
            val ktpPos = spinnerKtp?.selectedItemPosition ?: -1
            val mobilPos = spinnerMobil?.selectedItemPosition ?: -1

            if (tanggal.isEmpty()) {
                Toast.makeText(this, "Tanggal belum diisi!", Toast.LENGTH_SHORT).show()
            } else if (ktpPos == -1 || mobilPos == -1) {
                Toast.makeText(this, "Pilih data pembeli dan mobil!", Toast.LENGTH_SHORT).show()
            } else {
                val ktp = listKtpValue[ktpPos]
                val kodeMobil = listMobilValue[mobilPos]
                updateData(kodeCash ?: "", ktp, kodeMobil, tanggal)
            }
        }

        btnBatal?.setOnClickListener { finish() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val tanggal = "$year-${month + 1}-$dayOfMonth"
                etTanggal?.setText(tanggal)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadPembeli() {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, URL_PEMBELI,
            { response ->
                try {
                    listPembeli.clear()
                    listKtpValue.clear()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val ktp = obj.getString("ktp")
                        val nama = obj.getString("nama_pembeli")
                        listPembeli.add("$ktp - $nama")
                        listKtpValue.add(ktp)
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listPembeli)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerKtp?.adapter = adapter

                    // Set selected item sesuai kodeCash jika ada (optional)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal memuat data pembeli", Toast.LENGTH_SHORT).show()
                Log.e("LoadPembeliError", error.toString())
            })
        queue.add(stringRequest)
    }

    private fun loadMobil() {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, URL_MOBIL,
            { response ->
                try {
                    listMobil.clear()
                    listMobilValue.clear()
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val kode = obj.getString("kode_mobil")
                        val merk = obj.getString("merk")
                        val type = obj.getString("type")
                        listMobil.add("$kode - $merk $type")
                        listMobilValue.add(kode)
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listMobil)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerMobil?.adapter = adapter

                    // Set selected item sesuai kodeCash jika ada (optional)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal memuat data mobil", Toast.LENGTH_SHORT).show()
                Log.e("LoadMobilError", error.toString())
            })
        queue.add(stringRequest)
    }

    private fun updateData(kodeCash: String, ktp: String, kodeMobil: String, tanggal: String) {
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, URL_EDIT,
            { response ->
                try {
                    val json = JSONObject(response)
                    val message = json.optString("message", "Tidak ada pesan")
                    val status = json.optInt("status", 0)

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    if (status == 1) {
                        setResult(RESULT_OK)
                        finish()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this, "Respon tidak valid", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal update data!", Toast.LENGTH_SHORT).show()
                Log.e("UpdateDataError", error.toString())
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "kode_cash" to kodeCash,
                    "ktp" to ktp,
                    "kode_mobil" to kodeMobil,
                    "cash_tgl" to tanggal
                )
            }
        }
        queue.add(stringRequest)
    }

    companion object {
        private const val URL_PEMBELI = "http://192.168.0.15/penjualanmobil/Tampilpembeli.php"
        private const val URL_MOBIL = "http://192.168.0.15/penjualanmobil/Tampilmobil.php"
        private const val URL_EDIT = "http://192.168.0.15/penjualanmobil/EditCash.php"
    }
}