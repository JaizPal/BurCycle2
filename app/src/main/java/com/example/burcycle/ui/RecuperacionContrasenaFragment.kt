package com.example.burcycle.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.burcycle.databinding.FragmentRecuperacionContrasenaBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class RecuperacionContrasenaFragment : Fragment() {

    private var _binding: FragmentRecuperacionContrasenaBinding? = null
    private val binding get() = _binding!!

    private lateinit var textInputEmail: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecuperacionContrasenaBinding.inflate(inflater, container, false)
        textInputEmail = binding.textFieldEmail
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bRecuperar.setOnClickListener {
            if (comprobarEmail(binding.inEmail)) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(
                    binding.inEmail.text?.trim().toString().lowercase()
                ).addOnSuccessListener {
                    showAlert("Email enviado", "Email enviado")
                    findNavController().popBackStack()
                }.addOnFailureListener {
                    showAlert(it.message.toString(), "Error")
                }
            }
        }
    }

    private fun comprobarEmail(email: TextInputEditText): Boolean {
        var correcto = true
        if (email.text.toString().isBlank()) {
            textInputEmail.error = "Introduce el email"
            textInputEmail.isErrorEnabled = true
            correcto = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            textInputEmail.error = "No es un email válido"
            textInputEmail.isErrorEnabled = true
            correcto = false
        } else {
            textInputEmail.error = null
            textInputEmail.isErrorEnabled = false
        }
        return correcto
    }

    private fun showAlert(mensaje: String, titulo: String) {
        val mensajeInfo = when (mensaje) {
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> "El usuario no existe"
            "Email enviado" -> "Se ha enviado un email de recuperación de contraseña a su correo electrónico"
            else -> mensaje

        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
        builder.setMessage(mensajeInfo)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}