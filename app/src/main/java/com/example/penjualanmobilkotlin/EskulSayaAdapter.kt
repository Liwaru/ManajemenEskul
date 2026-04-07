package com.example.penjualanmobilkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class EskulSayaAdapter(
    context: Context,
    private val dataList: ArrayList<Eskul>
) : ArrayAdapter<Eskul>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_eskul_saya, parent, false)

        val current = dataList[position]

        itemView.findViewById<TextView>(R.id.tvNamaEskul).text = current.nama_eskul
        itemView.findViewById<TextView>(R.id.tvNamaPembina).text = "Pembina: ${current.nama_pembina}"
        itemView.findViewById<TextView>(R.id.tvJamMulai).text = "Jam Mulai: ${current.jam_mulai}"
        itemView.findViewById<TextView>(R.id.tvJamSelesai).text = "Jam Selesai: ${current.jam_selesai}"

        val drawableId = when (current.id_eskul) {
            1 -> R.drawable.futsal
            2 -> R.drawable.basket
            3 -> R.drawable.voli
            4 -> R.drawable.badminton
            5 -> R.drawable.musik
            6 -> R.drawable.catur
            else -> R.drawable.ic_default_eskul
        }
        itemView.findViewById<ImageView>(R.id.imgEskul).setImageResource(drawableId)

        return itemView
    }
}