package com.mariomorenoarroyo.tfg.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mariomorenoarroyo.tfg.data.model.Noticia
import com.mariomorenoarroyo.tfg.databinding.FragmentEditarNoticiaBinding

class EditarNoticiaFragment : Fragment() {
    private lateinit var binding: FragmentEditarNoticiaBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    companion object {
        private const val ARG_Noticia = "NOTICIA"
        fun newInstance(noticia: Noticia?): EditarNoticiaFragment {
            val fragment = EditarNoticiaFragment()
            val args = Bundle()
            args.putSerializable(ARG_Noticia, noticia)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditarNoticiaBinding.inflate(inflater, container, false)

        cargarNoticia()

        binding.buttonCancelar.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.buttonAceptar.setOnClickListener {
            guardarNoticia()
        }

        return binding.root
    }

    private fun cargarNoticia() {
        val noticia = arguments?.getSerializable(ARG_Noticia) as? Noticia
        if (noticia != null) {
            binding.edTitulo.setText(noticia.titulo)
            binding.edContenido.setText(noticia.contenido)
            Glide.with(requireContext())
                .load(noticia.imagen)
                .into(binding.imageView2)
        }
    }

    private fun guardarNoticia() {
        val noticia = arguments?.getSerializable(ARG_Noticia) as? Noticia
        if (noticia != null && noticia.noticiaId.isNotEmpty()) {
            val nuevoTitulo = binding.edTitulo.text.toString()
            val nuevoContenido = binding.edContenido.text.toString()

            val databaseReference = FirebaseDatabase.getInstance().getReference("noticias_globales")

            // Actualizar los valores de la noticia existente
            val updatesMap = hashMapOf<String, Any>(
                "titulo" to nuevoTitulo,
                "contenido" to nuevoContenido
            )

            databaseReference.child(noticia.noticiaId).updateChildren(updatesMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Noticia actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al actualizar la noticia", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Error: ID de la noticia no válido", Toast.LENGTH_SHORT).show()
        }
    }

}
