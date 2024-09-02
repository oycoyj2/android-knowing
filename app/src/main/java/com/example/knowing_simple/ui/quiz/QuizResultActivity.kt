package com.example.knowing_simple.ui.quiz

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R

class QuizResultActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        resultTextView = findViewById(R.id.resultTextView)

        val knownCount = intent.getIntExtra("knownCount", 0)
        val totalCount = intent.getIntExtra("totalCount", 0)

        resultTextView.text = "너는 $totalCount 개 만큼 맞았어. $knownCount 개 중에 말이야!"
    }
}