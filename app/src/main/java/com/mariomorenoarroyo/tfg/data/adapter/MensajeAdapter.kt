package com.mariomorenoarroyo.tfg.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mariomorenoarroyo.tfg.data.model.Mensaje
import com.mariomorenoarroyo.tfg.databinding.ViewMensajeBinding

class MensajeAdapter (
    var mensajes:List<Mensaje>
): RecyclerView.Adapter<MensajeAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewMensajeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mensaje: Mensaje) {
            with(binding) {
                mensajeText.text= mensaje.contenido

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewMensajeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.bind(mensaje)
    }

    override fun getItemCount(): Int = mensajes.size


}