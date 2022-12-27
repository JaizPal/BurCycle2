package com.example.burcycle.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.burcycle.R
import com.example.burcycle.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var textInputEmail: TextInputLayout
    private lateinit var textInputPassword: TextInputLayout

    private val GOOGLE_CODE = 100

    private val googleLoginIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener{
                            if (it.isSuccessful) {
                                guardarDatosUsuario(it.result.user?.email ?: "")
                                navegarInicio()
                            }
                        }.addOnFailureListener {
                            showAlert(it.message.toString(), "Error")
                        }
                    Log.d("--- Google Login ---", "Correcto")
                }
                Log.d("--- Google Login ---", "Incorrecto")
            } catch (e: ApiException) {
                Log.d("--- Google Login ---", "Incorrecto")
                Log.d("||| Google Login |||", e.message.toString())
                System.err.println(e.message)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        textInputEmail = binding.textFieldEmail
        textInputPassword = binding.textFieldPassword
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bEntrar.setOnClickListener {
            iniciarSesion()
        }

        binding.bRegistrarse.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.bEntrarGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        binding.tvOlvidoContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_recuperacionContrasenaFragment)
        }
    }

    private fun iniciarSesion() {
        val email = binding.inEmail
        val password = binding.inPassword
        if (comprobarCampos(email, password)) {

            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email.text.toString().trim().lowercase(), password.text.toString().trim()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
                        guardarDatosUsuario(it.result.user?.email ?: "")
                        navegarInicio()
                    } else {
                        showAlert("Confirmación email", "Error")
                    }
                }
            }.addOnFailureListener {
                password.text = null
                showAlert(it.message.toString(), "Error")
            }
        }
    }



    private fun iniciarSesionGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val googleClient = GoogleSignIn.getClient(context!!, googleConf)
        googleClient.signOut()
        googleLoginIntent.launch(googleClient.signInIntent)
    }

    private fun comprobarCampos(email: TextInputEditText, password: TextInputEditText): Boolean {
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

        if (password.text.toString().isBlank()) {
            textInputPassword.error = "Introduce la contraseña"
            textInputPassword.isErrorEnabled = true
            correcto = false
        } else {
            textInputPassword.error = null
            textInputPassword.isErrorEnabled = false
        }
        return correcto
    }

    private fun guardarDatosUsuario(email: String) {
        val prefs = context?.getSharedPreferences("inicioSesion", Context.MODE_PRIVATE)!!.edit()
        prefs.putString("email", email)
        prefs.apply()
    }

    private fun showAlert(mensaje: String, titulo: String) {
        val mensajeError = when (mensaje) {
            "Confirmación email" -> "Confirme la verificación del email en su correo electrónico"
            "The password is invalid or the user does not have a password." -> "Contraseña incorrecta"
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> "El usuario no existe"
            "The email address is already in use by another account." -> "La cuenta ya existe."
            "12501: " -> "Inicio de sesión cancelado"
            else -> {
                mensaje
            }
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
        builder.setMessage(mensajeError)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun navegarInicio() {
        val intent = Intent(context, InicioActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}