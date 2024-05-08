package com.mariomorenoarroyo.tfg.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.adapter.VapeListener
import com.mariomorenoarroyo.tfg.data.adapter.VaperAdapter
import com.mariomorenoarroyo.tfg.data.adapter.VaperCachimba
import com.mariomorenoarroyo.tfg.data.model.Cachimba
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.view.activity.MainActivity
import org.json.JSONObject

class VapeFragment : Fragment(), VapeListener {

    private lateinit var vapes: List<Vape>
    private lateinit var vapeAdapter: VaperAdapter
    private lateinit var cachimbaAdapter:VaperCachimba
    private lateinit var operativeActivity: MainActivity
    private lateinit var cachimbas: List<Cachimba>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            operativeActivity = context
        } else {
            throw IllegalStateException("La actividad debe ser MainActivity")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vape, container, false)

        val vapeRecyclerView = view.findViewById<RecyclerView>(R.id.vapeRecyclerView)
        val cachimbasRecyclerView = view.findViewById<RecyclerView>(R.id.cachimbasRecyclerView)

        vapeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cachimbasRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        cachimbasRecyclerView.visibility = View.GONE

        val vapeText = view.findViewById<TextView>(R.id.vapes)
        vapeText.setOnClickListener {
            vapeRecyclerView.visibility = View.VISIBLE
            cachimbasRecyclerView.visibility = View.GONE
            cargarDatosSiEsNecesario()
        }

        val cachimbaText = view.findViewById<TextView>(R.id.cachimbas)
        cachimbaText.setOnClickListener {
            cachimbasRecyclerView.visibility = View.VISIBLE
            vapeRecyclerView.visibility = View.GONE
            cargarDatosDeCachimbaSiEsNecesario()
        }

        vapes = obtenerVapes()
        vapeAdapter = VaperAdapter(vapes, this, requireContext())
        vapeRecyclerView.adapter = vapeAdapter

        cachimbas = obtenerCachimbas()
        cachimbaAdapter = VaperCachimba(cachimbas, this, requireContext())
        cachimbasRecyclerView.adapter = cachimbaAdapter

        return view
    }

    private fun cargarDatosDeCachimbaSiEsNecesario() {
        val database = FirebaseDatabase.getInstance()
        val cachimbaRef = database.getReference("cachimbas")

        cachimbaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val listaDeCachimbas = obtenerCachimbas()
                    for (cachimba in listaDeCachimbas) {
                        val CachimbaId = cachimbaRef.push().key
                        CachimbaId?.let {
                            cachimbaRef.child(it).setValue(cachimba)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun obtenerCachimbas(): List<Cachimba> {
        val listaDeCachimbas = mutableListOf<Cachimba>()

        val jsonObject = JSONObject(jsonStringCachimbas)
        val jsonArray = jsonObject.getJSONArray("cachimbas")

        for (i in 0 until jsonArray.length()) {
            val vapeJson = jsonArray.getJSONObject(i)
            val nombreCachimba = vapeJson.getString("nombreCachimba")
            val descripcionCachimba = vapeJson.getString("descripcionCachimba")
            val imagenCachimba = vapeJson.getString("imagenCachimba")
            val precioCachimba = vapeJson.getString("precioCachimba")

            listaDeCachimbas.add(Cachimba(nombreCachimba, descripcionCachimba, imagenCachimba, precioCachimba.toFloat()))
        }
        return listaDeCachimbas
    }

    private fun cargarDatosSiEsNecesario() {
        val database = FirebaseDatabase.getInstance()
        val vapesRef = database.getReference("vapes")

        vapesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    val listaDeVapes = obtenerVapes()
                    for (vape in listaDeVapes) {
                        val vapeId = vapesRef.push().key
                        vapeId?.let {
                            vapesRef.child(it).setValue(vape)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onVapeClick(vape: Vape) {
        val vapeDetails = VapeDetailFragment.newInstance(vape)
        operativeActivity.replaceFragment(vapeDetails)
    }

    override fun onCachimbaClick(cachimba: Cachimba) {
        val cachimbaDetails = CachimbaDetailFragment.newInstance(cachimba)
        operativeActivity.replaceFragment(cachimbaDetails)
    }

    private fun obtenerVapes(): List<Vape> {
        val listaDeVapes = mutableListOf<Vape>()

        val jsonObject = JSONObject(jsonStringVape)
        val jsonArray = jsonObject.getJSONArray("vapes")

        for (i in 0 until jsonArray.length()) {
            val vapeJson = jsonArray.getJSONObject(i)
            val nombreVape = vapeJson.getString("nombreVape")
            val descripcionVape = vapeJson.getString("descripcionVape")
            val imagenVape = vapeJson.getString("imagen")
            val precioVape = vapeJson.getString("precioVape")

            listaDeVapes.add(Vape(nombreVape, descripcionVape, imagenVape, precioVape.toFloat()))
        }

        return listaDeVapes
    }
}










