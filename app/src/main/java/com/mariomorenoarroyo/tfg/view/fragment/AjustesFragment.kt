package com.mariomorenoarroyo.tfg.view.fragment

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mariomorenoarroyo.tfg.R

class AjustesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val MY_CHANNEL_ID = "channelId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferencesajustes, rootKey)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this)
    }

    fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel=NotificationChannel(
                MY_CHANNEL_ID,
                "Canal de notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description="Canal de notificaciones"
            }
            val notificationManager: NotificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "dark_mode" -> {
                val isDarkModeEnabled = sharedPreferences?.getBoolean("dark_mode", false) ?: false
                if (isDarkModeEnabled) {
                    // Habilitar el modo oscuro
                    activity?.let { activity ->
                        if (activity is AppCompatActivity) {
                            activity.delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                        }
                    }
                } else {
                    // Deshabilitar el modo oscuro
                    activity?.let { activity ->
                        if (activity is AppCompatActivity) {
                            activity.delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO  
                        }
                    }
                }
            }
            "notifications" -> {
                val areNotificationsEnabled = sharedPreferences?.getBoolean("notifications", true) ?: true
                if (areNotificationsEnabled) {
                    Toast.makeText(requireContext(), "Notificaciones activadas", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Notificaciones desactivadas", Toast.LENGTH_SHORT).show()
                }

                // Actualizar la visibilidad del botón de notificación
                val notificationButton = findPreference<Preference>("notification_button")
                notificationButton?.isVisible = areNotificationsEnabled
                notificationButton?.setOnPreferenceClickListener {
                    createASimpleNotification(requireContext(),"Notificación de prueba", "Esto es una notificación de prueba")
                    true
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun createASimpleNotification(context: Context,tittle:String, content:String){
        // Verificar si el contexto está disponible
        val context = context ?: return

        // Crear la notificación
        val builder = NotificationCompat.Builder(context, MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(tittle)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Mostrar la notificación
        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}
