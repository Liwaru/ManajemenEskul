package com.example.penjualanmobilkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class EskulAdapter(
    context: Context,
    private val dataList: ArrayList<Eskul>
) : ArrayAdapter<Eskul>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_eskul, parent, false)
        }

        val current = dataList[position]

        val tvNamaPembina = itemView!!.findViewById<TextView>(R.id.tvNamaPembina)
        val tvJamMulai = itemView.findViewById<TextView>(R.id.tvJamMulai)
        val tvJamSelesai = itemView.findViewById<TextView>(R.id.tvJamSelesai)
        val imgEskul = itemView.findViewById<ImageView>(R.id.imgEskul)
        val btnEdit = itemView.findViewById<Button>(R.id.btnEdit)

        tvNamaPembina.text = "Pembina: ${current.nama_pembina}"
        tvJamMulai.text = "Jam Mulai: ${current.jam_mulai}"
        tvJamSelesai.text = "Jam Selesai: ${current.jam_selesai}"

        // Pilih gambar berdasarkan id_eskul (1-6)
        val drawableId = when (current.id_eskul) {
            1 -> R.drawable.futsal
            2 -> R.drawable.basket
            3 -> R.drawable.voli
            4 -> R.drawable.badminton
            5 -> R.drawable.musik
            6 -> R.drawable.catur
            else -> R.drawable.ic_default_eskul
        }
        imgEskul.setImageResource(drawableId)

        btnEdit.setOnClickListener {
            Toast.makeText(context, "Edit eskul: ${current.nama_eskul}", Toast.LENGTH_SHORT).show()
            // Nanti arahkan ke EditActivity dengan membawa id_eskul
        }

        return itemView
    }
}