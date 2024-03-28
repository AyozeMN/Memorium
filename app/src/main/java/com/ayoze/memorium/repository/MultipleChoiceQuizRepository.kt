package com.ayoze.memorium.repository

import android.util.Log
import com.ayoze.memorium.model.MultipleChoiceQuestion
import com.ayoze.memorium.model.MultipleChoiceQuiz
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class MultipleChoiceQuizRepository(private val listener: OnQuizzesFetchedListener) {

    interface OnQuizzesFetchedListener {
        fun onQuizzesFetched(quizzes: List<MultipleChoiceQuiz>)
        fun onFetchError(errorMessage: String)
    }

    suspend fun fetchAllQuizzes() {
        withContext(Dispatchers.IO) {
            val quizzes = mutableListOf<MultipleChoiceQuiz>()

            try {
                val collectionReference =
                    FirebaseFirestore.getInstance().collection("quizzes")
                val querySnapshot = collectionReference.get().await()

                for (doc in querySnapshot.documents) {
                    val quiz = MultipleChoiceQuiz()
                    quiz.id = doc.getString("id")
                    quiz.title = doc.getString("title")
                    quiz.image = doc.getString("image")
                    quiz.difficulty = doc.getString("difficulty")
                    val questionList = ArrayList<MultipleChoiceQuestion>()
                    val questions = doc.get("questions") as Map<*, *>
                    questions.forEach { question ->
                        val questionId = question.value.toString()
                        questionList.add(
                            MultipleChoiceQuestion(questionId, null, null, null)
                        )
                    }
                    quiz.questions = questionList
                    quizzes.add(quiz)
                    Log.d(TAG, "Quiz added $quiz")
                }
                withContext(Dispatchers.Main) {
                    listener.onQuizzesFetched(quizzes)
                    Log.i(TAG, "All Quizzes fetched $quizzes")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    listener.onFetchError(e.message ?: "Error fetching all quizzes.")
                }
            }
        }
    }

    suspend fun fetchQuizFromId(quizId: String) {
        withContext(Dispatchers.IO) {
            val quizzes = ArrayList<MultipleChoiceQuiz>()
            val quiz = MultipleChoiceQuiz()
            try {
                // Obtén la referencia a la colección multipleChoiceQuiz
                val collectionReference =
                    FirebaseFirestore.getInstance().collection("quizzes")
                        .document(quizId)
                // Consulta
                val querySnapshot = collectionReference.get().await()
                quiz.id = querySnapshot.getString("id")
                quiz.title = querySnapshot.getString("title")
                quiz.image = querySnapshot.getString("image")
                quiz.difficulty = querySnapshot.getString("difficulty")
                val questionList = ArrayList<MultipleChoiceQuestion>()
                Log.i(TAG, "Quiz Questions: ${querySnapshot.get("questions")}")
                val questions = querySnapshot.get("questions") as Map<*, *>
                questions.forEach { question ->
                    val questionId = question.value.toString()
                    questionList.add(
                        MultipleChoiceQuestion(questionId, null, null, null)
                    )
                }
                quiz.questions = questionList
                quizzes.add(quiz)
                withContext(Dispatchers.Main) {
                    listener.onQuizzesFetched(quizzes)
                    Log.i(TAG, "Quizzes fetched $quizzes")
                }
            } catch (e: Exception) {
                // Maneja errores si la tarea no se completó correctamente
                withContext(Dispatchers.Main) {
                    listener.onFetchError(e.message ?: "Error fetching quizzes.")
                }
            }
        }
    }

    companion object {
        private const val TAG = "MCQuizRespository"
    }
}