package com.mariomorenoarroyo.tfg.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.childEvents
import com.mariomorenoarroyo.tfg.data.adapter.NoticiaAdapter
import com.mariomorenoarroyo.tfg.data.adapter.NoticiaListener
import com.mariomorenoarroyo.tfg.data.model.Noticia
import com.mariomorenoarroyo.tfg.databinding.FragmentInicioBinding
import com.mariomorenoarroyo.tfg.view.activity.MainActivity

class InicioFragment : Fragment(), NoticiaListener {

    private lateinit var binding: FragmentInicioBinding
    private lateinit var operativeActivity: MainActivity
    private lateinit var noticiaAdapter: NoticiaAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var noticias: MutableList<Noticia>

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
                                val sabor = dataSnapshot.getValue(Noticia::class.java)
                                sabor?.let { noticias.add(it) }
                            }
                            noticiaAdapter.notifyDataSetChanged()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(context, "Error al cargar las noticias", Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }
    override fun onNoticiaClick(noticia: Noticia) {
        operativeActivity.replaceFragment(NoticiaDetailFragment.newInstance(noticia))
    }
}
