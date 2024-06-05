package com.mariomorenoarroyo.tfg.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.model.Vape

class VapeFavoriteAdapter(private val vapeList: List<Vape>) : RecyclerView.Adapter<VapeFavoriteAdapter.VapeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VapeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_vapes, parent, false)
        return VapeViewHolder(view)
    }

    override fun onBindViewHolder(holder: VapeViewHolder, position: Int) {
        val vape = vapeList[position]
        holder.bind(vape)
    }

    override fun getItemCount(): Int = vapeList.size

    class VapeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val vapeName: TextView = itemView.findViewById(R.id.titulo)
        private val vapeDescription: TextView = itemView.findViewById(R.id.descripcion)
        private val vapeImage: ImageView = itemView.findViewById(R.id.imagenVape)

        fun bind(vape: Vape) {
            vapeName.text = vape.nombreVape
            vapeDescription.text = vape.descripcionVape
            Glide.with(itemView)
                .load(vape.imagenVape)
                .into(vapeImage)
        }
    }
}
