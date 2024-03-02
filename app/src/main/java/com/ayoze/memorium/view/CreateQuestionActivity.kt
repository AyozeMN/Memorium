package com.ayoze.memorium.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.ayoze.memorium.databinding.ActivityCreateQuestionBinding
import com.ayoze.memorium.model.MultipleChoiceQuestion
import com.ayoze.memorium.util.Helper
import com.google.firebase.firestore.FirebaseFirestore

class CreateQuestionActivity : ComponentActivity() {

    private lateinit var binding: ActivityCreateQuestionBinding
    private var helper = Helper()
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup
        val bundle: Bundle? = intent.extras
        val uId: String? = bundle?.getString("uId")

        setUp(uId)
    }

    private fun setUp(uId: String?) {
        title = "Creación de Pregunta de Respuesta Múltiple"

        binding.btnCQ.setOnClickListener {
            //Comprobación campos de texto tienen que estar rellenos
            val textFieldsFilled: Boolean = (helper.checkTextFields(
                arrayOf(
                    binding.etStatementCQ,
                    binding.etOption1CQ,
                    binding.etOption2CQ,
                    binding.etOption3CQ,
                    binding.etOption4CQ
                )
            ))
            //Comprobación checks de opción correcta alguno tiene que estar activo
            val checkFieldsFilled: Boolean = (helper.checkCBoxes(
                arrayOf(
                    binding.cbOption1CQ,
                    binding.cbOption2CQ,
                    binding.cbOption3CQ,
                    binding.cbOption4CQ
                )
            ))

            if (textFieldsFilled && checkFieldsFilled) {
                val newQuestion = MultipleChoiceQuestion()

                db.collection("question").add(newQuestion)
                    .addOnSuccessListener { documentReference ->
                        newQuestion.id = documentReference.id

                        db.collection("question").document(documentReference.id).set(
                            hashMapOf(
                                "id" to documentReference.id,
                                "statement" to binding.etStatementCQ.text.toString(),
                                "option1" to hashMapOf(
                                    "statement" to binding.etOption1CQ.text.toString(),
                                    "isCorrect" to binding.cbOption1CQ.isChecked
                                ),
                                "option2" to hashMapOf(
                                    "statement" to binding.etOption2CQ.text.toString(),
                                    "isCorrect" to binding.cbOption2CQ.isChecked
                                ),
                                "option3" to hashMapOf(
                                    "statement" to binding.etOption3CQ.text.toString(),
                                    "isCorrect" to binding.cbOption3CQ.isChecked
                                ),
                                "option4" to hashMapOf(
                                    "statement" to binding.etOption4CQ.text.toString(),
                                    "isCorrect" to binding.cbOption4CQ.isChecked
                                )
                            )
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                // Log en caso de éxito al crear una pregunta
                                Log.w(TAG, "createQuestion:success")
                                // Mostrar por pantalla retroalimentación al usuario
                                Toast.makeText(
                                    baseContext,
                                    "Pregunta creada",
                                    Toast.LENGTH_LONG,
                                ).show()
                                // Navegación a la lista de preguntas
                                showListQuestion(uId)
                            } else {
                                // Log en caso de fallo al crear una pregunta
                                Log.w(TAG, "createQuestion:failure", it.exception)
                                // Mostrar por pantalla retroalimentación al usuario
                                Toast.makeText(
                                    baseContext,
                                    "Autenticación fallida",
                                    Toast.LENGTH_LONG,
                                ).show()
                            }
                        }
                    }
            } else if (!checkFieldsFilled) {
                binding.cbOption1CQ.error = "Tiene que seleccionar al menos una opción correcta"
            }
        }
    }

    private fun showListQuestion(uId: String?) {
        val listQuestionIntent = Intent(this, QuestionListActivity::class.java).apply {
            putExtra("uId", uId)
        }
        startActivity(listQuestionIntent)
    }

    companion object {
        private const val TAG = "CreateQuestionActivity"
    }
}