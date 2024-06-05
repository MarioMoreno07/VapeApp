package com.mariomorenoarroyo.tfg.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.model.Cachimba
import com.mariomorenoarroyo.tfg.data.model.Vape

class cachimbasFavoriteAdapter(private val cachimbaList: List<Cachimba>) : RecyclerView.Adapter<cachimbasFavoriteAdapter.CachimbaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CachimbaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_vapes, parent, false)
        return CachimbaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CachimbaViewHolder, position: Int) {
        val cachimba = cachimbaList[position]
        holder.bind(cachimba)
    }

    override fun getItemCount(): Int = cachimbaList.size

    class CachimbaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vapeName: TextView = itemView.findViewById(R.id.titulo)
        private val vapeDescription: TextView = itemView.findViewById(R.id.descripcion)
        private val vapeImage: ImageView = itemView.findViewById(R.id.imagenVape)

        fun bind(cachimba: Cachimba) {
            vapeName.text = cachimba.nombreCachimba
            vapeDescription.text = cachimba.descripcionCachimba
            Glide.with(itemView)
                .load(cachimba.imagenCachimba)
                .into(vapeImage)
        }
    }
}
