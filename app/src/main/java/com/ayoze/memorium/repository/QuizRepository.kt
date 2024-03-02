package com.ayoze.memorium.repository

class QuizRepository(private val listener: OnQuizActionPerformedListener) {

    interface OnQuizActionPerformedListener {
        fun onQuizCreatedPerformed()
    }

    suspend fun createQuiz() {

    }
}