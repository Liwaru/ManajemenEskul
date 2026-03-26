package com.example.penjualanmobilkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.penjualanmobilkotlin.EskulData

class EskulAdapter(
    private val list: List<Eskul>,
    private val onClick: (Eskul) -> Unit
) : RecyclerView.Adapter<EskulAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgEskul: ImageView = view.findViewById(R.id.imgEskul)
        val txtNama: TextView = view.findViewById(R.id.txtNama)
        val txtJadwal: TextView = view.findViewById(R.id.txtJadwal)
        val btnDaftar: Button = view.findViewById(R.id.btnDaftar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_eskul, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eskul = list[position]

        // ✅ SET DATA (INI YANG HILANG TADI)
        holder.txtNama.text = eskul.nama
        holder.txtJadwal.text = eskul.jadwal
        holder.imgEskul.setImageResource(eskul.gambar)

        // ✅ KLIK DAFTAR
        holder.btnDaftar.setOnClickListener {
            onClick(eskul)
        }
    }
}