package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Noticia (
    var titulo: String = "",
    var contenido: String = "",
    var imagen: String = "",
    val userId: String = "",
    val noticiaId: String = "",
):Parcelable,Serializable