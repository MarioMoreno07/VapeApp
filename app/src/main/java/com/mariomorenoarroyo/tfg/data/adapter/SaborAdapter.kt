package com.mariomorenoarroyo.tfg.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.databinding.ViewSaboresBinding

class SaborAdapter(
    private val sabores: List<Sabor>,
    private val listener: SaborListener,
    private val context: Context
    ): RecyclerView.Adapter<SaborAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewSaboresBinding) :
        RecyclerView.ViewHolder(binding.root),View.OnClickListener {

        fun bind(sabor: Sabor) {
            with(binding) {
                titulo.text = sabor.nombreSabor
                precio.text = sabor.precioSabor.toString() + "€"
                descripcion.text = sabor.descripcionSabor
                Glide.with(root)
                    .load(sabor.imagenSabor)
                    .centerCrop()
                    .into(imagenSabor)

                root.setOnClickListener {
                    listener.onSaborClick(sabor)
                }
            }

        }


        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val sabor = sabores[position]
                listener.onSaborClick(sabor)
        }
    }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ViewSaboresBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaborAdapter.ViewHolder, position: Int) {
        val sabor = sabores[position]
        holder.bind(sabor)
    }

    override fun getItemCount() = sabores.size
}