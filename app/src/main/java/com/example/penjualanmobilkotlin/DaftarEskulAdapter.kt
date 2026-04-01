package com.example.penjualanmobilkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.penjualanmobilkotlin.EskulItem
import com.example.penjualanmobilkotlin.R

class DaftarEskulAdapter(
    private val listEskul: List<EskulItem>,
    private val onDaftarClick: (EskulItem) -> Unit
) : RecyclerView.Adapter<DaftarEskulAdapter.EskulViewHolder>() {

    inner class EskulViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEskul: ImageView = itemView.findViewById(R.id.imgEskul)
        val tvNamaEskul: TextView = itemView.findViewById(R.id.tvNamaEskul)
        val tvJadwal: TextView = itemView.findViewById(R.id.tvJadwal)
        val btnDaftar: Button = itemView.findViewById(R.id.btnDaftar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EskulViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daftar_eskul, parent, false)
        return EskulViewHolder(view)
    }

    override fun getItemCount(): Int = listEskul.size

    override fun onBindViewHolder(holder: EskulViewHolder, position: Int) {
        val eskul = listEskul[position]

        holder.tvNamaEskul.text = eskul.namaEskul
        holder.tvJadwal.text = eskul.jadwal
        holder.imgEskul.setImageResource(eskul.gambarRes)

        holder.btnDaftar.setOnClickListener {
            onDaftarClick(eskul)
        }
    }
}