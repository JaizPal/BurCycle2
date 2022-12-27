package com.example.burcycle.ui

import android.app.AlertDialog
import android.os.Bundle
import android.os.PatternMatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.burcycle.R
import com.example.burcycle.databinding.FragmentRegisterBinding
import com.example.burcycle.ui.viewModel.RegisterViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registrerViewModel: RegisterViewModel by viewModels()

    private lateinit var textInputLayoutEmail : TextInputLayout
    private lateinit var textInputLayoutPassword : TextInputLayout
    private lateinit var textInputLayoutPasswordRepeat : TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrerViewModel.email.observe(this, Observer {
            binding.inEmail.setText(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        textInputLayoutEmail = binding.textFieldEmail
        textInputLayoutPassword = binding.textFieldPassword
        textInputLayoutPasswordRepeat = binding.textFieldPasswordRepeat
        return binding.root

    }

    private fun registrar() {
        val email = binding.inEmail.text.toString().trim().lowercase()
        val password = binding.inPassword.text.toString().trim()
        val passwordRepeat = binding.inPasswordRepeat.text.toString().trim()
        if (comprobarRegistro(email,password, passwordRepeat)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email, password
            ).addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Log.i("--- REGISTRO ---", "Email enviado")
                            showAlert("Email enviado", "")
                            navegarPrincipal()
                        }?.addOnFailureListener {
                            Log.i("--- REGISTRO ---", "Error en el registro, email no enviado")
                            showAlert(it.message.toString(), "Error")
                        }
                }
            }.addOnFailureListener {
                showAlert(it.message.toString(), "Error")
                Log.d("--- REGISTRO ---", "Error en el registro")
                Log.d("||| Causa |||", "${it.message} ${it.cause}")
            }
        }
    }

    private fun comprobarRegistro(email: String, password: String, passwordRepeat: String): Boolean {
        var correcto = true;
        if (email.isBlank()) {
            textInputLayoutEmail.error = "Campo obligatorio"
            textInputLayoutEmail.isErrorEnabled = true
            correcto = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayoutEmail.error = "No es un email válido"
            textInputLayoutEmail.isErrorEnabled = true
            correcto = false
        } else {
            textInputLayoutEmail.error = null
            textInputLayoutEmail.isErrorEnabled = false
        }

        if (password.isBlank()) {
            textInputLayoutPassword.error = "Campo obligatorio"
            textInputLayoutPassword.isErrorEnabled = true
            correcto = false
        } else if (password.length < 8) {
            textInputLayoutPassword.error = "La contraseña debe contener 8 caracteres"
            textInputLayoutPassword.isErrorEnabled = true
            correcto = false
        } else {
            textInputLayoutPassword.error = null
            textInputLayoutPassword.isErrorEnabled = false
        }

        if(passwordRepeat.isBlank()) {
            textInputLayoutPasswordRepeat.error = "Campo obligatorio"
            textInputLayoutPasswordRepeat.isErrorEnabled = true
            correcto = false
        } else if (passwordRepeat != password) {
            textInputLayoutPasswordRepeat.error = "La contraseña no coincide"
            textInputLayoutPasswordRepeat.isErrorEnabled = true
            correcto = false
        } else {
            textInputLayoutPasswordRepeat.error = null
            textInputLayoutPasswordRepeat.isErrorEnabled = false
        }
        return correcto
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bRegistrarse.setOnClickListener {
            registrar()
        }

//        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAlert(mensaje: String, titulo: String) {
        val contenidoMensaje = when (mensaje) {
            "Email enviado" -> "Se ha enviado un email de confirmación a su correo electrónico"
            "The password is invalid or the user does not have a password." -> "La contraseña es inválida o el usuario no tiene contraseña"
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> "El usuario no existe"
            "The email address is already in use by another account." -> "La cuenta ya existe."
            "12501: " -> "Inicio de sesión cancelado"
            else -> {
                "Error desconocido $mensaje"
            }
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
        builder.setMessage(contenidoMensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun navegarPrincipal() {
//        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }
}