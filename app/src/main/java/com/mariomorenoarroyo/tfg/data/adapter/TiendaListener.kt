package com.mariomorenoarroyo.tfg.data.adapter

import com.mariomorenoarroyo.tfg.data.model.Tienda

interface TiendaListener {
    fun onTiendaClick(tienda: Tienda)

    val jsonTiendaString:String
        get()="""
            {
                "tiendas": [
                    {
                        "nombre": "Sin Humo",
                        "imagen": "https://www.sinhumo.net/img/st/3.jpg",
                        "direccion": "Calle de la Princesa, 5, 28008 Madrid",
                        "horario": "Lunes a Viernes de 10:00 a 14:00 y de 17:00 a 20:00",
                        "telefono": "910 00 00 00"
                    },
                    {
                        "nombre": "VapeoStore",
                        "imagen": "https://s3.abcstatics.com/lavozdigital/www/multimedia/provincia/2023/06/15/vaper-RkrgqRmyKdeEdsykpbXlOVM-1200x840@abc.jpg",
                        "direccion": "Calle de la Princesa, 5, 28008 Madrid",
                        "horario": "Lunes a Viernes de 10:00 a 14:00 y de 17:00 a 20:00",
                        "telefono": "910 00 00 00"
                    },
                    {
                        "nombre": "VapeoLand",
                        "imagen": "https://www.bazardelvapeo.com/img/cms/tiendas/vape-shop-madrid-plenilunio-bazar-del-vapeo.jpeg",
                        "direccion": "Calle de la Princesa, 5, 28008 Madrid",
                        "horario": "Lunes a Viernes de 10:00 a 14:00 y de 17:00 a 20:00",
                        "telefono": "910 00 00 00"
                    },
                    {
                        "nombre": "VapeoCity",
                        "imagen": "https://lavapotienda.com/img/cms/sant-cugat.jpg",
                        "direccion": "Calle de la Princesa, 5, 28008 Madrid",
                        "horario": "Lunes a Viernes de 10:00 a 14:00 y de 17:00 a 20:00",
                        "telefono": "910 00 00 00"
                    }
                ]
            }

        """.trimIndent()
}