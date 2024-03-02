package com.ayoze.memorium.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayoze.memorium.adapter.MultipleChoiceQuestionAdapter
import com.ayoze.memorium.databinding.ActivityQuestionListBinding
import com.ayoze.memorium.model.MultipleChoiceQuestion
import com.ayoze.memorium.model.MultipleChoiceQuiz
import com.ayoze.memorium.repository.MultipleChoiceQuestionRepository
import com.ayoze.memorium.repository.MultipleChoiceQuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuestionListActivity : ComponentActivity() {

    private lateinit var binding: ActivityQuestionListBinding
    private var questionList = mutableListOf<MultipleChoiceQuestion>()
    private var quizList = mutableListOf<MultipleChoiceQuiz>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setup
        val bundle: Bundle? = intent.extras
        val uId: String? = bundle?.getString("uId")
        val origen: String? = bundle?.getString("origen")
        val quizId: String? = bundle?.getString("quizId")
        setUp(uId)
        initRecyclerView(origen, quizId)
    }

    private val questionRepository = MultipleChoiceQuestionRepository(object :
        MultipleChoiceQuestionRepository.OnQuestionsFetchedListener {
        override fun onQuestionsFetched(questions: List<MultipleChoiceQuestion>) {
            updateRecyclerView(questions)
            Log.d(TAG, "Questions fetched: $questions")
        }

        override fun onQuestionsFromQuizFetched(
            quiz: MultipleChoiceQuiz,
            questions: List<MultipleChoiceQuestion>
        ) {
            updateRecyclerView(questions)
            Log.d(TAG, "Questions from quiz fetched: $questions")
        }

        override fun onFetchError(errorMessage: String) {
            // Aquí puedes manejar el error al obtener las preguntas
            Log.i(TAG, "ERROR AL OBTENER LAS PREGUNTAS")
        }
    })

    private val quizRepository = MultipleChoiceQuizRepository(object :
        MultipleChoiceQuizRepository.OnQuizzesFetchedListener {
        override fun onQuizzesFetched(quizzes: List<MultipleChoiceQuiz>) {
            questionList.clear()
            questionList.addAll(quizzes[0].questions!!)
            quizList.clear()
            quizList.addAll(quizzes)
            Log.d(TAG, "Quizzes fetched: $quizzes")
            Log.d(TAG, "Questions fetched from test: " + quizzes[0].questions.toString())
        }

        override fun onFetchError(errorMessage: String) {
            // Aquí puedes manejar el error al obtener las preguntas
            Log.i(TAG, "ERROR AL OBTENER LAS PREGUNTAS DESDE UN TEST")
        }

    })

    private fun initRecyclerView(origen: String?, quizId: String?) {
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)
        binding.rvQuestion.layoutManager = manager
        val adapter = MultipleChoiceQuestionAdapter(questionList, 0) { onItemSelected(it) }
        binding.rvQuestion.adapter = adapter
        binding.rvQuestion.addItemDecoration(decoration)
        eventChangeListener(origen, quizId)
    }

    private fun eventChangeListener(origen: String?, quizId: String?) {
        if (origen.equals("QUESTIONS")) {
            binding.llQuizQuestions.isVisible = false
            // Llama a fetchQuestions con el nombre de la colección deseada
            lifecycleScope.launch {
                // Llama a la función suspend usando async
                val result = lifecycleScope.async {
                    questionRepository.fetchAllQuestions()
                }
                // Puedes hacer más cosas después de que la función suspend se complete, si es necesario
                // Espera a que la función suspend termine antes de continuar con el código
                result.await()
                // Más código después de que la función suspend haya terminado
                Log.i(TAG, "fetchAllQuestions: $questionList")
            }

        } else if (origen.equals("QUIZQUESTIONS") && !quizId.isNullOrEmpty()) {
            binding.llCreateQuestion.isVisible = false

            lifecycleScope.launch {
                val resultQuiz = lifecycleScope.async {
                    quizRepository.fetchQuizFromId(quizId)
                }
                resultQuiz.await()
                val resultQuestion = lifecycleScope.async {
                    questionRepository.fetchQuestionsFromQuiz(quizList[0])
                }
                resultQuestion.await()
                Log.i(TAG, "fetchQuizFromId: $questionList")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(newQuestions: List<MultipleChoiceQuestion>) {
        // Actualiza la lista de preguntas en el adaptador
        lifecycleScope.launch(Dispatchers.Main) {
            (binding.rvQuestion.adapter as? MultipleChoiceQuestionAdapter)?.apply {
                questionList.clear()
                questionList.addAll(newQuestions)
                notifyDataSetChanged()
                Log.i(TAG, "questionsChanged: $questionList")
            }
        }
    }

    private fun onItemSelected(question: MultipleChoiceQuestion) {
        Toast.makeText(this, question.statement.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun setUp(uId: String?) {
        binding.btnToCQ.setOnClickListener {
            showCreateQuestion(uId)
        }
    }

    private fun showCreateQuestion(uId: String?) {
        val createQuestionIntent = Intent(this, CreateQuestionActivity::class.java).apply {
            putExtra("uId", uId)
        }
        startActivity(createQuestionIntent)
    }

    companion object {
        private const val TAG = "QuestionListActivity"
    }
}