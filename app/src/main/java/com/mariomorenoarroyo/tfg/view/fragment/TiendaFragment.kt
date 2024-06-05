package com.mariomorenoarroyo.tfg.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.adapter.TiendaAdapter
import com.mariomorenoarroyo.tfg.data.adapter.TiendaListener
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.data.model.Tienda
import com.mariomorenoarroyo.tfg.view.activity.MainActivity
import org.json.JSONObject

class TiendaFragment : Fragment(), TiendaListener {
        private lateinit var tiendas: List<Tienda>
        private lateinit var tiendaAdapter: TiendaAdapter
        private lateinit var operativeActivity: MainActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            operativeActivity = context
        } else {
            throw IllegalStateException("La actividad debe ser MainActivity")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tienda, container, false)

      val tiendaRecyclerView = view.findViewById<RecyclerView>(R.id.tiendaRecyclerView)
        tiendaRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        cargarDatosSiEsNecesario()


        tiendas = obtenerTiendas()
        tiendaAdapter = TiendaAdapter(tiendas, this, requireContext())
        tiendaRecyclerView.adapter = tiendaAdapter

        return view
    }

    private fun obtenerTiendas(): List<Tienda> {
        val listaDeTiendas= mutableListOf<Tienda>()

        val jsonObject= JSONObject(jsonTiendaString)
        val jsonArray=jsonObject.getJSONArray("tiendas")

        for(i in 0 until jsonArray.length() ){
            val saborJson=jsonArray.getJSONObject(i)
            val nombreTienda=saborJson.getString("nombre")
            val imagenTienda = saborJson.getString("imagen")
            val ubicacionTienda = saborJson.getString("direccion")
            val horarioTienda = saborJson.getString("horario")
            val telefonoTienda= saborJson.getString("telefono")
            listaDeTiendas.add(Tienda(nombreTienda,imagenTienda,ubicacionTienda,horarioTienda,telefonoTienda))
        }

        return listaDeTiendas
    }

    private fun cargarDatosSiEsNecesario() {
        val database = FirebaseDatabase.getInstance()
        val tiendaRef = database.getReference("tiendas")

        tiendaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val listaDeTiendas = obtenerTiendas()
                    for (tienda in listaDeTiendas) {
                        val tiendaId = tiendaRef.push().key
                        tiendaRef.child(tiendaId!!).setValue(tienda)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar las tiendas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onTiendaClick(tienda: Tienda) {
        val tiendaDetails = TiendaDetailsFragment.newInstance(tienda)
        operativeActivity.replaceFragment(tiendaDetails)
    }
}