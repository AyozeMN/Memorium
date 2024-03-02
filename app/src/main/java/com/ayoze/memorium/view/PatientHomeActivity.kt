package com.ayoze.memorium.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.ayoze.memorium.R
import com.ayoze.memorium.databinding.ActivityPatientHomeBinding
import com.google.firebase.auth.FirebaseAuth

class PatientHomeActivity : ComponentActivity() {

    private lateinit var binding: ActivityPatientHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup
        val bundle: Bundle? = intent.extras
        val uId: String? = bundle?.getString("uId")
        val email: String? = bundle?.getString("email")
        val rol: String? = bundle?.getString("rol")

        setUp(email ?: "", uId ?: "", rol ?: "")

        // Guardado de datos en la Sesion
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("uId", uId)
        prefs.putString("email", email)
        prefs.putString("rol", rol)
        prefs.apply()
    }

    private fun setUp(email: String, uId: String, rol: String) {

        title = "Inicio"

        binding.tvUserName.text = "Bienvenido $email"

        binding.btnDoQuiz.setOnClickListener {
            showListQuiz(uId, rol)
        }

        //binding.btnStats.setOnClickListener {  }

        binding.btnLogOut.setOnClickListener {
            // Borrado de datos en la Sesion
            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            // Cerrar sesion firebase
            FirebaseAuth.getInstance().signOut()
            // Volver a la pantalla anterior
            onBackPressed()
        }
    }

    private fun showListQuiz(uId: String, rol: String?) {
        val listQuizIntent = Intent(this, QuizListActivity::class.java).apply {
            putExtra("uId", uId)
            putExtra("rol", rol)
        }
        startActivity(listQuizIntent)
    }
}