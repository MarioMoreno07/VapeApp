package com.mariomorenoarroyo.tfg.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.data.model.Tienda
import com.mariomorenoarroyo.tfg.databinding.ViewTiendaBinding

class TiendaAdapter(
    private val tiendas: List<Tienda>,
    private val listener: TiendaListener,
    private val context: Context
    ): RecyclerView.Adapter<TiendaAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewTiendaBinding) :
        RecyclerView.ViewHolder(binding.root),View.OnClickListener {

        fun bind(tienda: Tienda) {
            with(binding) {
                nombreTienda.text = tienda.nombreTienda
                ubicacionTienda.text = tienda.direccionTienda
                Glide.with(root)
                    .load(tienda.imagenTienda)
                    .centerCrop()
                    .into(imagenTienda)

                root.setOnClickListener {
                    listener.onTiendaClick(tienda)
                }
            }

        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val tienda = tiendas[position]
                listener.onTiendaClick(tienda)
        }
    }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiendaAdapter.ViewHolder {
       val binding= ViewTiendaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TiendaAdapter.ViewHolder, position: Int) {
        val tienda = tiendas[position]
        holder.bind(tienda)
    }
    override fun getItemCount() = tiendas.size
}


