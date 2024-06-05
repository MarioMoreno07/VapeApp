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
import com.mariomorenoarroyo.tfg.data.model.Tienda
import com.mariomorenoarroyo.tfg.data.model.Vape

class tiendasFavoriteAdapter(private val tiendasList: List<Tienda>) : RecyclerView.Adapter<tiendasFavoriteAdapter.TiendaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiendaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_tienda, parent, false)
        return TiendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TiendaViewHolder, position: Int) {
        val tienda = tiendasList[position]
        holder.bind(tienda)
    }

    override fun getItemCount(): Int = tiendasList.size

    class TiendaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tiendaName: TextView = itemView.findViewById(R.id.nombreTienda)
        private val tiendaImage: ImageView = itemView.findViewById(R.id.imagenTienda)
        private val tiendaDireccion: TextView = itemView.findViewById(R.id.ubicacionTienda)

        fun bind(tienda: Tienda) {
            tiendaName.text = tienda.nombreTienda
            tiendaDireccion.text = tienda.direccionTienda
            Glide.with(itemView)
                .load(tienda.imagenTienda)
                .into(tiendaImage)
        }
    }
}
