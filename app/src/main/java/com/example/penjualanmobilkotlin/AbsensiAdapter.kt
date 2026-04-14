package com.example.penjualanmobilkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AbsensiAdapter(
    context: Context,
    private val dataList: ArrayList<Absensi>
) : ArrayAdapter<Absensi>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_absensi, parent, false)

        val current = dataList[position]

        itemView.findViewById<TextView>(R.id.tvNamaSiswa).text = current.namaSiswa
        itemView.findViewById<TextView>(R.id.tvTanggalAbsensi).text =
            "Tanggal: ${current.tanggalAbsensi}"

        return itemView
    }
}
