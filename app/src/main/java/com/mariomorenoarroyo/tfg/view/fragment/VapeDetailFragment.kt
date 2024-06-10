package com.mariomorenoarroyo.tfg.view.fragment

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
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mariomorenoarroyo.tfg.data.adapter.MensajeAdapter
import com.mariomorenoarroyo.tfg.data.model.Mensaje
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.databinding.FragmentVapeDetailBinding

class VapeDetailFragment : Fragment() {
    private lateinit var binding: FragmentVapeDetailBinding
    private lateinit var vape: Vape
    private lateinit var mensajeAdapter: MensajeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    companion object {
        private const val ARG_Vape = "vape"
        fun newInstance(vape: Vape): VapeDetailFragment {
            val fragment = VapeDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_Vape, vape)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vape = it.getSerializable(ARG_Vape) as Vape
        }
        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("vapes/comentarios/${vape.nombreVape}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVapeDetailBinding.inflate(inflater, container, false)
        setupSendButton()
        setupRecyclerView()
        loadMessages()

        val vape = arguments?.getSerializable(ARG_Vape) as? Vape
        if (vape != null) {
            mostarDetallesVape(vape)
        } else {
            Toast.makeText(context, "No se ha podido cargar el vape", Toast.LENGTH_SHORT).show()
        }

        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val favoritoButton = binding.favorito as ImageButton
        checkIfVapeIsFavorito(vape, favoritoButton)
        favoritoButton.setOnClickListener {
            favoritoButton.isSelected = !favoritoButton.isSelected
            addVapeFavorito(vape, favoritoButton.isSelected)
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

    private fun checkIfVapeIsFavorito(vape: Vape?, favoritoButton: ImageButton) {
        val user = auth.currentUser
        if (user != null && vape != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val favoritesRef = database.child("users").child(email!!).child("Vapesfavoritos")

            favoritesRef.orderByChild("nombreVape").equalTo(vape.nombreVape).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    favoritoButton.isSelected = dataSnapshot.exists()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Error al verificar si el vape está en la lista de favoritos", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun addVapeFavorito(vape: Vape?, isFavorito: Boolean) {
        val user = auth.currentUser
        if (user != null && vape != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val favoritesRef = database.child("users").child(email!!).child("Vapesfavoritos")

            if (isFavorito) {
                // Añadir el vape a favoritos
                favoritesRef.push().setValue(vape)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Vape añadido a favoritos", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al añadir ${vape.nombreVape} a favoritos", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Eliminar el vape de favoritos
                favoritesRef.orderByChild("nombreVape").equalTo(vape.nombreVape).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            snapshot.ref.removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Vape eliminado de favoritos", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Error al eliminar ${vape.nombreVape} de favoritos", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(context, "Error al eliminar ${vape.nombreVape} de favoritos", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        } else {
            Toast.makeText(context, "No hay usuario autenticado o el vape es nulo", Toast.LENGTH_SHORT).show()
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

    private fun mostarDetallesVape(vape: Vape) {
        binding.nombreDelVape.text = ""
        binding.descripcionDelVape.text = ""

        obtenerVapeDeFirestore(vape)
    }

    private fun obtenerVapeDeFirestore(vape: Vape) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("vapes")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val vapeEnFirebase = snapshot.getValue(Vape::class.java)
                    if (vapeEnFirebase != null && vapeEnFirebase == vape) {
                        binding.nombreDelVape.text = vapeEnFirebase.nombreVape
                        binding.descripcionDelVape.text = vapeEnFirebase.descripcionVape
                        Glide.with(requireContext())
                            .load(vapeEnFirebase.imagenVape)
                            .centerCrop()
                            .into(binding.ivVapeDetail)
                        return
                    }
                }
                Toast.makeText(context, "No se encontraron detalles para este vape", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error al obtener los detalles del vape", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarRating(rating: Float) {
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val ratingRef = database.child("ratings").child("vapers").child(email!!).child(vape.nombreVape)

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
