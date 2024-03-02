package com.ayoze.memorium.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayoze.memorium.adapter.MultipleChoiceQuestionAdapter
import com.ayoze.memorium.databinding.ActivityCreateQuizBinding
import com.ayoze.memorium.databinding.ItemQuestionBinding
import com.ayoze.memorium.model.MultipleChoiceQuestion
import com.ayoze.memorium.model.MultipleChoiceQuiz
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class CreateQuizActivity : ComponentActivity() {

    private var db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var bindingItem: ItemQuestionBinding
    private lateinit var questionList: ArrayList<MultipleChoiceQuestion>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MultipleChoiceQuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        bindingItem = ItemQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup
        val bundle: Bundle? = intent.extras
        val uId: String? = bundle?.getString("uId")

        setUp(uId)

        initRecyclerView(uId)
    }

    private fun initRecyclerView(uId: String?) {
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)
        binding.rvQuestionList.layoutManager = manager

        questionList = arrayListOf()

        recyclerView = binding.rvQuestionList
        adapter = MultipleChoiceQuestionAdapter(questionList, 1) { onItemSelected(it) }
        recyclerView.adapter = adapter

        binding.rvQuestionList.adapter = recyclerView.adapter
        binding.rvQuestionList.addItemDecoration(decoration)

        eventChangeListener()

        binding.btnCQ.setOnClickListener {
            if (adapter.getNumberOfQuestionsSelected() == 5) {
                val questionsSelected = adapter.getQuestionsSelected()
                for (question in questionsSelected) {
                    Log.d("Pregunta ", "${question.id} seleccionada")
                }
                val newQuiz = MultipleChoiceQuiz(
                    null,
                    binding.tvTitleCreateQuiz.text.toString(),
                    null,
                    binding.etDifficultyCQ.text.toString(),
                    questionsSelected
                )
                val emptyQuiz = MultipleChoiceQuiz()

                db.collection("multipleChoiceQuiz").add(emptyQuiz)
                    .addOnSuccessListener { documentReference ->
                        emptyQuiz.id = documentReference.id

                        db.collection("multipleChoiceQuiz").document(documentReference.id).set(
                            hashMapOf(
                                "id" to documentReference.id,
                                "title" to newQuiz.title,
                                "image" to newQuiz.image,
                                "difficulty" to newQuiz.difficulty,
                                "questions" to hashMapOf(
                                    "q1" to (newQuiz.questions?.get(0)?.id ?: ""),
                                    "q2" to (newQuiz.questions?.get(1)?.id ?: ""),
                                    "q3" to (newQuiz.questions?.get(2)?.id ?: ""),
                                    "q4" to (newQuiz.questions?.get(3)?.id ?: ""),
                                    "q5" to (newQuiz.questions?.get(4)?.id ?: "")
                                )
                            )
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                // Log en caso de éxito al crear un test
                                Log.w(TAG, "createQuiz:success")
                                // Mostrar por pantalla retroalimentación al usuario
                                Toast.makeText(
                                    baseContext,
                                    "Test creado",
                                    Toast.LENGTH_LONG,
                                ).show()
                                // Navegación a la lista de preguntas
                                showListQuiz(uId)
                            } else {
                                // Log en caso de fallo al crear un test
                                Log.w(TAG, "createQuiz:failure", it.exception)
                                // Mostrar por pantalla retroalimentación al usuario
                                Toast.makeText(
                                    baseContext,
                                    "Autenticación fallida",
                                    Toast.LENGTH_LONG,
                                ).show()
                            }
                        }
                    }
            }
        }
    }

    private fun showListQuiz(uId: String?) {
        val listQuizIntent = Intent(this, QuizListActivity::class.java).apply {
            putExtra("uId", uId)
        }
        startActivity(listQuizIntent)
    }

    private fun setUp(uId: String?) {
        title = "Creación de Test de Respuesta Múltiple"
    }

    private fun eventChangeListener() {
        db.collection("question").addSnapshotListener(object :
            EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Log.e("Error Firestore", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val id: String = dc.document.get("id").toString()
                        val statement: String = dc.document.get("statement").toString()

                        val question = MultipleChoiceQuestion(
                            id,
                            statement
                        )

                        questionList.add(question)
                    }
                }

                binding.rvQuestionList.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun onItemSelected(question: MultipleChoiceQuestion) {
        Toast.makeText(this, question.statement.toString(), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "CreateQuizActivity"
    }

}