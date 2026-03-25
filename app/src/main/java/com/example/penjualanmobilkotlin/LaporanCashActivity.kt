package com.example.penjualanmobilkotlin

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.util.Calendar

class LaporanCashActivity : AppCompatActivity() {

    private lateinit var spinnerPeriode: Spinner
    private lateinit var etTanggal: EditText
    private lateinit var listLaporan: ListView

    private val listData = ArrayList<String>()

    companion object {
        private const val URL_LAPOR =
            "http://10.80.250.56/penjualanmobil/LaporanCash.php"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.laporanjualcash)

        spinnerPeriode = findViewById(R.id.spinnerpilihan)
        etTanggal = findViewById(R.id.edittanggal)
        listLaporan = findViewById(R.id.listcash)

        val periode = arrayOf("Bulanan", "Tahunan")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            periode
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriode.adapter = adapter

        etTanggal.setOnClickListener {
            showDatePicker()
        }

        spinnerPeriode.onItemSelectedListener =
            SimpleItemSelectedListener { loadData() }
    }

    private fun showDatePicker() {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val selectedPeriode = spinnerPeriode.selectedItem.toString()

        val dialog = DatePickerDialog(
            this,
            { _, y, m, _ ->

                if (selectedPeriode == "Tahunan") {

                    etTanggal.setText(y.toString())

                } else {

                    val tanggal = "$y-${String.format("%02d", m + 1)}"
                    etTanggal.setText(tanggal)
                }

                loadData()

            },
            year,
            month,
            1
        )

        try {

            val dayId = resources.getIdentifier("day", "id", "android")
            val monthId = resources.getIdentifier("month", "id", "android")

            val datePicker = dialog.datePicker

            if (selectedPeriode == "Tahunan") {

                datePicker.findViewById<android.view.View>(dayId)?.visibility =
                    android.view.View.GONE
                datePicker.findViewById<android.view.View>(monthId)?.visibility =
                    android.view.View.GONE

            } else {

                datePicker.findViewById<android.view.View>(dayId)?.visibility =
                    android.view.View.GONE
            }

        } catch (e: Exception) {

            Log.e("DatePicker", "Gagal menyembunyikan field: ${e.message}")
        }

        dialog.show()
    }

    private fun loadData() {

        val periode =
            if (spinnerPeriode.selectedItem.toString() == "Tahunan")
                "tahun"
            else
                "bulan"

        val tanggal = etTanggal.text.toString().trim()

        if (tanggal.isEmpty()) return

        val url = "$URL_LAPOR?periode=$periode&tanggal=$tanggal"

        val request = StringRequest(
            Request.Method.GET,
            url,

            { response ->

                try {

                    val arr = JSONArray(response)

                    listData.clear()

                    for (i in 0 until arr.length()) {

                        val obj = arr.getJSONObject(i)

                        val tampil =
                            "Kode Cash : ${obj.getString("kode_cash")}\n" +
                                    "Pembeli : ${obj.getString("nama_pembeli")}\n" +
                                    "Mobil : ${obj.getString("merk")} ${obj.getString("type")}\n" +
                                    "Tanggal : ${obj.getString("cash_tgl")}\n" +
                                    "Bayar : Rp ${obj.getString("cash_bayar")}"

                        listData.add(tampil)
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        listData
                    )

                    listLaporan.adapter = adapter

                } catch (e: Exception) {

                    Log.e("JSONError", e.message ?: "Error")

                    Toast.makeText(
                        this,
                        "Gagal memproses data!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },

            {

                Toast.makeText(
                    this,
                    "Gagal Memuat Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }
}