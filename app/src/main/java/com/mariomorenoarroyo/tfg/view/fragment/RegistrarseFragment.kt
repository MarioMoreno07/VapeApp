package com.mariomorenoarroyo.tfg.view.fragment


import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.databinding.FragmentRegistrarseBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity

import com.mariomorenoarroyo.tfg.view.activity.ProviderType
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class RegistrarseFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarseBinding
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      binding = FragmentRegistrarseBinding.inflate(inflater, container, false)
        val view = binding.root


        val btnRegistrarse: Button = view.findViewById(R.id.botonRegistrarse)

        btnRegistrarse.setOnClickListener {
            registrarUsuario()
        }


        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registrarUsuario() {
        val correoElectronico = binding.editTextCorreo.text.toString()
        val fechaNacimiento = binding.editTextDate.text.toString()

        // Comprobar que el usuario tiene 18 años
        if (fechaNacimiento.isNotEmpty() && !esMayorDe18(fechaNacimiento)) {
            Toast.makeText(context, "Debes tener al menos 18 años para crear una cuenta", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar que se ingresaron todos los campos necesarios
        if (correoElectronico.isNotEmpty() && binding.editTextContrasena.text.isNotEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                correoElectronico,
                binding.editTextContrasena.text.toString()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val email = task.result?.user?.email ?: ""
                    // Guardar los datos en Firestore
                    guardarDatosFirestore(email)
                    showHome(email, ProviderType.BASIC)
                    findNavController().navigate(R.id.action_registrarseFragment_to_iniciarSesionFragment)
                } else {
                    Toast.makeText(context, "Error al registrar el usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }


    private fun guardarDatosFirestore(email: String) {
        db.collection("users").document(email).set(
            hashMapOf(
                "email" to binding.editTextCorreo.text.toString(),
                "nombre" to binding.editTextNombre.text.toString(),
                "telefono" to binding.editTextPhone.text.toString(),
                "fechaNacimiento" to binding.editTextDate.text.toString(),

                )
        ).addOnSuccessListener {
            Toast.makeText(requireContext(), "Usuario registrado", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Error al guardar los datos en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun esMayorDe18(fechaNacimiento: String): Boolean {

        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaNacimientoLocalDate = LocalDate.parse(fechaNacimiento, formato)
        val fechaActual = LocalDate.now().minusYears(18)

        return fechaNacimientoLocalDate.isBefore(fechaActual)
    }

}