package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Vape (
    val nombreVape: String,
    val descripcionVape: String,
    val imagenVape: String,
    val precioVape: Float,
):Parcelable, Serializable {
    constructor():this("","","",0.0f)
}
