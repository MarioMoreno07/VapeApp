package com.mariomorenoarroyo.tfg.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.data.model.Noticia
import com.mariomorenoarroyo.tfg.data.model.Vape
import com.mariomorenoarroyo.tfg.databinding.FragmentDetailNoticiaBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity


class NoticiaDetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailNoticiaBinding
    private lateinit var noticia: Noticia
    private lateinit var auth: FirebaseAuth
    private lateinit var operativeActivity: MainActivity



    companion object {
        private const val ARG_Noticia = "NOTICIA"
        fun newInstance(noticia: Noticia): NoticiaDetailFragment {
            val fragment = NoticiaDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_Noticia, noticia)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        operativeActivity = activity as MainActivity
        arguments?.let {
            noticia = it.getSerializable(ARG_Noticia) as Noticia
        }
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentDetailNoticiaBinding.inflate(inflater, container, false)

        val noticia = arguments?.getSerializable(ARG_Noticia) as? Noticia
        if(noticia != null){
            mostrarDetallesNoticia(noticia)
        }else{
            Toast.makeText(context, "Error al cargar la noticia", Toast.LENGTH_SHORT).show()
        }
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.eliminar.setOnClickListener {
            eliminarNoticia(noticia)
        }

        binding.editar.setOnClickListener {
            //operativeActivity.replaceFragment(EditarNoticiaFragment.newInstance(noticia))
            editarNoticia(noticia)
        }


        return binding.root
    }


    private fun mostrarDetallesNoticia(noticia: Noticia) {
        binding.tituloNoticia.text = ""
        binding.contenidoNoticia.text = ""

        obtenerNoticia(noticia)
    }

    private fun obtenerNoticia(noticia: Noticia) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("noticias_globales")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val noticiaEnFirebase = snapshot.getValue(Noticia::class.java)
                    if (noticiaEnFirebase != null && noticiaEnFirebase == noticia) {
                        binding.tituloNoticia.text = noticiaEnFirebase.titulo
                        binding.contenidoNoticia.text = noticiaEnFirebase.contenido
                        Glide.with(requireContext())
                            .load(noticiaEnFirebase.imagen)
                            .centerCrop()
                            .into(binding.imagenNoticia)
                        return
                    }
                }
                // No se encontró el vape correspondiente
                Toast.makeText(context, "No se encontraron detalles para esta noticia", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error al obtener los detalles de la noticia", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun eliminarNoticia(noticia: Noticia?) {
        noticia?.let {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = noticia.userId
                val idNoticia = noticia.noticiaId

                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("noticias_globales")

                databaseReference.orderByChild("noticiaId").equalTo(idNoticia)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val noticiaEnFirebase = snapshot.getValue(Noticia::class.java)
                                if (noticiaEnFirebase != null && noticiaEnFirebase.userId == userId) {
                                    // Verificar si el usuario actual es el propietario de la noticia
                                    if (currentUser.uid == userId) {
                                        snapshot.ref.removeValue()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Noticia eliminada correctamente", Toast.LENGTH_SHORT).show()
                                                requireActivity().onBackPressed()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Error al eliminar la noticia: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "No tienes permiso para eliminar esta noticia", Toast.LENGTH_SHORT).show()
                                    }
                                    return
                                }
                            }
                            // No se encontró la noticia
                            Toast.makeText(context, "No se encontró la noticia", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(context, "Error al eliminar la noticia: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editarNoticia(noticia: Noticia?) {
        noticia?.let {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = noticia.userId
                val idNoticia = noticia.noticiaId

                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("noticias_globales")

                databaseReference.orderByChild("noticiaId").equalTo(idNoticia)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val noticiaEnFirebase = snapshot.getValue(Noticia::class.java)
                                if (noticiaEnFirebase != null && noticiaEnFirebase.userId == userId) {
                                    // Verificar si el usuario actual es el propietario de la noticia
                                    if (currentUser.uid == userId) {
                                        operativeActivity.replaceFragment(EditarNoticiaFragment.newInstance(noticia))
                                        Toast.makeText(context, "Puedes editar la noticia", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "No tienes permiso para eliminar esta noticia", Toast.LENGTH_SHORT).show()
                                    }
                                    return
                                }
                            }
                            // No se encontró la noticia
                            Toast.makeText(context, "No se encontró la noticia", Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(context, "Error al eliminar la noticia: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }

}