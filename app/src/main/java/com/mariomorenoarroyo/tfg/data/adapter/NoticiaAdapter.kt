package com.mariomorenoarroyo.tfg.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mariomorenoarroyo.tfg.data.model.Noticia
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.databinding.ViewNoticiasBinding
import com.mariomorenoarroyo.tfg.databinding.ViewSaboresBinding
import com.mariomorenoarroyo.tfg.databinding.ViewVapesBinding

class NoticiaAdapter(
    private val noticias: List<Noticia>,
    private val listener: NoticiaListener,
    private val context: Context
    ): RecyclerView.Adapter<NoticiaAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ViewNoticiasBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(noticia: Noticia) {
            with(binding) {
               tituloNoticia.text = noticia.titulo
                contenidoNoticia.text = noticia.contenido
                Glide.with(root)
                    .load(noticia.imagen)
                    .centerCrop()
                    .into(imagenNoticia)
                root.setOnClickListener {
                    listener.onNoticiaClick(noticia)
                }
            }
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val noticia = noticias[position]
                listener.onNoticiaClick(noticia)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding= ViewNoticiasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticiaAdapter.ViewHolder, position: Int) {
        val noticia = noticias[position]
        holder.bind(noticia)
    }

    override fun getItemCount() = noticias.size
}