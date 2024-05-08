package com.mariomorenoarroyo.tfg.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.databinding.ViewSaboresBinding
import com.mariomorenoarroyo.tfg.databinding.ViewVapesBinding

class VaperAdapter(
    private val vapers: List<Vape>,
    private val listener: VapeListener,
    private val context: Context
    ): RecyclerView.Adapter<VaperAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewVapesBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(vaper: Vape) {
            with(binding) {
                titulo.text = vaper.nombreVape
                precio.text = vaper.precioVape.toString() + "€"
                descripcion.text = vaper.descripcionVape
                Glide.with(root)
                    .load(vaper.imagenVape)
                    .centerCrop()
                    .into(imagenVape)

                root.setOnClickListener {
                    listener.onVapeClick(vaper)
                }
            }
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val vaper = vapers[position]
                listener.onVapeClick(vaper)
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

    override fun onBindViewHolder(holder: VaperAdapter.ViewHolder, position: Int) {
        val sabor = vapers[position]
        holder.bind(sabor)
    }

    override fun getItemCount() = vapers.size
}