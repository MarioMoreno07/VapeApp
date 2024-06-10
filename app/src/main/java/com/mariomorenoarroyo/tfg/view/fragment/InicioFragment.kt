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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mariomorenoarroyo.tfg.data.adapter.NoticiaAdapter
import com.mariomorenoarroyo.tfg.data.adapter.NoticiaListener
import com.mariomorenoarroyo.tfg.data.model.Noticia
import com.mariomorenoarroyo.tfg.databinding.FragmentInicioBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity
import com.google.firebase.storage.FirebaseStorage

class InicioFragment : Fragment(), NoticiaListener {

    private lateinit var binding: FragmentInicioBinding
    private lateinit var operativeActivity: MainActivity
    private lateinit var noticiaAdapter: NoticiaAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var noticias: MutableList<Noticia>
    private var imageUri: Uri? = null

    // Launcher para el contrato de selección de imagen
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { uri ->
                // Cuando se selecciona una imagen, cargarla en la lista de noticias
                imageUri = uri
                // Lógica para guardar la imagen y mostrarla en la lista de noticias
            }
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

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        binding.noticias.layoutManager = LinearLayoutManager(context)
        binding.addNoticia.setOnClickListener {
            operativeActivity.replaceFragment(AddNoticiaFragment())
        }

        binding.noticias.layoutManager = LinearLayoutManager(requireContext())

        loadNoticias()
        noticias = mutableListOf()
        noticiaAdapter = NoticiaAdapter(noticias,this,requireContext())
        binding.noticias.adapter = noticiaAdapter

        return binding.root
    }

    private fun loadNoticias() {
        val user = auth.currentUser
        user?.let {
            database.child("noticias_globales")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        noticias.clear()
                        for (dataSnapshot in snapshot.children) {
                            val noticia = dataSnapshot.getValue(Noticia::class.java)
                            noticia?.let {
                                // Si hay una imagen asociada a la noticia, cargarla desde Firebase Storage
                                if (!TextUtils.isEmpty(it.imagen)) {
                                    loadImageFromStorage(it)
                                } else {
                                    noticias.add(it)
                                }
                            }
                        }
                        noticiaAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error al cargar las noticias", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun loadImageFromStorage(noticia: Noticia) {
        // Obtener la referencia al almacenamiento de Firebase donde se encuentra la imagen
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(noticia.imagen)

        // Descargar la imagen y cargarla en la lista de noticias
        storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
            noticia.imagen = imageUrl.toString()
            noticias.add(noticia)
            noticiaAdapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Error al cargar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNoticiaClick(noticia: Noticia) {
        operativeActivity.replaceFragment(NoticiaDetailFragment.newInstance(noticia))
    }
}
