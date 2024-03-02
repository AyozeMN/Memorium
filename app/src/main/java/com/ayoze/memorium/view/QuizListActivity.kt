package com.ayoze.memorium.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayoze.memorium.adapter.MultipleChoiceQuizAdapter
import com.ayoze.memorium.databinding.ActivityQuizListBinding
import com.ayoze.memorium.model.MultipleChoiceQuestion
import com.ayoze.memorium.model.MultipleChoiceQuiz
import com.ayoze.memorium.model.Roles
import com.ayoze.memorium.repository.MultipleChoiceQuestionRepository
import com.ayoze.memorium.repository.MultipleChoiceQuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuizListActivity : ComponentActivity() {

    private lateinit var binding: ActivityQuizListBinding
    private lateinit var quizList: ArrayList<MultipleChoiceQuiz>
    private lateinit var questionList: ArrayList<MultipleChoiceQuestion>
    private var uId: String? = ""
    private var rol: String? = ""

    override fun onStart() {
        super.onStart()
        binding.llQuestionList.visibility = View.INVISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup
        val bundle: Bundle? = intent.extras
        uId = bundle?.getString("uId")
        Log.i(TAG, "uId $uId")
        rol = bundle?.getString("rol")
        Log.i(TAG, "rol $rol")

        initRecyclerView()

        setUp(uId, rol)
    }

    private val quizRepository = MultipleChoiceQuizRepository(object :
        MultipleChoiceQuizRepository.OnQuizzesFetchedListener {
        override fun onQuizzesFetched(quizzes: List<MultipleChoiceQuiz>) {
            updateRecyclerView(quizzes)
            getQuizzesQuestions(quizzes)
            Log.d(TAG, "Quizzes fetched: $quizzes")
        }

        override fun onFetchError(errorMessage: String) {
            Log.i(TAG, "ERROR AL OBTENER LOS QUIZZES $errorMessage")
        }
    })

    private val questionRepository = MultipleChoiceQuestionRepository(object :
        MultipleChoiceQuestionRepository.OnQuestionsFetchedListener {
        override fun onQuestionsFetched(questions: List<MultipleChoiceQuestion>) {

            Log.d(TAG, "Questions fetched: $questions")
        }

        override fun onQuestionsFromQuizFetched(
            quiz: MultipleChoiceQuiz,
            questions: List<MultipleChoiceQuestion>
        ) {
            updateQuiz(quiz, questions)
            Log.d(TAG, "Questions from quiz fetched: $questions")
        }

        override fun onFetchError(errorMessage: String) {
            Log.i(TAG, "ERROR AL OBTENER LAS PREGUNTAS $errorMessage")
        }
    })

    @SuppressLint("NotifyDataSetChanged")
    private fun updateQuiz(quiz: MultipleChoiceQuiz, questions: List<MultipleChoiceQuestion>) {
        (binding.rvQuizzes.adapter as? MultipleChoiceQuizAdapter)?.apply {
            quizList.forEach {
                if (it.id == quiz.id) {
                    it.questions = questions
                }
            }
            notifyDataSetChanged()
            Log.i(TAG, "updateQuiz quizzesChanged: $quizList")
        }
    }

    private fun getQuizzesQuestions(quizzes: List<MultipleChoiceQuiz>) {
        lifecycleScope.launch {
            val resultQuestion = lifecycleScope.async {
                quizzes.forEach {
                    questionRepository.fetchQuestionsFromQuiz(it)
                }
            }
            resultQuestion.await()
            Log.i(TAG, "Questions fetched from quizzes $quizzes")
        }
    }

    private fun setUp(uId: String?, rol: String?) {
        if (uId != null && rol != null) {
            binding.btnToCQ.isVisible = rol == Roles.COLABORADOR.name
        }

        binding.btnToCQ.setOnClickListener {
            showCreateQuiz(uId)
        }
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)
        binding.rvQuizzes.layoutManager = manager

        quizList = arrayListOf()
        questionList = arrayListOf()

        binding.rvQuizzes.adapter = MultipleChoiceQuizAdapter(quizList) { onItemSelected(it) }
        binding.rvQuizzes.addItemDecoration(decoration)

        eventChangeListener()
    }

    private fun eventChangeListener() {
        lifecycleScope.launch {
            val result = lifecycleScope.async {
                quizRepository.fetchAllQuizzes()
            }
            result.await()
            Log.i(TAG, "fetchAllQuizzes: $quizList")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView(newQuizzes: List<MultipleChoiceQuiz>) {
        lifecycleScope.launch(Dispatchers.Main) {
            (binding.rvQuizzes.adapter as? MultipleChoiceQuizAdapter)?.apply {
                quizList.clear()
                quizList.addAll(newQuizzes)
                notifyDataSetChanged()
                Log.i(TAG, "quizzesChanged: $quizList")
            }
        }
        binding.llQuestionList.visibility = View.VISIBLE
    }

    private fun onItemSelected(quiz: MultipleChoiceQuiz) {
        if (rol.equals(Roles.COLABORADOR.name)) {
            showQuestionList(uId, quiz.id)
            finish()
        } else {
            showDoQuiz(uId, quiz.id)
            DoQuizActivity.questionList = quiz.questions!!
            Toast.makeText(this, quiz.title.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCreateQuiz(uId: String?) {
        val createQuizIntent = Intent(this, CreateQuizActivity::class.java).apply {
            putExtra("uId", uId)
        }
        startActivity(createQuizIntent)
    }

    private fun showQuestionList(uId: String?, quizId: String?) {
        val listQuestionIntent = Intent(this, QuestionListActivity::class.java).apply {
            putExtra("uId", uId)
            putExtra("quizId", quizId)
            putExtra("origen", "QUIZQUESTIONS")
        }
        startActivity(listQuestionIntent)
    }

    private fun showDoQuiz(uId: String?, quizId: String?) {
        val doQuizIntent = Intent(this, DoQuizActivity::class.java).apply {
            putExtra("uId", uId)
            putExtra("rol", rol)
            putExtra("quizId", quizId)
        }
        doQuizIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(doQuizIntent)
    }

    companion object {
        private const val TAG = "QuizListActivity"
    }
}