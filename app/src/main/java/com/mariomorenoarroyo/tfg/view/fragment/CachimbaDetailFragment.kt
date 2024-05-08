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
        fun newInstance(cachimba : Cachimba): CachimbaDetailFragment {
            val fragment = CachimbaDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_CACHIMBA,cachimba)
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

        databaseReference = FirebaseDatabase.getInstance().getReference("cachimbas/${cachimba.nombreCachimba}/comentarios")
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
        } else{
            Toast.makeText(context, "No se ha podido cargar la cachimba", Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }

    private fun setupSendButton() {
        binding.buttonSendComment.setOnClickListener {
            val mensajeText = binding.editTextComment.text.toString().trim()
            if (mensajeText.isNotEmpty()) {
                // Obtener el ID del usuario actual
                val userID = auth.currentUser?.uid

                // Crear un nuevo mensaje
                val mensajeID = databaseReference.push().key
                val nuevoMensaje = userID?.let { it1 -> Mensaje(mensajeText) }

                // Guardar el mensaje en la base de datos
                mensajeID?.let {
                    databaseReference.child(it).setValue(nuevoMensaje)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show()
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
        mensajeAdapter = MensajeAdapter(emptyList()) // Inicializamos el adaptador con una lista vacía
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
                        // Se encontró el vape correspondiente, mostrar los detalles
                        binding.nombreDelVape.text = vapeEnFirebase.nombreCachimba
                        binding.descripcionDelVape.text = vapeEnFirebase.descripcionCachimba
                        // Cargar la imagen del vape
                        Glide.with(requireContext())
                            .load(vapeEnFirebase.imagenCachimba)
                            .centerCrop()
                            .into(binding.ivVapeDetail)
                        return
                    }
                }
                // No se encontró el vape correspondiente
                Toast.makeText(context, "No se encontraron detalles para este vape", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error al obtener los detalles del vape", Toast.LENGTH_SHORT).show()
            }
        })
    }
}