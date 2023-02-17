package com.example.burcycle.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.burcycle.databinding.FragmentCuentaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CuentaFragment : Fragment() {

    private var _binding: FragmentCuentaBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCuentaBinding.inflate(inflater, container, false)

        binding.bCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

        binding.bEliminarRecientes.setOnClickListener {
            val sharedPreferences =
                context?.getSharedPreferences("parkingsCercanos", Context.MODE_PRIVATE)?.edit()
            sharedPreferences?.remove("parking1")
            sharedPreferences?.remove("parking2")
            sharedPreferences?.remove("parking3")
            sharedPreferences?.remove("parking4")
            sharedPreferences?.remove("parking5")
            sharedPreferences?.apply()
            showDialog("Aparcamientos recientes eliminados", "Información")
        }
        return binding.root
    }

    private fun showDialog(mensaje: String, titulo: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, which ->
            }
            .show()
    }

    private fun cerrarSesion() {
        val prefs = context?.getSharedPreferences("inicioSesion", Context.MODE_PRIVATE)!!.edit()
        prefs.clear()
        prefs.apply()
        navegarLogin()
    }
    private fun navegarLogin() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


}