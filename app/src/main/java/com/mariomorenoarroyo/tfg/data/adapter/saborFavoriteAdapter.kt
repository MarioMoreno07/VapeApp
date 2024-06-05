package com.mariomorenoarroyo.tfg.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.model.Sabor


class saborFavoriteAdapter(private val saborList: List<Sabor>) : RecyclerView.Adapter<saborFavoriteAdapter.SaborViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaborViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_sabores, parent, false)
        return SaborViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaborViewHolder, position: Int) {
        val sabor = saborList[position]
        holder.bind(sabor)
    }

    override fun getItemCount(): Int = saborList.size

    class SaborViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val saborName: TextView = itemView.findViewById(R.id.titulo)
        private val saborDescription: TextView = itemView.findViewById(R.id.descripcion)
        private val saborImage: ImageView = itemView.findViewById(R.id.imagenSabor)

        fun bind(sabor: Sabor) {
            saborName.text = sabor.nombreSabor
            saborDescription.text = sabor.descripcionSabor
            Glide.with(itemView)
                .load(sabor.imagenSabor)
                .into(saborImage)
        }
    }
}
