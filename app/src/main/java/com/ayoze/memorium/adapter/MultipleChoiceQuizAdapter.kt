package com.ayoze.memorium.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayoze.memorium.R
import com.ayoze.memorium.model.MultipleChoiceQuiz
import com.ayoze.memorium.viewHolder.MultipleChoiceQuizViewHolder

class MultipleChoiceQuizAdapter(
    private val quizList: List<MultipleChoiceQuiz>,
    private val onClickListener: (MultipleChoiceQuiz) -> Unit
) : RecyclerView.Adapter<MultipleChoiceQuizViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MultipleChoiceQuizViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MultipleChoiceQuizViewHolder(
            layoutInflater.inflate(
                R.layout.item_quiz,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MultipleChoiceQuizViewHolder, position: Int) {
        val item = quizList[position]
        holder.render(item, onClickListener)
    }

    override fun getItemCount(): Int = quizList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}