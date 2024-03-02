package com.ayoze.memorium.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayoze.memorium.R
import com.ayoze.memorium.databinding.ItemQuestionBinding
import com.ayoze.memorium.viewHolder.MultipleChoiceQuestionViewHolder
import com.ayoze.memorium.model.MultipleChoiceQuestion

class MultipleChoiceQuestionAdapter(
    private val questionList: List<MultipleChoiceQuestion>,
    private val origin: Int,
    private val onClickListener: (MultipleChoiceQuestion) -> Unit
) : RecyclerView.Adapter<MultipleChoiceQuestionViewHolder>() {

    private var checkBoxesSeleccionados = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultipleChoiceQuestionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MultipleChoiceQuestionViewHolder(
            layoutInflater.inflate(
                R.layout.item_question,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MultipleChoiceQuestionViewHolder, position: Int) {
        val item = questionList[position]
        holder.render(item, origin, onClickListener)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && checkBoxesSeleccionados >= 5) {
                holder.checkBox.isChecked = false
            } else {
                item.isSelected = isChecked
                checkBoxesSeleccionados += if (isChecked) 1 else -1
            }
        }
    }

    override fun getItemCount(): Int = questionList.size

    fun getQuestionsSelected(): List<MultipleChoiceQuestion> {
        return questionList.filter { it.isSelected == true }
    }

    fun getNumberOfQuestionsSelected(): Int {
        return checkBoxesSeleccionados
    }
}