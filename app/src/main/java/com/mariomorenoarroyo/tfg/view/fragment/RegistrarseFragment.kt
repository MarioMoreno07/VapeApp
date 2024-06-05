package com.mariomorenoarroyo.tfg.view.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.databinding.FragmentRegistrarseBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity
import com.mariomorenoarroyo.tfg.view.activity.ProviderType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegistrarseFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarseBinding
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarseBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.botonRegistrarse.setOnClickListener {
            registrarUsuario()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registrarUsuario() {
        val correoElectronico = binding.editTextCorreo.text.toString().trim()
        val fechaNacimiento = binding.editTextDate.text.toString().trim()
        val contrasena = binding.editTextContrasena.text.toString().trim()
        val nombre = binding.editTextNombre.text.toString().trim()
        val telefono = binding.editTextPhone.text.toString().trim()


        if (correoElectronico.isEmpty() || contrasena.isEmpty() || nombre.isEmpty() || telefono.isEmpty() || fechaNacimiento.isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!esMayorDe18(fechaNacimiento)) {
            Toast.makeText(context, "Debes tener al menos 18 años para crear una cuenta", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correoElectronico, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val email = task.result?.user?.email ?: ""
                    guardarDatosRealtimeDatabase(email)
                    showHome(email, ProviderType.BASIC)
                    findNavController().navigate(R.id.action_registrarseFragment_to_iniciarSesionFragment)
                } else {
                    Toast.makeText(context, "Error al registrar el usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun guardarDatosRealtimeDatabase(email: String) {
        val user = hashMapOf(
            "email" to binding.editTextCorreo.text.toString(),
            "nombre" to binding.editTextNombre.text.toString(),
            "telefono" to binding.editTextPhone.text.toString(),
            "fechaNacimiento" to binding.editTextDate.text.toString()
        )

        val sanitizedEmail = email.replace(".", ",")
        db.child("users").child(sanitizedEmail).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Usuario registrado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al guardar los datos en Realtime Database: ${e.message}", Toast.LENGTH_SHORT).show()
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
    private fun esMayorDe18(fechaNacimiento: String): Boolean {
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaNacimientoLocalDate = LocalDate.parse(fechaNacimiento, formato)
        val fechaActual = LocalDate.now().minusYears(18)
        return fechaNacimientoLocalDate.isBefore(fechaActual) || fechaNacimientoLocalDate.isEqual(fechaActual)
    }
}
