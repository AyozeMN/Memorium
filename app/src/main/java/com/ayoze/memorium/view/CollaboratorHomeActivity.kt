package com.ayoze.memorium.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.ayoze.memorium.R
import com.ayoze.memorium.databinding.ActivityCollaboratorHomeBinding
import com.google.firebase.auth.FirebaseAuth

class CollaboratorHomeActivity : ComponentActivity() {

    private lateinit var binding: ActivityCollaboratorHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollaboratorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup
        val bundle: Bundle? = intent.extras
        val uId: String? = bundle?.getString("uId")
        val email: String? = bundle?.getString("email")
        val rol: String? = bundle?.getString("rol")

        setUp(uId, rol, email ?: "")

        // Guardado de datos en la Sesion
        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("uId", uId)
        prefs.putString("rol", rol)
        prefs.apply()
    }

    private fun setUp(uId: String?, rol: String?, email: String) {
        title = "Inicio"

        binding.tvUserName.text = "Bienvenido $email"

        binding.btnListQuestions.setOnClickListener {
            showListQuestion(uId)
        }

        binding.btnListPatients.setOnClickListener {
            showListPatient(uId)
        }

        binding.btnListQuiz.setOnClickListener {
            showListQuiz(uId, rol)
        }

        binding.btnLogOut.setOnClickListener {
            // Borrado de datos en la Sesion
            val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            // Cerrar sesion firebase
            FirebaseAuth.getInstance().signOut()
            // Volver a la pantalla anterior
            onBackPressed()
        }
    }

    private fun showListQuestion(uId: String?) {
        val listQuestionIntent = Intent(this, QuestionListActivity::class.java).apply {
            putExtra("uId", uId)
            putExtra("origen", "QUESTIONS")
        }
        startActivity(listQuestionIntent)
    }

    private fun showListPatient(uId: String?) {
        val listPatientIntent = Intent(this, PatientListActivity::class.java).apply {
            putExtra("uId", uId)
        }
        startActivity(listPatientIntent)
    }

    private fun showListQuiz(uId: String?, rol: String?) {
        val listQuizIntent = Intent(this, QuizListActivity::class.java).apply {
            putExtra("uId", uId)
            putExtra("rol", rol)
        }
        startActivity(listQuizIntent)
    }
}