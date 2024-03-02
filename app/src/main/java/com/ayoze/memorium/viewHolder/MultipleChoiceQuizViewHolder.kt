package com.ayoze.memorium.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ayoze.memorium.R
import com.ayoze.memorium.databinding.ItemQuizBinding
import com.ayoze.memorium.model.MultipleChoiceQuiz
import com.bumptech.glide.Glide

class MultipleChoiceQuizViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemQuizBinding.bind(view)

    fun render(
        quiz: MultipleChoiceQuiz, onClickListener: (MultipleChoiceQuiz) -> Unit
    ) {

        if (quiz.image.isNullOrEmpty() || quiz.image.equals("null") || quiz.image.equals("")) {
            Glide.with(binding.ivQuiz.context).load(R.drawable.memorium_whitebg_l)
                .into(binding.ivQuiz)
        } else {
            Glide.with(binding.ivQuiz.context).load(quiz.image).into(binding.ivQuiz)
        }

        if (quiz.title.isNullOrEmpty() || quiz.title.equals("null") || quiz.title.equals("")) {
            binding.tvQuizStatement.text = "Sin título"
        } else {
            binding.tvQuizStatement.text = quiz.title
        }

        if (quiz.questions.isNullOrEmpty()) {
            binding.tvQuizQuestionNumber.text = 0.toString()
        } else {
            binding.tvQuizQuestionNumber.text = quiz.questions!!.size.toString()
        }

        if (quiz.difficulty.isNullOrEmpty() || quiz.difficulty.equals("")) {
            binding.tvQuizDifficulty.text = "No especificada"
        } else {
            binding.tvQuizDifficulty.text = quiz.difficulty
        }

        itemView.setOnClickListener { onClickListener(quiz) }
    }
}