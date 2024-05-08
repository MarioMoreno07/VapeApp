package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Sabor (
    val nombreSabor: String,
    val descripcionSabor: String,
    val imagenSabor: String,
    val precioSabor: Float,
):Parcelable, Serializable {
    constructor():this("","","",0.0f)
}
