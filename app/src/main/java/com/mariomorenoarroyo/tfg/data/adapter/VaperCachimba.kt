package com.mariomorenoarroyo.tfg.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.data.model.Cachimba

import com.mariomorenoarroyo.tfg.databinding.ViewVapesBinding

class VaperCachimba(
    private val cachimbas: List<Cachimba>,
    private val listener: VapeListener,
    private val context: Context
    ): RecyclerView.Adapter<VaperCachimba.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewVapesBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(cachimbas: Cachimba) {
            with(binding) {
                titulo.text = cachimbas.nombreCachimba
                precio.text = cachimbas.precioCachimba.toString() +"€"
                descripcion.text = cachimbas.descripcionCachimba
                Glide.with(root)
                    .load(cachimbas.imagenCachimba)
                    .centerCrop()
                    .into(imagenVape)

                root.setOnClickListener {
                    listener.onCachimbaClick(cachimbas)
                }
            }
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val cachimba = cachimbas[position]
                listener.onCachimbaClick(cachimba)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ViewVapesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VaperCachimba.ViewHolder, position: Int) {
        val sabor = cachimbas[position]
        holder.bind(sabor)
    }

    override fun getItemCount() = cachimbas.size
}