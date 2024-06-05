package com.mariomorenoarroyo.tfg.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
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
import com.mariomorenoarroyo.tfg.data.model.Tienda
import com.mariomorenoarroyo.tfg.databinding.FragmentTiendaDetailsBinding

class TiendaDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTiendaDetailsBinding
    private lateinit var tienda: Tienda
    private lateinit var mensajeAdapter: MensajeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    companion object {
        private const val ARG_TIENDA = "tienda"
        fun newInstance(tienda : Tienda): TiendaDetailsFragment {
            val fragment = TiendaDetailsFragment()
            val args = Bundle()
            args.putSerializable(ARG_TIENDA,tienda)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       arguments?.let {
            tienda= it.getSerializable(ARG_TIENDA) as Tienda
       }
        auth= Firebase.auth
        databaseReference=FirebaseDatabase.getInstance().getReference("tiendas/comentarios/${tienda.nombreTienda}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentTiendaDetailsBinding.inflate(inflater,container,false)
        setupSendButton()
        setupRecyclerView()
        loadMessages()

        val tienda = arguments?.getSerializable(ARG_TIENDA) as? Tienda
        if(tienda!= null){
            mostrarDetalleTienda(tienda)
        }else{
            Toast.makeText(context, "No se ha podido cargar la tienda", Toast.LENGTH_SHORT).show()
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

        binding.favorito.setOnClickListener {
            addTiendaToFavorito(tienda)
        }

        binding.fabAddInstagram.setOnClickListener {
            Toast.makeText(context, "${tienda?.nombreTienda}Oficial", Toast.LENGTH_SHORT).show()
        }

        binding.fabAddPhone.setOnClickListener {
            Toast.makeText(context,"${tienda?.telefonoTienda}", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun addTiendaToFavorito(tienda: Tienda?) {
        val user = auth.currentUser
        if (user != null && tienda != null) {
            val userEmail = user.email
            val email = userEmail?.replace(".", ",")

            val database = FirebaseDatabase.getInstance().reference
            val favoritesRef = database.child("users").child(email!!).child("tiendasfavoritos")

            // Comprobar si el vape ya está en la lista de favoritos del usuario
            favoritesRef.orderByChild("nombreTienda").equalTo(tienda.nombreTienda).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // El vape ya está en la lista de favoritos
                        Toast.makeText(context, "La tienda ya está en la lista de favoritos", Toast.LENGTH_SHORT).show()
                    } else {
                        // El vape no está en la lista de favoritos, añadirlo
                        favoritesRef.push().setValue(tienda)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Tienda añadido a favoritos", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al añadir ${tienda.nombreTienda} a favoritos", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Error al verificar si el vape está en la lista de favoritos", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "No hay usuario autenticado o vape es nulo", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupSendButton() {
        binding.buttonSendComment.setOnClickListener {
            val mensajeText = binding.editTextComment.text.toString().trim()
            if (mensajeText.isNotEmpty()) {

                val userID = auth.currentUser?.uid

                val mensajeID = databaseReference.push().key
                val nuevoMensaje = userID?.let { it1 -> Mensaje(mensajeText) }

                // Guardar el mensaje en la base de datos
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
        mensajeAdapter = MensajeAdapter(emptyList()) // Inicializamos el adaptador con una lista vacía
        binding.recyclerViewComments.apply {
            adapter = mensajeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadMessages() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
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

    private fun mostrarDetalleTienda(tienda: Tienda) {
        binding.nombreDelTienda.text= ""
        binding.horario.text= ""

        obtenerTiendasDeFirestore(tienda)
    }

    private fun obtenerTiendasDeFirestore(tienda: Tienda) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("tiendas")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                for(snapshot in datasnapshot.children){
                    val tiendaEnFirestore = snapshot.getValue(Tienda::class.java)
                    if (tiendaEnFirestore != null && tiendaEnFirestore == tienda ) {
                        binding.nombreDelTienda.text = tiendaEnFirestore.nombreTienda
                        binding.horario.text = tiendaEnFirestore.horarioTienda
                        Glide.with(requireContext())
                            .load(tiendaEnFirestore.imagenTienda)
                            .centerCrop()
                            .into(binding.ivTiendaDetail)
                        return
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al obtener el sabor", Toast.LENGTH_SHORT).show()
            }
        })

    }
}