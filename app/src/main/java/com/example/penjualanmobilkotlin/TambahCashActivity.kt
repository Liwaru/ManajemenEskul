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
    
    class TambahCashActivity : AppCompatActivity() {
    
        private lateinit var etKodeCash: EditText
        private lateinit var etTanggal: EditText
        private lateinit var spinnerKtp: Spinner
        private lateinit var spinnerMobil: Spinner
        private lateinit var btnSimpan: Button
    
        private val listPembeli = ArrayList<String>()
        private val listMobil = ArrayList<String>()
        private val listKtpValue = ArrayList<String>()
        private val listMobilValue = ArrayList<String>()
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_tambah_cash)
    
            etKodeCash = findViewById(R.id.editkodecash)
            etTanggal = findViewById(R.id.edittanggal)
            spinnerKtp = findViewById(R.id.spinnerktp)
            spinnerMobil = findViewById(R.id.spinnerkodemobil)
            btnSimpan = findViewById(R.id.tombolsimpan)
    
            loadPembeli()
            loadMobil()
    
            etTanggal.setOnClickListener {
                showDatePicker()
            }
    
            btnSimpan.setOnClickListener {
    
                val kodeCash = etKodeCash.text.toString()
                val tanggal = etTanggal.text.toString()
    
                if (kodeCash.isEmpty() || tanggal.isEmpty()) {
                    Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show()
                } else {
    
                    val posPembeli = spinnerKtp.selectedItemPosition
                    val posMobil = spinnerMobil.selectedItemPosition
    
                    val ktp = listKtpValue[posPembeli]
                    val kodeMobil = listMobilValue[posMobil]
    
                    simpanData(kodeCash, ktp, kodeMobil, tanggal)
                }
            }
        }
    
        private fun showDatePicker() {
            val calendar = Calendar.getInstance()
    
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
    
            val dialog = DatePickerDialog(
                this,
                { _, y, m, d ->
                    val tanggal = "$y-${m + 1}-$d"
                    etTanggal.setText(tanggal)
                },
                year,
                month,
                day
            )
    
            dialog.show()
        }
    
        private fun loadPembeli() {
    
            val request = StringRequest(Request.Method.GET, URL_PEMBELI,
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
    
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    
                    spinnerKtp.adapter = adapter
    
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
                    listMobilValue.clear()
    
                    for (i in 0 until jsonArray.length()) {
    
                        val obj = jsonArray.getJSONObject(i)
    
                        val kode = obj.getString("kode_mobil")
                        val merk = obj.getString("merk")
                        val type = obj.getString("type")
    
                        listMobil.add("$kode - $merk $type")
                        listMobilValue.add(kode)
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
    
        private fun simpanData(
            kodeCash: String,
            ktp: String,
            kodeMobil: String,
            tanggal: String
        ) {
    
            val request = object : StringRequest(
                Method.POST, URL_TAMBAH,

                { response ->

                    Log.d("Response", response)

                    val json = JSONObject(response)
                    val success = json.getBoolean("success")
                    val message = json.getString("message")

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                    if (success) {
                        startActivity(Intent(this, DataCashActivity::class.java))
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
    
                    params["kode_cash"] = kodeCash
                    params["ktp"] = ktp
                    params["kode_mobil"] = kodeMobil
                    params["cash_tgl"] = tanggal
    
                    return params
                }
            }
    
            Volley.newRequestQueue(this).add(request)
        }
    
        companion object {
    
            private const val URL_TAMBAH =
                "http://10.80.250.56/Penjualanmobil/TambahCash.php"
    
            private const val URL_PEMBELI =
                "http://10.80.250.56/Penjualanmobil/Tampilpembeli.php"
    
            private const val URL_MOBIL =
                "http://10.80.250.56/Penjualanmobil/Tampilmobil.php"
        }
    }