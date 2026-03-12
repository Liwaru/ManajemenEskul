package com.example.penjualanmobilkotlin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class TambahKreditActivity : AppCompatActivity() {

    private lateinit var etKodeKredit: EditText
    private lateinit var etTanggal: EditText
    private lateinit var etCicilan: EditText

    private lateinit var spinnerKtp: Spinner
    private lateinit var spinnerPaket: Spinner
    private lateinit var spinnerMobil: Spinner

    private lateinit var btnSimpan: Button

    private val listPembeli = ArrayList<String>()
    private val listMobil = ArrayList<String>()
    private val listPaket = ArrayList<String>()

    private val listKtpValue = ArrayList<String>()
    private val listMobilValue = ArrayList<String>()
    private val listPaketValue = ArrayList<String>()

    private val hargaMobil = ArrayList<Int>()
    private val tenor = ArrayList<Int>()
    private val uangMuka = ArrayList<Double>()
    private val bunga = ArrayList<Double>()

    private var cicilanPerbulan = 0
    private var totalCicil = 0
    private var tenorDipilih = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_tambah_kredit)

        etKodeKredit = findViewById(R.id.editkodekredit)
        etTanggal = findViewById(R.id.edittanggal)
        etCicilan = findViewById(R.id.editcicilan)

        spinnerKtp = findViewById(R.id.spinnerktp)
        spinnerPaket = findViewById(R.id.spinnerpaket)
        spinnerMobil = findViewById(R.id.spinnerkodemobil)

        btnSimpan = findViewById(R.id.tombolsimpan)

        loadPembeli()
        loadMobil()
        loadPaket()

        etTanggal.setOnClickListener {
            showDatePicker()
        }

        spinnerPaket.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    hitungCicilan()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        spinnerMobil.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    hitungCicilan()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        btnSimpan.setOnClickListener {

            val kodeKredit = etKodeKredit.text.toString()
            val tanggal = etTanggal.text.toString()

            if (kodeKredit.isEmpty() || tanggal.isEmpty() || cicilanPerbulan == 0) {

                Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()

            } else {

                val posPembeli = spinnerKtp.selectedItemPosition
                val posMobil = spinnerMobil.selectedItemPosition
                val posPaket = spinnerPaket.selectedItemPosition

                val ktp = listKtpValue[posPembeli]
                val kodeMobil = listMobilValue[posMobil]
                val kodePaket = listPaketValue[posPaket]

                simpanData(
                    kodeKredit,
                    ktp,
                    kodePaket,
                    kodeMobil,
                    tanggal,
                    cicilanPerbulan,
                    tenorDipilih,
                    totalCicil
                )
            }
        }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, y, m, d ->

                val tanggal = "$y-${m + 1}-$d"
                etTanggal.setText(tanggal)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun hitungCicilan() {

        val posPaket = spinnerPaket.selectedItemPosition
        val posMobil = spinnerMobil.selectedItemPosition

        if (posPaket >= 0 && posMobil >= 0 &&
            hargaMobil.isNotEmpty() &&
            uangMuka.isNotEmpty()
        ) {

            val harga = hargaMobil[posMobil]
            val dpPersen = uangMuka[posPaket]

            tenorDipilih = tenor[posPaket]
            val bungaPaket = bunga[posPaket]

            val dpRupiah = harga * dpPersen / 100.0
            val hutang = harga - dpRupiah

            val bungaPerBulan = (hutang * bungaPaket / 100.0) / 12.0
            val bungaTotal = bungaPerBulan * tenorDipilih

            totalCicil = (hutang + bungaTotal).toInt()

            cicilanPerbulan = totalCicil / tenorDipilih

            etCicilan.setText("Rp $cicilanPerbulan")
        }
    }

    private fun loadPembeli() {

        val request = StringRequest(
            Request.Method.GET,
            URL_PEMBELI,

            { response ->

                val jsonArray = JSONArray(response)

                listPembeli.clear()
                listKtpValue.clear()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val ktp = obj.getString("ktp")
                    val nama = obj.getString("nama_pembeli")

                    listPembeli.add("$ktp - $nama")
                    listKtpValue.add(ktp)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listPembeli
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spinnerKtp.adapter = adapter

            },
            { error ->
                Log.e("Volley", error.toString())
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun loadMobil() {

        val request = StringRequest(
            Request.Method.GET,
            URL_MOBIL,

            { response ->

                val jsonArray = JSONArray(response)

                listMobil.clear()
                listMobilValue.clear()
                hargaMobil.clear()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val kode = obj.getString("kode_mobil")
                    val merk = obj.getString("merk")
                    val type = obj.getString("type")
                    val harga = obj.getInt("harga")

                    listMobil.add("$kode - $merk $type")

                    listMobilValue.add(kode)
                    hargaMobil.add(harga)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listMobil
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spinnerMobil.adapter = adapter

            },
            { error ->
                Log.e("Volley", error.toString())
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun loadPaket() {

        val request = StringRequest(
            Request.Method.GET,
            URL_PAKET,

            { response ->

                val jsonArray = JSONArray(response)

                listPaket.clear()
                listPaketValue.clear()
                uangMuka.clear()
                tenor.clear()
                bunga.clear()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val kode = obj.getString("kode_paket")
                    val dp = obj.getDouble("uang_muka")
                    val tnr = obj.getInt("tenor")
                    val bng = obj.getDouble("bunga_cicilan")

                    listPaket.add("$kode - DP:$dp% Tenor:$tnr Bunga:$bng%")

                    listPaketValue.add(kode)
                    uangMuka.add(dp)
                    tenor.add(tnr)
                    bunga.add(bng)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listPaket
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spinnerPaket.adapter = adapter

            },
            { error ->
                Log.e("Volley", error.toString())
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun simpanData(
        kodeKredit: String,
        ktp: String,
        kodePaket: String,
        kodeMobil: String,
        tanggal: String,
        bayarKredit: Int,
        tenorVal: Int,
        totalCicilVal: Int
    ) {

        val request = object : StringRequest(
            Method.POST,
            URL_TAMBAH,

            { response ->

                Log.d("Response", response)

                val json = JSONObject(response)

                val success = json.getBoolean("success")
                val message = json.getString("message")

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                if (success) {

                    startActivity(
                        Intent(this, DataKreditActivity::class.java)
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

                Log.e("Volley error", error.toString())
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                val params = HashMap<String, String>()

                params["kode_kredit"] = kodeKredit
                params["ktp"] = ktp
                params["kode_paket"] = kodePaket
                params["kode_mobil"] = kodeMobil
                params["tanggal_kredit"] = tanggal
                params["bayar_kredit"] = bayarKredit.toString()
                params["tenor"] = tenorVal.toString()
                params["totalcicil"] = totalCicilVal.toString()

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    companion object {

        private const val URL_TAMBAH =
            "http://192.168.0.15/Penjualanmobil/TambahKredit.php"

        private const val URL_PEMBELI =
            "http://192.168.0.15/Penjualanmobil/Tampilpembeli.php"

        private const val URL_MOBIL =
            "http://192.168.0.15/Penjualanmobil/Tampilmobil.php"

        private const val URL_PAKET =
            "http://192.168.0.15/Penjualanmobil/Tampilpaket.php"
    }
}