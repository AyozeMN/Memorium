package com.ayoze.memorium.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.ayoze.memorium.R
import com.ayoze.memorium.databinding.ActivityAuthBinding
import com.ayoze.memorium.model.Roles
import com.ayoze.memorium.repository.AuthRepository
import com.ayoze.memorium.repository.UserRepository
import com.ayoze.memorium.util.Helper
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AuthActivity : ComponentActivity() {

    private lateinit var binding: ActivityAuthBinding
    private var helper = Helper()
    private var uId: String? = null
    private var email: String? = null
    private var rol: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup
        setUp()
        // Session
        session()
    }

    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = VISIBLE

    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        uId = prefs.getString("uId", null)
        email = prefs.getString("email", null)
        rol = prefs.getString("rol", null)
        if (!uId.isNullOrBlank() && !rol.isNullOrBlank()) {
            binding.authLayout.visibility = View.INVISIBLE
            if (rol == Roles.COLABORADOR.name && !email.isNullOrBlank()) {
                showCollaboratorHome(uId!!, email!!)
            } else if (rol == Roles.PACIENTE.name && !email.isNullOrBlank()) {
                showPatientHome(uId!!, email!!)
            }
        }
    }

    private val authRepository =
        AuthRepository(object : AuthRepository.OnAuthActionPerformedListener {
            override fun onUserAuthenticationPerformed(uId: String, email: String) {
                lifecycleScope.launch {
                    this@AuthActivity.uId = uId
                    this@AuthActivity.email = email
                    userRepository.getRolFromId(uId)
                }
            }

            override fun onUserRegisteredPerformed(uId: String, email: String, rol: Roles) {
                lifecycleScope.async {
                    this@AuthActivity.uId = uId
                    this@AuthActivity.email = email
                    this@AuthActivity.rol = rol.name
                    userRepository.createUser(uId, email, rol)
                    redirect(this@AuthActivity.rol!!)
                }

            }

            override fun onError(errorMessage: String) {
                Log.w(TAG, errorMessage)
                Toast.makeText(
                    baseContext,
                    "Registro fallido",
                    Toast.LENGTH_LONG,
                ).show()
            }

        })

    private val userRepository =
        UserRepository(object : UserRepository.OnUserActionPerformedListener {
            override fun onUserCreated(uId: String, email: String, rol: Roles) {
                Log.i(
                    TAG,
                    ">> Datos obtenidos fin crear user en bbdd uId:$uId, mail:$email, rol:${rol.name}"
                )
            }

            override fun onRolFetched(rol: Roles) {
                this@AuthActivity.rol = rol.name
                redirect(this@AuthActivity.rol!!)
            }

            override fun onError(errorMessage: String) {
                Log.e(TAG, errorMessage)
                Toast.makeText(
                    baseContext,
                    "Registro fallido",
                    Toast.LENGTH_LONG,
                ).show()
            }
        })

    private fun setUp() {

        title = "Autenticación"

        // Registro
        binding.signUpBtn.setOnClickListener {
            //La primera vez que se pulse el botón muestro el radio button del rol y los campos de nuevo user
            binding.rolRadioGrp.visibility = VISIBLE
            binding.registerDataLL.visibility = VISIBLE

            // Comprobación datos registro
            if (helper.checkTextFields(
                    arrayOf(
                        binding.emailEditText, binding.passwordEditText
                    )
                ) && helper.checkRGButtons(binding.rolRadioGrp)
            ) {
                val emailText = binding.emailEditText.text.toString()
                val passwordText = binding.passwordEditText.text.toString()
                val nameText = binding.nameEditText.text.toString()
                val birthDateText = binding.birthDateEditText.text.toString()
                val rolText = Helper().getRolOnRegister(binding.rolRadioGrp)
                Log.i(
                    TAG,
                    "Datos registro mail:$emailText, rol:$rolText, name:$nameText, bdate:$birthDateText"
                )

                lifecycleScope.launch {
                    authRepository.registerUser(emailText, passwordText, rolText)
                }
            } else {
                if (helper.checkRGButtons(binding.rolRadioGrp)) {
                    binding.colaboradorRadioBtn.error = null
                } else {
                    binding.colaboradorRadioBtn.error = "Tiene que seleccionar un rol"
                }
            }
        }

        // Login
        binding.loginBtn.setOnClickListener {
            // Comprobación datos login
            if (helper.checkTextFields(arrayOf(binding.emailEditText, binding.passwordEditText))) {
                val emailText = binding.emailEditText.text.toString()
                val passwordText = binding.passwordEditText.text.toString()
                lifecycleScope.launch {
                    async { authRepository.authenticateUser(emailText, passwordText) }.await()
                }
            }
        }
    }

    private fun redirect(rol: String) {
        if (rol == Roles.COLABORADOR.name) {
            uId?.let { email?.let { it1 -> showCollaboratorHome(it, it1) } }
        } else if (rol == Roles.PACIENTE.name) {
            uId?.let { email?.let { it1 -> showPatientHome(it, it1) } }
        }
    }

    private fun showAlert() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un Error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showPatientHome(uid: String, email: String) {
        val homeIntent = Intent(this, PatientHomeActivity::class.java).apply {
            putExtra("uId", uid)
            putExtra("email", email)
            putExtra("rol", Roles.PACIENTE.name)
        }
        startActivity(homeIntent)
    }

    private fun showCollaboratorHome(uid: String, email: String) {
        val listIntent = Intent(this, CollaboratorHomeActivity::class.java).apply {
            putExtra("uId", uid)
            putExtra("email", email)
            putExtra("rol", Roles.COLABORADOR.name)
        }
        startActivity(listIntent)
    }

    companion object {
        private const val TAG = "AuthActivity"
    }
}