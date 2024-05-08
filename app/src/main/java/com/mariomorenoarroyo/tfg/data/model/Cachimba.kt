package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Cachimba (
    val nombreCachimba: String,
    val descripcionCachimba: String,
    val imagenCachimba: String,
    val precioCachimba: Float,
):Parcelable, Serializable {
    constructor():this("","","",0.0f)
}
