package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Cachimba (
    val nombreCachimba: String = "",
    val descripcionCachimba: String = "",
    val imagenCachimba: String = "",
    val precioCachimba: Float = 0.0f,
):Parcelable, Serializable
