package com.mariomorenoarroyo.tfg.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.view.fragment.AjustesFragment
import com.mariomorenoarroyo.tfg.view.fragment.FavoritoskFragment
import com.mariomorenoarroyo.tfg.view.fragment.InicioFragment
import com.mariomorenoarroyo.tfg.view.fragment.PerfilFragment
import com.mariomorenoarroyo.tfg.view.fragment.SaboresFragment
import com.mariomorenoarroyo.tfg.view.fragment.TiendaFragment
import com.mariomorenoarroyo.tfg.view.fragment.VapeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        overridePendingTransition(R.anim.side_right, R.anim.fade_out)

        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navigationView = findViewById(R.id.nav_view)
        val btnShow: ImageButton = findViewById(R.id.btnShowMenu)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        replaceFragment(InicioFragment())

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_inicio -> {
                    replaceFragment(InicioFragment())
                    true
                }
                R.id.menu_tienda -> {
                    replaceFragment(TiendaFragment())
                    true
                }
                R.id.menu_sabor -> {
                    replaceFragment(SaboresFragment())
                    true
                }
                R.id.menu_vape -> {
                    replaceFragment(VapeFragment())
                    true
                }
                else -> false
            }
        }
        btnShow.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navigationView.getHeaderView(0)
        profileImageView = headerView.findViewById(R.id.imageView)
        userNameTextView = headerView.findViewById(R.id.nombreUsuario)
        database = FirebaseDatabase.getInstance()
        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userEmail = currentUser.email
            if (userEmail != null) {
                val usersRef = database.reference.child("users")
                val query = usersRef.orderByChild("email").equalTo(userEmail)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userName = userSnapshot.child("nombre").getValue(String::class.java)
                            if (userName != null) {
                                userNameTextView.text = userName
                            }
                            val profileImageUri = userSnapshot.child("profileImageUri").getValue(String::class.java)
                            profileImageUri?.let { uri ->
                                // Cargar la imagen de perfil en el ImageView usando Glide
                                Glide.with(this@MainActivity).load(Uri.parse(uri)).into(profileImageView)
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@MainActivity, "Error al obtener el nombre de usuario", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        profileImageView.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_edit_profile -> {
                    replaceFragment(PerfilFragment())
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(AjustesFragment())
                    true
                }
                R.id.favoritos -> {
                    replaceFragment(FavoritoskFragment())
                    true
                }
                R.id.log_out -> {
                    signOut()
                    true
                }
                else -> false
            }
        }
    }

    private fun signOut() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            data?.data?.let { uri ->
                saveImageUriToDatabase(uri)
                Glide.with(this).load(uri).into(profileImageView)
            }
        }
    }

    private fun saveImageUriToDatabase(imageUri: Uri) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userEmail = user.email
            userEmail?.let { email ->
                val userRef = database.reference.child("users")
                val query = userRef.orderByChild("email").equalTo(email)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userId = userSnapshot.key
                            userId?.let {
                                userRef.child(userId).child("profileImageUri").setValue(imageUri.toString())
                                    .addOnSuccessListener {
                                        Toast.makeText(this@MainActivity, "Imagen de perfil actualizada correctamente", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this@MainActivity, "Error al actualizar la imagen de perfil: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@MainActivity, "Error al obtener el ID de usuario", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_main, fragment)
            .addToBackStack(null)
            .commit()
    }
    companion object {
        private const val GALLERY_REQUEST_CODE = 100
    }
}
