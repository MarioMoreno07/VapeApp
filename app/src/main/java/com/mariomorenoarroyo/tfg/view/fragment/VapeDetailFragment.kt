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
        fun newInstance(vape : Vape): VapeDetailFragment {
            val fragment = VapeDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_Vape,vape)
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

        databaseReference = FirebaseDatabase.getInstance().getReference("vapes/${vape.nombreVape}/comentarios")
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
        } else{
            Toast.makeText(context, "No se ha podido cargar el vape", Toast.LENGTH_SHORT).show()
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
                        // Se encontró el vape correspondiente, mostrar los detalles
                        binding.nombreDelVape.text = vapeEnFirebase.nombreVape
                        binding.descripcionDelVape.text = vapeEnFirebase.descripcionVape
                        // Cargar la imagen del vape
                        Glide.with(requireContext())
                            .load(vapeEnFirebase.imagenVape)
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