package com.example.penjualanmobilkotlin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.util.Calendar

class TambahKreditActivity : AppCompatActivity() {

    private lateinit var etKodeKredit: EditText
    private lateinit var etTanggal: EditText
    private lateinit var etCicilan: EditText
    private lateinit var spinnerPembeli: Spinner
    private lateinit var spinnerPaket: Spinner
    private lateinit var spinnerMobil: Spinner
    private lateinit var btnSimpan: Button

    private val listPembeli = ArrayList<String>()
    private val listPaket = ArrayList<String>()
    private val listMobil = ArrayList<String>()

    private val valPembeli = ArrayList<String>()
    private val valPaket = ArrayList<String>()
    private val valMobil = ArrayList<String>()

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

        spinnerPembeli = findViewById(R.id.spinnerktp)
        spinnerPaket = findViewById(R.id.spinnerpaket)
        spinnerMobil = findViewById(R.id.spinnerkodemobil)

        btnSimpan = findViewById(R.id.tombolsimpan)

        loadPembeli()
        loadMobil()
        loadPaket()

        etTanggal.setOnClickListener {
            showDatePicker()
        }

        spinnerPaket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                hitungCicilan()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerMobil.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
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

                val posPembeli = spinnerPembeli.selectedItemPosition
                val posMobil = spinnerMobil.selectedItemPosition
                val posPaket = spinnerPaket.selectedItemPosition

                val ktp = valPembeli[posPembeli]
                val kodeMobil = valMobil[posMobil]
                val kodePaket = valPaket[posPaket]

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
            { _, year, month, day ->

                val tanggal = "$year-${month + 1}-$day"

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

        val request = StringRequest(Request.Method.GET, URL_PEMBELI,
            { response ->

                val jsonArray = JSONArray(response)

                listPembeli.clear()
                valPembeli.clear()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val ktp = obj.getString("ktp")
                    val nama = obj.getString("nama_pembeli")

                    listPembeli.add("$ktp - $nama")
                    valPembeli.add(ktp)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listPembeli
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinnerPembeli.adapter = adapter

            },
            { error ->
                Log.e("Volley", error.toString())
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun loadMobil() {

        val request = StringRequest(Request.Method.GET, URL_MOBIL,
            { response ->

                val jsonArray = JSONArray(response)

                listMobil.clear()
                valMobil.clear()
                hargaMobil.clear()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val kode = obj.getString("kode_mobil")
                    val merk = obj.getString("merk")
                    val type = obj.getString("type")

                    val harga = obj.getInt("harga")

                    listMobil.add("$kode - $merk $type")

                    valMobil.add(kode)
                    hargaMobil.add(harga)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listMobil
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinnerMobil.adapter = adapter

            },
            { error ->
                Log.e("Volley", error.toString())
            })

        Volley.newRequestQueue(this).add(request)
    }

    private fun loadPaket() {

        val request = StringRequest(Request.Method.GET, URL_PAKET,
            { response ->

                val jsonArray = JSONArray(response)

                listPaket.clear()
                valPaket.clear()
                uangMuka.clear()
                tenor.clear()
                bunga.clear()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val kode = obj.getString("kode_paket")
                    val dp = obj.getDouble("uang_muka")
                    val tnr = obj.getInt("tenor")
                    val bng = obj.getDouble("bunga_cicilan")

                    listPaket.add("$kode - DP:$dp% Tenor:$tnr Bunga$bng%")

                    valPaket.add(kode)
                    uangMuka.add(dp)
                    tenor.add(tnr)
                    bunga.add(bng)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listPaket
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinnerPaket.adapter = adapter

            },
            { error ->
                Log.e("Volley", error.toString())
            })

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
            Method.POST, URL_TAMBAH,

            { response ->

                Toast.makeText(this, response, Toast.LENGTH_LONG).show()

                if (response.contains("Data berhasil disimpan")) {

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
            "http://10.208.184.71/Penjualanmobilkotlinvscode/TambahKredit.php"

        private const val URL_PEMBELI =
            "http://10.208.184.71/Penjualanmobilkotlinvscode/Tampilpembeli.php"

        private const val URL_MOBIL =
            "http://10.208.184.71/Penjualanmobilkotlinvscode/Tampilmobil.php"

        private const val URL_PAKET =
            "http://10.208.184.71/Penjualanmobilkotlinvscode/Tampilpaket.php"
    }
}