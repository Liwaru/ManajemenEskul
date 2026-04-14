package com.example.penjualanmobilkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class PendaftaranAdapter(
    private val context: Context,
    private val listData: ArrayList<Pendaftaran>,
    private val onUpdateStatus: (Pendaftaran, String, String) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = listData.size

    override fun getItem(position: Int): Any = listData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_pendaftaran, parent, false)

        val txtNama = view.findViewById<TextView>(R.id.txtNama)
        val edtAlasanTolak = view.findViewById<EditText>(R.id.edtAlasanTolak)
        val btnTerima = view.findViewById<Button>(R.id.btnTerima)
        val btnTolak = view.findViewById<Button>(R.id.btnTolak)

        val item = listData[position]

        txtNama.text = buildString {
            append(item.namaSiswa)
            if (item.namaEskul.isNotBlank()) {
                append("\nEskul: ")
                append(item.namaEskul)
            }
            if (item.status.isNotBlank()) {
                append("\nStatus: ")
                append(item.status)
            }
            if (item.alasan.isNotBlank()) {
                append("\nAlasan: ")
                append(item.alasan)
            }
        }

        if (item.alasan.isNotBlank()) {
            edtAlasanTolak.setText(item.alasan)
        } else {
            edtAlasanTolak.setText("")
        }

        val isFinalStatus = item.status.equals("diterima", ignoreCase = true) ||
            item.status.equals("ditolak", ignoreCase = true)

        btnTerima.isEnabled = !isFinalStatus
        btnTolak.isEnabled = !isFinalStatus
        edtAlasanTolak.isEnabled = !isFinalStatus

        btnTerima.setOnClickListener {
            onUpdateStatus(item, "diterima", "")
        }

        btnTolak.setOnClickListener {
            val alasan = edtAlasanTolak.text.toString().trim()
            if (alasan.isEmpty()) {
                edtAlasanTolak.error = "Alasan penolakan harus diisi"
                Toast.makeText(context, "Isi alasan penolakan dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onUpdateStatus(item, "ditolak", alasan)
        }

        return view
    }
}
