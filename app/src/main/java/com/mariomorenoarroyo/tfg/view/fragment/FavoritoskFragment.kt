package com.mariomorenoarroyo.tfg.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.adapter.VapeFavoriteAdapter
import com.mariomorenoarroyo.tfg.data.adapter.cachimbasFavoriteAdapter
import com.mariomorenoarroyo.tfg.data.adapter.saborFavoriteAdapter
import com.mariomorenoarroyo.tfg.data.adapter.tiendasFavoriteAdapter
import com.mariomorenoarroyo.tfg.data.model.Cachimba
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.data.model.Tienda
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.databinding.FragmentFavoritoskBinding

class FavoritoskFragment : Fragment() {
    private lateinit var binding: FragmentFavoritoskBinding
    private lateinit var vapeFavoriteAdapter: VapeFavoriteAdapter
    private lateinit var saborFavoriteAdapter: saborFavoriteAdapter
    private lateinit var cachimbaFavoriteAdapter: cachimbasFavoriteAdapter
    private lateinit var tiendaFavoriteAdapter: tiendasFavoriteAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var vapesFavoritos: MutableList<Vape>
    private lateinit var saboresFavoritos:MutableList<Sabor>
    private lateinit var tiendasFavoritas:MutableList<Tienda>
    private lateinit var cachimbasFavoritas:MutableList<Cachimba>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoritoskBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding.recyclerViewVapesFavorites.visibility = View.GONE
        binding.recyclerViewCachimbasFavorites.visibility = View.GONE
        binding.recyclerViewSaboresFavorites.visibility = View.GONE
        binding.recyclerViewTiendasFavorites.visibility = View.GONE


        vapesFavoritos = mutableListOf()
        vapeFavoriteAdapter = VapeFavoriteAdapter(vapesFavoritos)
        binding.recyclerViewVapesFavorites.adapter = vapeFavoriteAdapter
        binding.recyclerViewVapesFavorites.layoutManager = LinearLayoutManager(context)

        binding.vapes.setOnClickListener{
            loadVapesFavorites()
            binding.recyclerViewVapesFavorites.visibility = View.VISIBLE
            binding.recyclerViewCachimbasFavorites.visibility = View.GONE
            binding.recyclerViewSaboresFavorites.visibility = View.GONE
            binding.recyclerViewTiendasFavorites.visibility = View.GONE
        }

        saboresFavoritos = mutableListOf()
        saborFavoriteAdapter = saborFavoriteAdapter(saboresFavoritos)
        binding.recyclerViewSaboresFavorites.adapter = saborFavoriteAdapter
        binding.recyclerViewSaboresFavorites.layoutManager = LinearLayoutManager(context)


        binding.sabores.setOnClickListener{
            loadSaboresFavorites()
            binding.recyclerViewVapesFavorites.visibility = View.GONE
            binding.recyclerViewCachimbasFavorites.visibility = View.GONE
            binding.recyclerViewSaboresFavorites.visibility = View.VISIBLE
            binding.recyclerViewTiendasFavorites.visibility = View.GONE
        }

        cachimbasFavoritas = mutableListOf()
        cachimbaFavoriteAdapter = cachimbasFavoriteAdapter(cachimbasFavoritas)
        binding.recyclerViewCachimbasFavorites.adapter = cachimbaFavoriteAdapter
        binding.recyclerViewCachimbasFavorites.layoutManager = LinearLayoutManager(context)

        binding.cachimbas.setOnClickListener{
            loadCachimbasFavorites()
            binding.recyclerViewVapesFavorites.visibility = View.GONE
            binding.recyclerViewCachimbasFavorites.visibility = View.VISIBLE
            binding.recyclerViewSaboresFavorites.visibility = View.GONE
            binding.recyclerViewTiendasFavorites.visibility = View.GONE
        }

        tiendasFavoritas = mutableListOf()
        tiendaFavoriteAdapter = tiendasFavoriteAdapter(tiendasFavoritas)
        binding.recyclerViewTiendasFavorites.adapter = tiendaFavoriteAdapter
        binding.recyclerViewTiendasFavorites.layoutManager = LinearLayoutManager(context)

        binding.tiendas.setOnClickListener{
            loadTiendasFavorites()
            binding.recyclerViewVapesFavorites.visibility = View.GONE
            binding.recyclerViewCachimbasFavorites.visibility = View.GONE
            binding.recyclerViewSaboresFavorites.visibility = View.GONE
            binding.recyclerViewTiendasFavorites.visibility = View.VISIBLE
        }


        return binding.root
    }

    private fun loadTiendasFavorites() {
        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", ",")
            email?.let {
                database.child("users").child(email).child("tiendasfavoritos")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            tiendasFavoritas.clear()
                            for (dataSnapshot in snapshot.children) {
                                val sabor = dataSnapshot.getValue(Tienda::class.java)
                                sabor?.let { tiendasFavoritas.add(it) }
                            }
                            tiendaFavoriteAdapter.notifyDataSetChanged()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Error al cargar los favoritos", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    private fun loadCachimbasFavorites() {
        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", ",")
            email?.let {
                database.child("users").child(email).child("cachimbasfavoritos")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            cachimbasFavoritas.clear()
                            for (dataSnapshot in snapshot.children) {
                                val sabor = dataSnapshot.getValue(Cachimba::class.java)
                                sabor?.let { cachimbasFavoritas.add(it) }
                            }
                            cachimbaFavoriteAdapter.notifyDataSetChanged()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Error al cargar los favoritos", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }

    private fun loadSaboresFavorites() {
        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", ",")
            email?.let {
                database.child("users").child(email).child("Saboresfavoritos")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            saboresFavoritos.clear()
                            for (dataSnapshot in snapshot.children) {
                                val sabor = dataSnapshot.getValue(Sabor::class.java)
                                sabor?.let { saboresFavoritos.add(it) }
                            }
                            saborFavoriteAdapter.notifyDataSetChanged()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Error al cargar los favoritos", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }


    private fun loadVapesFavorites() {
        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", ",")
            email?.let {
                database.child("users").child(email).child("Vapesfavoritos")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            vapesFavoritos.clear()
                            for (dataSnapshot in snapshot.children) {
                                val vape = dataSnapshot.getValue(Vape::class.java)
                                vape?.let { vapesFavoritos.add(it) }
                            }
                            vapeFavoriteAdapter.notifyDataSetChanged()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Error al cargar los favoritos", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }
    }
}