package com.mariomorenoarroyo.tfg.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mariomorenoarroyo.tfg.R

class AjustesFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this)
    }

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
                    // Habilitar las notificaciones
                } else {
                    // Deshabilitar las notificaciones
                }
            }

            "letra" -> {
                val fontSize = sharedPreferences?.getString("font_size", "medium") ?: "medium"
                when (fontSize) {
                    "small" -> {
                        // Cambiar el tamaño de la letra a pequeño

                    }
                    "medium" -> {
                        // Cambiar el tamaño de la letra a mediano
                    }
                    "large" -> {
                        // Cambiar el tamaño de la letra a grande
                    }
                }
            }
        }
    }

}