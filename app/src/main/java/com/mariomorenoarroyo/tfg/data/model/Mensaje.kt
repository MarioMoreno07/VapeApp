package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Mensaje(
    val contenido: String = "",
    val id: String = "",
    val duracion: String=""
): Parcelable, Serializable {
    constructor():this("")
}
