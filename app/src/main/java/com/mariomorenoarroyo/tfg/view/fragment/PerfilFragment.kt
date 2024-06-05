package com.mariomorenoarroyo.tfg.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.adapter.VapeFavoriteAdapter
import com.mariomorenoarroyo.tfg.data.model.Vape

class PerfilFragment : Fragment() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var buttonSaveProfile: Button
    private lateinit var buttonDeleteAccount: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        editTextUsername = view.findViewById(R.id.editTextUsername)
        editTextPhone = view.findViewById(R.id.editTextPhone)
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile)
        buttonDeleteAccount = view.findViewById(R.id.delete)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        loadProfile()

        buttonSaveProfile.setOnClickListener {
            saveProfile()
        }

        buttonDeleteAccount.setOnClickListener {
            deleteAccount()
        }

        return view
    }

    /**
     * Cargar los datos del perfil del usuario desde Realtime Database
     */
    private fun loadProfile() {
        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", ",")
            email?.let {
                database.child("users").child(email).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val username = snapshot.child("nombre").getValue(String::class.java)
                        val phone = snapshot.child("telefono").getValue(String::class.java)

                        editTextUsername.setText(username)
                        editTextPhone.setText(phone)
                    }
                }
            }
        }
    }

    /**
     * Guardar los datos del perfil del usuario en Realtime Database
     */
    private fun saveProfile() {
        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", ",")
            email?.let {
                val username = editTextUsername.text.toString()
                val phone = editTextPhone.text.toString()

                val updatesMap = mutableMapOf<String, Any>()
                updatesMap["nombre"] = username
                updatesMap["telefono"] = phone

                database.child("users").child(email).updateChildren(updatesMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Perfil actualizado, vuelve a iniciar sesión para ver los cambios", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    /**
     * Eliminar la cuenta del usuario
     */
    private fun deleteAccount() {
        val user = auth.currentUser
        user?.let {
            val email = it.email?.replace(".", ",")
            email?.let {
                // Eliminar los datos del usuario en Realtime Database
                database.child("users").child(email).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Eliminar el usuario de Firebase Authentication
                        user.delete().addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                Toast.makeText(context, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            } else {
                                Toast.makeText(context, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Error al eliminar los datos de usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}
