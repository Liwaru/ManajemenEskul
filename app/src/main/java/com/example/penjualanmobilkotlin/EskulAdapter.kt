package com.example.penjualanmobilkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.penjualanmobilkotlin.Eskul
import com.example.penjualanmobilkotlin.R

class EskulAdapter(
    private val eskulList: List<Eskul>,
    private val sessionIdPembina: Int,
    private val onEditClick: (Eskul) -> Unit
) : RecyclerView.Adapter<EskulAdapter.EskulViewHolder>() {

    inner class EskulViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEskul: ImageView = itemView.findViewById(R.id.imgEskul)
        val tvNamaEskul: TextView = itemView.findViewById(R.id.tvNamaEskul)
        val tvNamaPembina: TextView = itemView.findViewById(R.id.tvNamaPembina)
        val tvJamMulai: TextView = itemView.findViewById(R.id.tvJamMulai)
        val tvJamSelesai: TextView = itemView.findViewById(R.id.tvJamSelesai)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EskulViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_eskul, parent, false)
        return EskulViewHolder(view)
    }

    override fun getItemCount(): Int = eskulList.size

    override fun onBindViewHolder(holder: EskulViewHolder, position: Int) {
        val eskul = eskulList[position]

        holder.tvNamaEskul.text = eskul.namaEskul
        holder.tvNamaPembina.text = "Pembina: ${eskul.namaPembina}"
        holder.tvJamMulai.text = "Jam Mulai: ${eskul.jamMulai}"
        holder.tvJamSelesai.text = "Jam Selesai: ${eskul.jamSelesai}"

        // Ganti Glide dengan Coil
        holder.imgEskul.load(eskul.gambar) {
            placeholder(R.drawable.ic_default_eskul)
            error(R.drawable.ic_default_eskul)
        }

        if (eskul.idPembina == sessionIdPembina) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnEdit.setOnClickListener {
                onEditClick(eskul)
            }
        } else {
            holder.btnEdit.visibility = View.GONE
        }
    }
}