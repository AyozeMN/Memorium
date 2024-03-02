package com.ayoze.memorium.view

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.ayoze.memorium.R
import com.ayoze.memorium.databinding.ActivityDoQuizBinding
import com.ayoze.memorium.databinding.ActivityQuizResultBinding
import com.ayoze.memorium.model.MultipleChoiceQuestion

class DoQuizActivity : ComponentActivity(), View.OnClickListener {

    companion object {
        var questionList: List<MultipleChoiceQuestion> = listOf()
        var questionAnswer: ArrayList<Boolean> = arrayListOf()
    }

    private val TAG = "DoQuizActivity"
    private var currentQuestionIndex = 0
    private var limitCorrectAnswer = 0
    private var optionsClicked = 0
    private var selectedAnswers: ArrayList<Boolean> = arrayListOf()
    private var score = 0
    private var uId: String? = ""
    private var rol: String? = ""
    private var quizId: String? = ""

    private lateinit var binding: ActivityDoQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setup
        val bundle: Bundle? = intent.extras
        uId = bundle?.getString("uId")
        Log.i(TAG, "uId $uId")
        rol = bundle?.getString("rol")
        quizId = bundle?.getString("quizId")
        Log.i(TAG, "rol $rol")
        binding.apply {
            btnAnswer1.setOnClickListener(this@DoQuizActivity)
            btnAnswer2.setOnClickListener(this@DoQuizActivity)
            btnAnswer3.setOnClickListener(this@DoQuizActivity)
            btnAnswer4.setOnClickListener(this@DoQuizActivity)
            btnNext.setOnClickListener(this@DoQuizActivity)
        }
        Log.i(TAG, questionList.toString())
        loadQuestions()
        startTimer()
    }

    private fun startTimer() {
        val totalTimeInMillis = (5 * 60 * 1000L)
        object : CountDownTimer(totalTimeInMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                binding.tvTimerIndicator.text =
                    String.format("%02d:%02d", minutes, remainingSeconds)
            }

            override fun onFinish() {
                finishQuiz()
            }

        }.start()
    }

    private fun loadQuestions() {
        limitCorrectAnswer = 0
        optionsClicked = 0
        loadCorrectAnswers()
        clearSelectedAnswers()
        binding.apply {
            tvQuestionIndicator.text = "Pregunta ${currentQuestionIndex + 1} / ${questionList.size}"
            qpIndicator.progress =
                (currentQuestionIndex.toFloat() / questionList.size.toFloat() * 100).toInt()
            tvQuestion.text = questionList[currentQuestionIndex].statement
            btnAnswer1.text =
                questionList[currentQuestionIndex].options?.get(0)?.optionStatement ?: ""
            if (questionList[currentQuestionIndex].options?.get(0)
                    ?.isCorrect() == true
            ) limitCorrectAnswer++
            btnAnswer2.text =
                questionList[currentQuestionIndex].options?.get(1)?.optionStatement ?: ""
            if (questionList[currentQuestionIndex].options?.get(1)
                    ?.isCorrect() == true
            ) limitCorrectAnswer++
            btnAnswer3.text =
                questionList[currentQuestionIndex].options?.get(2)?.optionStatement ?: ""
            if (questionList[currentQuestionIndex].options?.get(2)
                    ?.isCorrect() == true
            ) limitCorrectAnswer++
            btnAnswer4.text =
                questionList[currentQuestionIndex].options?.get(3)?.optionStatement ?: ""
            if (questionList[currentQuestionIndex].options?.get(3)
                    ?.isCorrect() == true
            ) limitCorrectAnswer++
            btnAnswer1.isActivated = false
            btnAnswer2.isActivated = false
            btnAnswer3.isActivated = false
            btnAnswer4.isActivated = false
            btnAnswer1.setBackgroundResource(R.drawable.bg_answer)
            btnAnswer2.setBackgroundResource(R.drawable.bg_answer)
            btnAnswer3.setBackgroundResource(R.drawable.bg_answer)
            btnAnswer4.setBackgroundResource(R.drawable.bg_answer)
        }
    }

    private fun loadCorrectAnswers() {
        questionAnswer.clear()
        questionList[currentQuestionIndex].options?.forEach { option ->
            option.isCorrect()?.let { questionAnswer.add(it) }
        }

        Log.i(TAG, "questionAnswer $questionAnswer")
    }

    private fun clearSelectedAnswers() {
        selectedAnswers.clear()
        selectedAnswers.add(0, false)
        selectedAnswers.add(1, false)
        selectedAnswers.add(2, false)
        selectedAnswers.add(3, false)
    }

    private fun finishQuiz() {
        val totalQuestions = questionList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat()) * 100).toInt()

        val dialogBinding = ActivityQuizResultBinding.inflate(layoutInflater)
        dialogBinding.apply {
            piQuizScore.progress = percentage
            tvQuizScore.text = "$percentage%"
            if (percentage > 50) {
                tvQuizResultTitle.text = "¡Enhorabuena! Has pasado el test"
                tvQuizResultTitle.setTextColor(Color.BLUE)
            } else {
                tvQuizResultTitle.text = "¡Vaya! Inténtalo de nuevo"
                tvQuizResultTitle.setTextColor(Color.RED)
            }
            tvQuizResultCount.text = totalQuestions.toString()
            tvQuizRightCount.text = score.toString()
            tvQuizWrongCount.text = (totalQuestions - score).toString()
        }

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .show()

        dialogBinding.btnFinish.setOnClickListener {
            alertDialog.dismiss()
            closeActivity()
        }
    }

    private fun closeActivity() {
        val quizListActivity = Intent(this, QuizListActivity::class.java).apply {
            putExtra("uId", uId)
            putExtra("rol", rol)
        }
        quizListActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(quizListActivity)
        finish()
    }

    override fun onClick(v: View?) {
        val clickedButton = v as Button
        if (clickedButton.id == binding.btnNext.id) {
            if (currentQuestionIndex <= questionList.size - 1) {
                Log.i(TAG, "selectedAnswersNext $selectedAnswers")
                Log.i(TAG, "questionAnswerNext $questionAnswer")
                if (questionAnswer == selectedAnswers) {
                    score++
                    Toast.makeText(this, "Respuesta correcta", Toast.LENGTH_SHORT).show()
                }
                if (currentQuestionIndex == questionList.size - 1) {
                    finishQuiz()
                    return
                }
                currentQuestionIndex++
                loadQuestions()
            }
        } else {
            if (clickedButton.isActivated) {
                Log.i(TAG, "unclicked")
                when (resources.getResourceEntryName(clickedButton.id)) {
                    "btnAnswer1" -> {
                        selectedAnswers.removeAt(0)
                        selectedAnswers.add(0, false)
                    }

                    "btnAnswer2" -> {
                        selectedAnswers.removeAt(1)
                        selectedAnswers.add(1, false)
                    }

                    "btnAnswer3" -> {
                        selectedAnswers.removeAt(2)
                        selectedAnswers.add(2, false)
                    }

                    "btnAnswer4" -> {
                        selectedAnswers.removeAt(3)
                        selectedAnswers.add(3, false)
                    }
                }
                Log.i(TAG, "selectedAnswersChanged $selectedAnswers")
                optionsClicked--
                clickedButton.isActivated = false
                clickedButton.setBackgroundResource(R.drawable.bg_answer)
            } else if (optionsClicked < limitCorrectAnswer) {
                Log.i(TAG, "clicked")
                Log.i(TAG, "clickedButton ${resources.getResourceEntryName(clickedButton.id)}")
                when (resources.getResourceEntryName(clickedButton.id)) {
                    "btnAnswer1" -> {
                        selectedAnswers.removeAt(0)
                        selectedAnswers.add(0, true)
                    }

                    "btnAnswer2" -> {
                        selectedAnswers.removeAt(1)
                        selectedAnswers.add(1, true)
                    }

                    "btnAnswer3" -> {
                        selectedAnswers.removeAt(2)
                        selectedAnswers.add(2, true)
                    }

                    "btnAnswer4" -> {
                        selectedAnswers.removeAt(3)
                        selectedAnswers.add(3, true)
                    }
                }
                Log.i(TAG, "selectedAnswersChanged $selectedAnswers")
                clickedButton.id
                optionsClicked++
                clickedButton.isActivated = true
                clickedButton.setBackgroundResource(R.drawable.bg_answer_clicked)
            }
        }
    }
}