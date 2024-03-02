package com.ayoze.memorium.viewHolder

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ayoze.memorium.databinding.ItemQuestionBinding
import com.ayoze.memorium.model.MultipleChoiceQuestion

class MultipleChoiceQuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemQuestionBinding.bind(view)
    val checkBox = binding.cbQuestion

    fun render(
        question: MultipleChoiceQuestion,
        origin: Int,
        onClickListener: (MultipleChoiceQuestion) -> Unit
    ) {
        binding.tvQuestionId.isVisible = false
        binding.tvQuestionId.text = question.id
        binding.tvQuestionStatement.text = question.statement

        when (origin) {
            0 -> renderFullItem(question)
            1 -> renderShortItem()
            else -> renderFullItem(question)
        }

        itemView.setOnClickListener { onClickListener(question) }
    }

    private fun renderFullItem(question: MultipleChoiceQuestion) {
        binding.tvOption1.text = question.options?.get(0)?.optionStatement.toString()
        binding.cbOption1.isChecked = question.options?.get(0)?.isCorrect() == true
        binding.cbOption1.isEnabled = false
        binding.tvOption2.text = question.options?.get(1)?.optionStatement.toString()
        binding.cbOption2.isChecked = question.options?.get(1)?.isCorrect() == true
        binding.cbOption2.isEnabled = false
        binding.tvOption3.text = question.options?.get(2)?.optionStatement.toString()
        binding.cbOption3.isChecked = question.options?.get(2)?.isCorrect() == true
        binding.cbOption3.isEnabled = false
        binding.tvOption4.text = question.options?.get(3)?.optionStatement.toString()
        binding.cbOption4.isChecked = question.options?.get(3)?.isCorrect() == true
        binding.cbOption4.isEnabled = false
    }

    private fun renderShortItem() {
        binding.cbQuestion.isVisible = true
        binding.sQuestion1.isVisible = false
        binding.llQuestionList.isVisible = false
    }
}