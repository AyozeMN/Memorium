package com.ayoze.memorium.repository

import android.util.Log
import com.ayoze.memorium.model.CheckOption
import com.ayoze.memorium.model.MultipleChoiceQuestion
import com.ayoze.memorium.model.MultipleChoiceQuiz
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MultipleChoiceQuestionRepository(private val listener: OnQuestionsFetchedListener) {

    interface OnQuestionsFetchedListener {
        fun onQuestionsFetched(questions: List<MultipleChoiceQuestion>)
        fun onQuestionsFromQuizFetched(quiz: MultipleChoiceQuiz, questions: List<MultipleChoiceQuestion>)
        fun onFetchError(errorMessage: String)
    }

    suspend fun fetchAllQuestions() {
        withContext(Dispatchers.IO) {
            val questions = mutableListOf<MultipleChoiceQuestion>()

            try {
                // Obtén la referencia a la colección "questions"
                val collectionReference = FirebaseFirestore.getInstance().collection("questions")
                // Realiza la consulta de manera asíncrona y Bloquea el hilo actual hasta que la tarea se complete
                val querySnapshot = collectionReference.get().await()

                for (doc in querySnapshot.documents) {
                    val id: String = doc.getString("id") ?: ""
                    val statement: String = doc.getString("statement") ?: ""
                    val option1 = CheckOption(
                        doc.getString("option1.statement"), doc.getBoolean("option1.isCorrect")
                    )
                    val option2 = CheckOption(
                        doc.getString("option2.statement"), doc.getBoolean("option2.isCorrect")
                    )
                    val option3 = CheckOption(
                        doc.getString("option3.statement"), doc.getBoolean("option3.isCorrect")
                    )
                    val option4 = CheckOption(
                        doc.getString("option4.statement"), doc.getBoolean("option4.isCorrect")
                    )
                    val optionsList: List<CheckOption> = listOf(option1, option2, option3, option4)
                    val question = MultipleChoiceQuestion(id, statement, optionsList)
                    questions.add(question)
                    Log.d(TAG, "Question added: $question")
                }
                withContext(Dispatchers.Main) {
                    listener.onQuestionsFetched(questions)
                    Log.i(TAG, "Questions fetched $questions")
                }
            } catch (e: Exception) {
                // Maneja errores si la tarea no se completó correctamente
                withContext(Dispatchers.Main) {
                    listener.onFetchError(e.message ?: "Error fetching questions.")
                }
            }
        }
    }

    suspend fun fetchQuestionsFromQuiz(quiz: MultipleChoiceQuiz) {
        withContext(Dispatchers.IO) {
            val questions = mutableListOf<MultipleChoiceQuestion>()
            val collectionReference = FirebaseFirestore.getInstance().collection("questions")
            try {
                quiz.questions?.forEach {
                    val doc = collectionReference.document(it.id).get().await()
                    val id: String = doc.getString("id") ?: ""
                    val statement: String = doc.getString("statement") ?: ""
                    val option1 = CheckOption(
                        doc.getString("option1.statement"), doc.getBoolean("option1.isCorrect")
                    )
                    val option2 = CheckOption(
                        doc.getString("option2.statement"), doc.getBoolean("option2.isCorrect")
                    )
                    val option3 = CheckOption(
                        doc.getString("option3.statement"), doc.getBoolean("option3.isCorrect")
                    )
                    val option4 = CheckOption(
                        doc.getString("option4.statement"), doc.getBoolean("option4.isCorrect")
                    )
                    val optionsList: List<CheckOption> = listOf(option1, option2, option3, option4)
                    val question = MultipleChoiceQuestion(id, statement, optionsList)
                    questions.add(question)
                    Log.d(TAG, "Question added: $question")
                }
                withContext(Dispatchers.Main) {
                    listener.onQuestionsFromQuizFetched(quiz, questions)
                    Log.i(TAG, "Questions fetched $questions")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onFetchError(e.message ?: "Error fetching questions from quiz.")
                }
            }
        }
    }

    companion object {
        private const val TAG = "MCQuestionRepository"
    }
}