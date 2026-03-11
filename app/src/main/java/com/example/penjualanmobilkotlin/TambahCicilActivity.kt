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

class TambahCicilActivity : AppCompatActivity() {

    private lateinit var etKodeCicilan: EditText
    private lateinit var etTanggal: EditText
    private lateinit var etCicilanKe: EditText
    private lateinit var etJumlahCicilan: EditText
    private lateinit var etSisaKe: EditText
    private lateinit var etSisaCicilan: EditText

    private lateinit var spinnerKredit: Spinner
    private lateinit var btnSimpan: Button

    private val listKredit = ArrayList<String>()
    private val valKredit = ArrayList<String>()

    private val bayarKredit = ArrayList<Int>()
    private val cicilanKeList = ArrayList<Int>()
    private val sisaKeList = ArrayList<Int>()
    private val sisaHargaList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_cicil)

        etKodeCicilan = findViewById(R.id.editkodecicilan)
        etTanggal = findViewById(R.id.edittanggal)
        etCicilanKe = findViewById(R.id.editcicilke)
        etJumlahCicilan = findViewById(R.id.editbayar)
        etSisaKe = findViewById(R.id.editsisake)
        etSisaCicilan = findViewById(R.id.editsisaharga)

        spinnerKredit = findViewById(R.id.spinnerkodekredit)
        btnSimpan = findViewById(R.id.tombolsimpan)

        loadKredit()

        etTanggal.setOnClickListener {
            showDatePicker()
        }

        spinnerKredit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    isiOtomatis()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        btnSimpan.setOnClickListener {

            val kodeCicilan = etKodeCicilan.text.toString()
            val tanggal = etTanggal.text.toString()

            if (kodeCicilan.isEmpty() || tanggal.isEmpty()) {

                Toast.makeText(
                    this,
                    "Semua Data Wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val pos = spinnerKredit.selectedItemPosition

                val kodeKredit = valKredit[pos]

                val cicilKe = etCicilanKe.text.toString()
                val jumlahCicilan = etJumlahCicilan.text.toString()
                val sisaKe = etSisaKe.text.toString()
                val sisaCicilan = etSisaCicilan.text.toString()

                simpanData(
                    kodeCicilan,
                    kodeKredit,
                    tanggal,
                    cicilKe,
                    jumlahCicilan,
                    sisaKe,
                    sisaCicilan
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

    private fun loadKredit() {

        val request = StringRequest(
            Request.Method.GET,
            URL_KREDIT,

            { response ->

                val jsonArray = JSONArray(response)

                listKredit.clear()
                valKredit.clear()
                bayarKredit.clear()
                cicilanKeList.clear()
                sisaKeList.clear()
                sisaHargaList.clear()

                for (i in 0 until jsonArray.length()) {

                    val obj = jsonArray.getJSONObject(i)

                    val kode = obj.getString("kode_kredit")
                    val ktp = obj.getString("ktp")

                    listKredit.add("$kode - $ktp")

                    valKredit.add(kode)
                    bayarKredit.add(obj.getInt("bayar_kredit"))
                    cicilanKeList.add(obj.getInt("cicilan_ke"))
                    sisaKeList.add(obj.getInt("sisa_ke"))
                    sisaHargaList.add(obj.getInt("sisa_harga"))
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listKredit
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spinnerKredit.adapter = adapter

            },

            { error ->
                Log.e("Volley", error.toString())
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun isiOtomatis() {

        val pos = spinnerKredit.selectedItemPosition

        if (pos >= 0 && pos < cicilanKeList.size) {

            etCicilanKe.setText(cicilanKeList[pos].toString())

            etJumlahCicilan.setText(
                bayarKredit[pos].toString()
            )

            etSisaKe.setText(
                sisaKeList[pos].toString()
            )

            etSisaCicilan.setText(
                sisaHargaList[pos].toString()
            )
        }
    }

    private fun simpanData(
        kodeCicilan: String,
        kodeKredit: String,
        tanggal: String,
        cicilanKe: String,
        jumlahCicilan: String,
        sisaKe: String,
        sisaCicilan: String
    ) {

        val request = object : StringRequest(
            Method.POST,
            URL_TAMBAH,

            { response ->

                Toast.makeText(
                    this,
                    response,
                    Toast.LENGTH_LONG
                ).show()

                if (response.contains("Data berhasil disimpan!")) {

                    startActivity(
                        Intent(
                            this,
                            DataCicilActivity::class.java
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

                Log.e("Volley error", error.toString())
            }

        ) {

            override fun getParams(): MutableMap<String, String> {

                val params = HashMap<String, String>()

                params["kode_cicilan"] = kodeCicilan
                params["kode_kredit"] = kodeKredit
                params["tanggal_cicilan"] = tanggal
                params["cicilanke"] = cicilanKe
                params["jumlah_cicilan"] = jumlahCicilan
                params["sisacicilke"] = sisaKe
                params["sisa_cicilan"] = sisaCicilan

                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    companion object {

        private const val URL_KREDIT =
            "http://10.208.184.71/Penjualanmobilkotlinvscode/Tampilkredithitung.php"

        private const val URL_TAMBAH =
            "http://10.208.184.71/Penjualanmobilkotlinvscode/TambahCicilan.php"
    }
}