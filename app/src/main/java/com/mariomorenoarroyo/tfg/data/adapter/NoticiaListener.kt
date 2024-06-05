package com.mariomorenoarroyo.tfg.data.adapter

import com.mariomorenoarroyo.tfg.data.model.Noticia

interface NoticiaListener {
fun onNoticiaClick(noticia: Noticia)
}