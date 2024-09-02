package com.example.knowing_simple.ui.quiz.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.model.Quiz
import com.example.knowing_simple.ui.quiz.QuizDetailActivity

class QuizAdapter(private var quizList: List<Quiz>, private val context: Context) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(quizList[position])
    }

    override fun getItemCount(): Int {
        return quizList.size
    }

    fun updateData(newQuizList: List<Quiz>) {
        quizList = newQuizList
        notifyDataSetChanged()
    }

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionButton: Button = itemView.findViewById(R.id.questionButton)

        fun bind(quiz: Quiz) {
            questionButton.text = quiz.question
            questionButton.setOnClickListener {
                val intent = Intent(context, QuizDetailActivity::class.java)
                intent.putExtra("question", quiz.question)
                intent.putExtra("answer", quiz.answer)
                context.startActivity(intent)
            }
        }
    }
}