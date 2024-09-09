package com.example.knowing_simple.ui.quiz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R

class QuizResultActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        resultTextView = findViewById(R.id.resultTextView)
        confirmButton = findViewById(R.id.confirmButton)

        val knownCount = intent.getIntExtra("knownCount", 0)
        val totalCount = intent.getIntExtra("totalCount", 0)

        resultTextView.text = "문제 개수: $totalCount\n맞춘 문제: $knownCount"

        confirmButton.setOnClickListener {
            finish() // 현재 액티비티 종료하고 이전 화면으로 돌아가기
        }
    }


}