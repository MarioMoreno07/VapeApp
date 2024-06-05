package com.mariomorenoarroyo.tfg.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mariomorenoarroyo.tfg.data.model.Noticia
import com.mariomorenoarroyo.tfg.databinding.FragmentAddNoticiaBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity

class AddNoticiaFragment : Fragment() {

    private lateinit var binding: FragmentAddNoticiaBinding
    private lateinit var operativeActivity: MainActivity
    private val db = FirebaseDatabase.getInstance().reference
    private var imageUri: Uri? = null

    // Launcher for the gallery intent
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data?.data
            Glide.with(this)
                .load(imageUri)
                .into(binding.imageView2)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operativeActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNoticiaBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.buttonAdd.setOnClickListener {
            addNoticia()
        }

        binding.buttonCancelar.setOnClickListener {
            operativeActivity.replaceFragment(InicioFragment())
        }

        binding.imageView2.setOnClickListener {
            selectImageFromGallery()
        }

        return view
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun addNoticia() {
        val titulo = binding.edTitulo.text.toString().trim()
        val contenido = binding.edContenido.text.toString().trim()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val id = db.child("noticias_globales").push().key

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(contenido) || imageUri == null) {
            Toast.makeText(context, "Por favor, completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        id?.let {
            val noticia = Noticia(titulo, contenido, imageUri.toString(), userId, it)

            db.child("noticias_globales").child(it).setValue(noticia)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Noticia añadida con éxito", Toast.LENGTH_SHORT).show()
                    operativeActivity.replaceFragment(InicioFragment())
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al añadir noticia: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
