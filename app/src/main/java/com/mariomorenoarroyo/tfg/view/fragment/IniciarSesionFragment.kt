package com.mariomorenoarroyo.tfg.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.databinding.FragmentIniciarSesionBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity

import com.mariomorenoarroyo.tfg.view.activity.ProviderType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


class IniciarSesionFragment : Fragment() {

    private val GOOGLE_SIGN_IN = 100
    private lateinit var binding: FragmentIniciarSesionBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentIniciarSesionBinding.inflate(inflater, container, false)
        val view=binding.root


        val btnIniciarSesion: Button = view.findViewById(R.id.iniciarSesion)
        val btnRegistrarse: Button = view.findViewById(R.id.registrarse)
        val btnGoogle: Button = view.findViewById(R.id.google)
        val olvContrasena: TextView = view.findViewById(R.id.olvContraseña)



        session()



        btnIniciarSesion.setOnClickListener {
            iniciarUsuario()
        }

        btnGoogle.setOnClickListener {

           iniciarSesionGoogle()
        }

        btnRegistrarse.setOnClickListener {
            // Navegar al RegistrarseFragment al seleccionar Registrarse
            findNavController().navigate(R.id.action_iniciarSesionFragment_to_registrarseFragment)
        }

        olvContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_iniciarSesionFragment_to_olvidarContrasenaFragment)
        }

        return view
    }

    private fun iniciarSesionGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        val googleClient = GoogleSignIn.getClient(requireContext(), googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }


    private fun session() {
        // Obtener SharedPreferences del contexto de la aplicación
        val prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null) {
            showHome(email, ProviderType.valueOf(provider))
        }
    }

    private fun iniciarUsuario() {
        if (binding.correo.text.isNotEmpty() && binding.contrasena.text.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.correo.text.toString(),
                binding.contrasena.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Si la operación es exitosa, muestra la pantalla principal
                    showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                } else {
                    // Si hay un error, muestra un mensaje de error al usuario
                    Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Si los campos están vacíos, muestra un mensaje indicando que deben completarse
            Toast.makeText(context, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN ) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val email = account.email
                    if (email != null) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    showHome(email, ProviderType.GOOGLE)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error al iniciar sesión",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "El correo electrónico de la cuenta de Google es nulo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Imprime el número de error
                    Toast.makeText(
                        context,
                        "Error al iniciar sesión: Cuenta de Google nula",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Error en Google Sign-In: ${e.statusCode}")
                e.printStackTrace()
                Toast.makeText(context, "Error en Google Sign-In", Toast.LENGTH_SHORT).show()
            }
        }
    }




}