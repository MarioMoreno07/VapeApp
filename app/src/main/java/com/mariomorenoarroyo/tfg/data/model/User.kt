package com.mariomorenoarroyo.tfg.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var nombre: String? = null,
    var telefono: String? = null,
    var fechaNacimiento: String? = null,
    var correo: String? = null
):Parcelable
