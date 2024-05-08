package com.mariomorenoarroyo.tfg.view.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.adapter.SaborAdapter
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.data.adapter.SaborListener
import com.mariomorenoarroyo.tfg.view.activity.MainActivity
import org.json.JSONObject

class SaboresFragment : Fragment(), SaborListener {

    private lateinit var sabores: List<Sabor>
    private lateinit var saborAdapter: SaborAdapter
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sabores, container, false)

        val saborRecyclerView = view.findViewById<RecyclerView>(R.id.saborRecyclerView)
        saborRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        cargarDatosSiEsNecesario()


        sabores = obtenerSabores()
        saborAdapter = SaborAdapter(sabores, this, requireContext())
        saborRecyclerView.adapter = saborAdapter

        return view
    }

    private fun cargarDatosSiEsNecesario() {
        val database = FirebaseDatabase.getInstance()
        val saboresRef = database.getReference("sabores")

        saboresRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val listaDeSabores = obtenerSabores()
                    for (sabor in listaDeSabores) {
                        val saborId = saboresRef.push().key
                        saboresRef.child(saborId!!).setValue(sabor)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
               Toast.makeText(requireContext(), "Error al cargar los sabores", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaborClick(sabor: Sabor) {
        val saborDetails = SaborDetailFragment.newInstance(sabor)
        operativeActivity.replaceFragment(saborDetails)
    }

    private fun obtenerSabores(): List<Sabor> {
        val listaDeSabores= mutableListOf<Sabor>()

        val jsonObject= JSONObject(jsonString)
        val jsonArray=jsonObject.getJSONArray("sabores")

        for(i in 0 until jsonArray.length() ){
            val saborJson=jsonArray.getJSONObject(i)
            val nombreSabor=saborJson.getString("sabor")
            val descripcionSabor=saborJson.getString("descripcion")
            val imagenSabor = saborJson.getString("imagen")
            val precioSabor = saborJson.getString("precio")


            listaDeSabores.add(Sabor(nombreSabor,descripcionSabor,imagenSabor,precioSabor.toFloat()))
        }

        return listaDeSabores
    }
}












