package com.mariomorenoarroyo.tfg.data.adapter

import com.mariomorenoarroyo.tfg.data.model.Cachimba
import com.mariomorenoarroyo.tfg.data.model.Vape

interface VapeListener {
    fun onVapeClick(vape: Vape)
    fun onCachimbaClick(cachimba:Cachimba)

    val jsonStringVape:String
        get() = """
            {
                "vapes": [
                    {
                        "nombreVape": "Vaper Supreme",
                        "descripcionVape": "La mejor calidad en vapes, perfecto para los amantes de los vapes y quieren lo mejor",
                        "imagen":"https://www.vitalcigar.es/img/productos/istick-pico-2-75w-gzeno-s-tank-2ml-eleaf.jpg",
                        "precioVape": 10.99
                        
                    },
                    {
                        "nombreVape": "Vaper Pro 4000",
                        "descripcionVape": "Un vape de alta calidad pefecto para los amantes de los vapes",
                        "imagen":"https://upload.vapeo24.com/producto/md/uwell-koko-gk2-586660.webp",
                        "precioVape": 12.99
                       
                    },
                    {
                        "nombreVape": "Vaper Puf",
                        "descripcionVape": "Vaper para los principiantes, perfecto para empezar a vapear",
                        "imagen":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS558ocS0MkmFmc0oerIvLcXfrKpCLYUxa6LdrtyPo-KQ&s",
                        "precioVape": 14.99
                        
                    }
                ]
            }            
        """.trimIndent()


    val jsonStringCachimbas:String
        get() = """
            {
                "cachimbas": [
                    {
                        "nombreCachimba": "Cachimba Ultime",
                        "descripcionCachimba": "Cachimba de alta calidad, perfecta para los amantes de las cachimbas",
                        "imagenCachimba":"https://cachimba-planet.com/4073-large_default/aladin-mvp-360-shiny-full-blue.jpg",
                        "precioCachimba": 100.99
                        
                    },
                    {
                        "nombreCachimba": "Cachimba night",
                        "descripcionCachimba": "Cachimba con un tono oscuro con dos entradas de aire",
                        "imagenCachimba":"https://m.media-amazon.com/images/I/61SzlMzlmNL.jpg",
                        "precioCachimba": 120.99
                       
                    },
                    {
                        "nombreCachimba": "Cachimba Golden",
                        "descripcionCachimba": "Cachimba de color dorado con un diseño elegante y moderno",
                        "imagenCachimba":"https://m.media-amazon.com/images/I/41+7uTy5DiL.jpg",
                        "precioCachimba": 140.99
                        
                    }
                ]
            }            
        """.trimIndent()


}