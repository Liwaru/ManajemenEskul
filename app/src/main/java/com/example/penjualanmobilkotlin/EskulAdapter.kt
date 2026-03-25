package com.example.penjualanmobilkotlin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.penjualanmobilkotlin.EskulData

class EskulAdapter(
    private val listEskul: List<Eskul>
) : RecyclerView.Adapter<EskulAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    override fun getItemCount(): Int = listEskul.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eskul = listEskul[position]

        holder.imgEskul.setImageResource(eskul.gambar)
        holder.txtNama.text = eskul.nama
        holder.txtJadwal.text = eskul.jadwal

        // cek apakah sudah daftar
        if (EskulData.eskulDipilih.contains(eskul)) {
            holder.btnDaftar.text = "Sudah Terdaftar"
            holder.btnDaftar.isEnabled = false
        } else {
            holder.btnDaftar.text = "Daftar"
            holder.btnDaftar.isEnabled = true
        }

        holder.btnDaftar.setOnClickListener {

            val context = holder.itemView.context
            val session = SessionManager(context)
            val idUser = session.getIdUser()

            val url = "http://192.168.1.8/manajemeneskul/daftar_eskul.php"

            val request = object : com.android.volley.toolbox.StringRequest(
                Method.POST, url,
                { response ->
                    val json = org.json.JSONObject(response)

                    Toast.makeText(
                        context,
                        json.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()

                    if (json.getBoolean("success")) {
                        EskulData.eskulDipilih.add(eskul)
                        notifyDataSetChanged() // 🔥 fix
                    }
                },
                {
                    Toast.makeText(context, "Gagal koneksi", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "id_user" to idUser!!,
                        "id_eskul" to eskul.id.toString() // 🔥 fix
                    )
                }
            }

            com.android.volley.toolbox.Volley.newRequestQueue(context).add(request)
        }
    }
}