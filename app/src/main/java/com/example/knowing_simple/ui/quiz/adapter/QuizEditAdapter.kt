package com.example.knowing_simple.ui.quiz.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.model.Quiz

class QuizEditAdapter(private var quizList: List<Quiz>, private val context: Context) : RecyclerView.Adapter<QuizEditAdapter.QuizEditViewHolder>() {

    private val selectedQuizIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizEditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz_edit, parent, false)
        return QuizEditViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizEditViewHolder, position: Int) {
        holder.bind(quizList[position])
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    fun updateData(newQuizList: List<Quiz>) {
        quizList = newQuizList
        notifyDataSetChanged()
    }

    fun getSelectedQuizIds(): Set<Int> {
        return selectedQuizIds
    }

    fun selectAll() {
        selectedQuizIds.clear()
        quizList.forEach { selectedQuizIds.add(it.id) }
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedQuizIds.clear()
        notifyDataSetChanged()
    }

    inner class QuizEditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deleteCheckBox: CheckBox = itemView.findViewById(R.id.deleteCheckBox)
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)

        fun bind(quiz: Quiz) {
            questionTextView.text = quiz.question

            deleteCheckBox.setOnCheckedChangeListener(null) // 이전 상태를 초기화
            deleteCheckBox.isChecked = selectedQuizIds.contains(quiz.id)

            itemView.setOnClickListener{
                val isChecked = !deleteCheckBox.isChecked
                deleteCheckBox.isChecked = isChecked
                if (isChecked) {
                    selectedQuizIds.add(quiz.id)
                } else {
                    selectedQuizIds.remove(quiz.id)
                }
            }

            deleteCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedQuizIds.add(quiz.id)
                } else {
                    selectedQuizIds.remove(quiz.id)
                }
            }
        }
    }
}
