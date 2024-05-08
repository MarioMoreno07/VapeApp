package com.mariomorenoarroyo.tfg.data.adapter

import com.mariomorenoarroyo.tfg.data.model.Sabor

interface SaborListener {
    fun onSaborClick(sabor: Sabor)

    val jsonString:String
        get() = """
            {
                "sabores": [
                    {
                        "sabor": "Fresa",
                        "descripcion": "Un clásico sabor a fresa que no puede faltar.",
                         "imagen":"https://m.media-amazon.com/images/I/61KdmYHsCCL._AC_UF894,1000_QL80_.jpg",
                        "precio": 10.99
                       
                    },
                    {
                        "sabor": "Menta",
                        "descripcion": "Refrescante sabor a menta para una sensación fresca en cada inhalación.",
                         "imagen":"https://m.media-amazon.com/images/I/61iEIIhtP0S._AC_UF894,1000_QL80_.jpg",
                        "precio": 12.99
                       
                    },
                    {
                        "sabor": "Café",
                        "descripcion": "Sabor a café para los amantes de esta bebida.",
                        "imagen":"https://tuvapeo.com/wp-content/uploads/2024/03/dainty-s-premium-coffee-80ml.jpg",
                        "precio": 14.99
                        
                    },
                    {
                        "sabor": "Caramelo",
                        "descripcion": "Sabor a caramelo para los más golosos.",
                        "imagen":"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQaR12JYqTcyX3Iyg5anpBevEiyLKMkYAEV28Bdd33DRw&s",
                        "precio": 18.99
                        
                    }
                ]
            }            
        """.trimIndent()
}