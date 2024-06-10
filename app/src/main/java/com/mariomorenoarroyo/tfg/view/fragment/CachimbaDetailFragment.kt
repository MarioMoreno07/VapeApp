package com.mariomorenoarroyo.tfg.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.adapter.MensajeAdapter
import com.mariomorenoarroyo.tfg.data.model.Cachimba
import com.mariomorenoarroyo.tfg.data.model.Mensaje
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.databinding.FragmentVapeDetailBinding

class CachimbaDetailFragment : Fragment() {
    private lateinit var binding: FragmentVapeDetailBinding
    private lateinit var cachimba: Cachimba
    private lateinit var mensajeAdapter: MensajeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    companion object {
        private const val ARG_CACHIMBA = "cachimba"
        fun newInstance(cachimba: Cachimba): CachimbaDetailFragment {
            val fragment = CachimbaDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_CACHIMBA, cachimba)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cachimba = it.getSerializable(ARG_CACHIMBA) as Cachimba
        }
        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("cachimbas/comentarios/${cachimba.nombreCachimba}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVapeDetailBinding.inflate(inflater, container, false)
        setupSendButton()
        setupRecyclerView()
        loadMessages()

        val cachimba = arguments?.getSerializable(ARG_CACHIMBA) as? Cachimba
        if (cachimba != null) {
            mostarDetallesVape(cachimba)
        } else {
            Toast.makeText(context, "No se ha podido cargar la cachimba", Toast.LENGTH_SHORT).show()
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val favoritoButton = binding.favorito as ImageButton
        checkIfCachimbaIsFavorito(cachimba, favoritoButton)
        favoritoButton.setOnClickListener {
            favoritoButton.isSelected = !favoritoButton.isSelected
            addCachimbaFavorito(cachimba, favoritoButton.isSelected)
        }

        binding.recyclerViewComments.visibility = View.GONE
        binding.textViewCommentsTitle.setOnClickListener {
            if (binding.recyclerViewComments.visibility == View.VISIBLE) {
                binding.recyclerViewComments.visibility = View.GONE
            } else {
                binding.recyclerViewComments.visibility = View.VISIBLE
            }
        }

        binding.ratingBarVape.setOnRatingBarChangeListener { _, rating, _ ->
            guardarRating(rating)
        }

        return binding.root
    }

    private fun checkIfCachimbaIsFavorito(cachimba: Cachimba?, favoritoButton: ImageButton) {
        val user = auth.currentUser
        if (user != null && cachimba != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val favoritesRef = database.child("users").child(email!!).child("cachimbasfavoritos")

            favoritesRef.orderByChild("nombreCachimba").equalTo(cachimba.nombreCachimba).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    favoritoButton.isSelected = dataSnapshot.exists()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Error al verificar si la cachimba está en la lista de favoritos", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun addCachimbaFavorito(cachimba: Cachimba?, isFavorito: Boolean) {
        val user = auth.currentUser
        if (user != null && cachimba != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val favoritesRef = database.child("users").child(email!!).child("cachimbasfavoritos")

            if (isFavorito) {
                // Añadir la cachimba a favoritos
                favoritesRef.push().setValue(cachimba)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Cachimba añadida a favoritos", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al añadir ${cachimba.nombreCachimba} a favoritos", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Eliminar la cachimba de favoritos
                favoritesRef.orderByChild("nombreCachimba").equalTo(cachimba.nombreCachimba).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            snapshot.ref.removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Cachimba eliminada de favoritos", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error al eliminar ${cachimba.nombreCachimba} de favoritos", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(context, "Error al eliminar ${cachimba.nombreCachimba} de favoritos", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(context, "No hay usuario autenticado o cachimba es nula", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSendButton() {
        binding.buttonSendComment.setOnClickListener {
            val mensajeText = binding.editTextComment.text.toString().trim()
            if (mensajeText.isNotEmpty()) {
                val userID = auth.currentUser?.uid
                val mensajeID = databaseReference.push().key
                val nuevoMensaje = userID?.let { Mensaje(mensajeText) }

                mensajeID?.let {
                    databaseReference.child(it).setValue(nuevoMensaje)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Mensaje enviado. Haz click en 'comentarios' para ver los comentarios " +
                                    "de otras personas", Toast.LENGTH_LONG).show()
                            binding.editTextComment.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(context, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        mensajeAdapter = MensajeAdapter(emptyList())
        binding.recyclerViewComments.apply {
            adapter = mensajeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadMessages() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mensajes = mutableListOf<Mensaje>()
                for (snapshot in dataSnapshot.children) {
                    val mensaje = snapshot.getValue(Mensaje::class.java)
                    mensaje?.let {
                        mensajes.add(it)
                    }
                }
                mensajeAdapter.mensajes = mensajes
                mensajeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error al cargar los mensajes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostarDetallesVape(cachimba: Cachimba) {
        binding.nombreDelVape.text = ""
        binding.descripcionDelVape.text = ""

        obtenerVapeDeFirestore(cachimba)
    }

    private fun obtenerVapeDeFirestore(cachimba: Cachimba) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("cachimbas")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val vapeEnFirebase = snapshot.getValue(Cachimba::class.java)
                    if (vapeEnFirebase != null && vapeEnFirebase == cachimba) {
                        // Se encontró la cachimba correspondiente, mostrar los detalles
                        binding.nombreDelVape.text = vapeEnFirebase.nombreCachimba
                        binding.descripcionDelVape.text = vapeEnFirebase.descripcionCachimba
                        // Cargar la imagen de la cachimba
                        Glide.with(requireContext())
                            .load(vapeEnFirebase.imagenCachimba)
                            .centerCrop()
                            .into(binding.ivVapeDetail)
                        return
                    }
                }
                // No se encontró la cachimba correspondiente
                Toast.makeText(context, "No se encontraron detalles para esta cachimba", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error al obtener los detalles de la cachimba", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarRating(rating: Float) {
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val ratingRef = database.child("ratings").child("cachimbas").child(email!!)
                .child(cachimba.nombreCachimba)

            ratingRef.setValue(rating).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Rating guardado: $rating estrellas", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error al guardar el rating", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}

