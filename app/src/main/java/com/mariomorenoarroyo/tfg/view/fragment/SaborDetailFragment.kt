package com.mariomorenoarroyo.tfg.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
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
import com.mariomorenoarroyo.tfg.data.model.Mensaje
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.databinding.FragmentSaborDetailBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity

class SaborDetailFragment : Fragment() {
    private lateinit var binding: FragmentSaborDetailBinding
    private lateinit var sabor: Sabor
    private lateinit var mensajeAdapter: MensajeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var operativeActivity: MainActivity

    companion object {
        private const val ARG_SABOR = "sabor"
        fun newInstance(sabor: Sabor): SaborDetailFragment {
            val fragment = SaborDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_SABOR, sabor)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sabor = it.getSerializable(ARG_SABOR) as Sabor
            operativeActivity = activity as MainActivity
        }
        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("sabores/comentarios/${sabor.nombreSabor}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaborDetailBinding.inflate(inflater, container, false)
        setupSendButton()
        setupRecyclerView()
        loadMessages()

        val sabor = arguments?.getSerializable(ARG_SABOR) as? Sabor
        if (sabor != null) {
            mostarDetallesSabor(sabor)
        } else {
            Toast.makeText(context, "No se ha podido cargar el sabor", Toast.LENGTH_SHORT).show()
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.recyclerViewComments.visibility = View.GONE
        binding.textViewCommentsTitle.setOnClickListener {
            if (binding.recyclerViewComments.visibility == View.VISIBLE) {
                binding.recyclerViewComments.visibility = View.GONE
            } else {
                binding.recyclerViewComments.visibility = View.VISIBLE
            }
        }

        val favoritoButton = binding.favorito
        checkIfSaborIsFavorito(sabor, favoritoButton)
        favoritoButton.setOnClickListener {
            favoritoButton.isSelected = !favoritoButton.isSelected
            addSaborFavorito(sabor, favoritoButton.isSelected)
        }
        binding.ratingBarSabor.setOnRatingBarChangeListener { _, rating, _ ->
            guardarRating(rating)
        }

        return binding.root
    }

    private fun checkIfSaborIsFavorito(sabor: Sabor?, favoritoButton: ImageButton) {
        val user = auth.currentUser
        if (user != null && sabor != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val favoritesRef = database.child("users").child(email!!).child("Saboresfavoritos")

            favoritesRef.orderByChild("nombreSabor").equalTo(sabor.nombreSabor).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    favoritoButton.isSelected = dataSnapshot.exists()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Error al verificar si el sabor está en la lista de favoritos", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun addSaborFavorito(sabor: Sabor?, isFavorito: Boolean) {
        val user = auth.currentUser
        if (user != null && sabor != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val favoritesRef = database.child("users").child(email!!).child("Saboresfavoritos")

            if (isFavorito) {
                // Añadir el sabor a favoritos
                favoritesRef.push().setValue(sabor)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Sabor añadido a favoritos", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al añadir ${sabor.nombreSabor} a favoritos", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Eliminar el sabor de favoritos
                favoritesRef.orderByChild("nombreSabor").equalTo(sabor.nombreSabor).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            snapshot.ref.removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Sabor eliminado de favoritos", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error al eliminar ${sabor.nombreSabor} de favoritos", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(context, "Error al eliminar ${sabor.nombreSabor} de favoritos", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(context, "No hay usuario autenticado o el sabor es nulo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSendButton() {
        binding.buttonSendComment.setOnClickListener {
            val mensajeText = binding.editTextComment.text.toString().trim()
            if (mensajeText.isNotEmpty()) {
                // Obtener el ID del usuario actual
                val userID = auth.currentUser?.uid

                val mensajeID = databaseReference.push().key
                val nuevoMensaje = userID?.let { it1 -> Mensaje(mensajeText) }

                mensajeID?.let {
                    databaseReference.child(it).setValue(nuevoMensaje)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Mensaje enviado. Haz click en 'comentarios' para ver los comentarios " +
                                    "de otras personas", Toast.LENGTH_LONG).show()
                            // Limpiar el campo de texto después de enviar el mensaje
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

    private fun mostarDetallesSabor(sabor: Sabor) {
        binding.nombreDelSabor.text = ""
        binding.descripcionDelSabor.text = ""

        obtenerSaboresDeFirestore(sabor)
    }

    private fun obtenerSaboresDeFirestore(sabor: Sabor) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("sabores")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                for (snapshot in datasnapshot.children) {
                    val saborEnFirestore = snapshot.getValue(Sabor::class.java)
                    if (saborEnFirestore != null && saborEnFirestore == sabor) {
                        binding.nombreDelSabor.text = saborEnFirestore.nombreSabor
                        binding.descripcionDelSabor.text = saborEnFirestore.descripcionSabor
                        Glide.with(requireContext())
                            .load(saborEnFirestore.imagenSabor)
                            .centerCrop()
                            .into(binding.ivSaborDetail)
                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al obtener el sabor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarRating(rating: Float) {
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val ratingRef = database.child("ratings").child("sabores").child(email!!).child(sabor.nombreSabor)

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
