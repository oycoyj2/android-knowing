package com.example.knowing_simple.ui.quiz

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R

class QuizDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_detail)

        val questionTextView: TextView = findViewById(R.id.questionTextView)
        val answerTextView: TextView = findViewById(R.id.answerTextView)

        // 인텐트로부터 문제와 답변 데이터를 받아옴
        val question = intent.getStringExtra("question")
        val answer = intent.getStringExtra("answer")

        // 데이터를 화면에 표시
        questionTextView.text = question
        answerTextView.text = answer
    }
}