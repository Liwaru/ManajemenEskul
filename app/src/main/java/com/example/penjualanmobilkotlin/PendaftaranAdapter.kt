package com.example.penjualanmobilkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter

class PendaftaranAdapter(
    private val context: Context,
    private val listData: ArrayList<String>
) : BaseAdapter() {

    override fun getCount(): Int = listData.size

    override fun getItem(position: Int): Any = listData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_pendaftaran, parent, false)

        val txtNama = view.findViewById<TextView>(R.id.txtNama)
        val btnTerima = view.findViewById<Button>(R.id.btnTerima)
        val btnTolak = view.findViewById<Button>(R.id.btnTolak)

        val nama = listData[position]

        txtNama.text = nama

        btnTerima.setOnClickListener {
            Toast.makeText(context, "$nama diterima", Toast.LENGTH_SHORT).show()
        }

        btnTolak.setOnClickListener {
            Toast.makeText(context, "$nama ditolak", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}