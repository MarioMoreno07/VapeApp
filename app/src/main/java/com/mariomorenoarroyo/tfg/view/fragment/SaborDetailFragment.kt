package com.mariomorenoarroyo.tfg.view.fragment

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.adapter.MensajeAdapter
import com.mariomorenoarroyo.tfg.data.model.Mensaje
import com.mariomorenoarroyo.tfg.data.model.Sabor
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.databinding.FragmentSaborDetailBinding
import com.mariomorenoarroyo.tfg.databinding.FragmentSaboresBinding

class SaborDetailFragment : Fragment() {
    private lateinit var binding: FragmentSaborDetailBinding
    private lateinit var sabor: Sabor
    private lateinit var mensajeAdapter: MensajeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference


    companion object {
        private const val ARG_SABOR = "sabor"
        fun newInstance(sabor : Sabor): SaborDetailFragment {
            val fragment = SaborDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_SABOR,sabor)
            fragment.arguments = args
            return fragment

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sabor = it.getSerializable(ARG_SABOR) as Sabor
        }
        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference("sabores/${sabor.nombreSabor}/comentarios")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSaborDetailBinding.inflate(inflater, container, false)
        setupSendButton()
        setupRecyclerView()
        loadMessages()

        val sabor = arguments?.getSerializable(ARG_SABOR) as? Sabor
        if (sabor != null) {
            mostarDetallesSabor(sabor)
        } else{
            Toast.makeText(context, "No se ha podido cargar el sabor", Toast.LENGTH_SHORT).show()
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

    private fun mostarDetallesSabor(sabor: Sabor) {
        binding.nombreDelSabor.text=""
        binding.descripcionDelSabor.text=""

        obtenerSaboresDeFirestore(sabor)
    }

    private fun obtenerSaboresDeFirestore(sabor:Sabor) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("sabores")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                for(snapshot in datasnapshot.children){
                    val saborEnFirestore = snapshot.getValue(Sabor::class.java)
                    if (saborEnFirestore != null && saborEnFirestore == sabor ) {
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

}