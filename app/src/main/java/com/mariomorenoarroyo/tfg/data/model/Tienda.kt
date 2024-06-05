package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Tienda(
    val nombreTienda: String = "",
    val imagenTienda: String = "",
    val direccionTienda: String ="",
    val horarioTienda: String = "",
    val telefonoTienda: String = "",
):Parcelable, Serializable
