package com.example.knowing_simple.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.data.service.QuizService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    private lateinit var quizService: QuizService
    private lateinit var questionTextView: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private var isAnswerVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // View 초기화
        questionTextView = findViewById(R.id.questionTextView)
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
        answerTextView = findViewById(R.id.answerTextView)
        showAnswerButton = findViewById(R.id.showAnswerButton)

        // QuizService 초기화
        val quizDao = QuizDatabase.getDatabase(this).quizDao()
        quizService = QuizService(quizDao)

        // 퀴즈 로드 및 첫 번째 퀴즈 표시
        CoroutineScope(Dispatchers.IO).launch {
            quizService.loadQuizzes()
            runOnUiThread { showQuiz() }
        }

        yesButton.setOnClickListener {
            onAnswerSelected(true)
        }

        noButton.setOnClickListener {
            onAnswerSelected(false)
        }

        showAnswerButton.setOnClickListener{
            toggleAnswerVisibility()
        }

    }


    private fun showQuiz() {
        val currentQuiz = quizService.getCurrentQuiz()
        questionTextView.text = currentQuiz.question
        answerTextView.text = currentQuiz.answer
        answerTextView.visibility = View.GONE
        isAnswerVisible = false
        showAnswerButton.text = "답 확인"
    }

    private fun toggleAnswerVisibility() {
        if (isAnswerVisible) {
            answerTextView.visibility = View.GONE
            showAnswerButton.text = "답 확인"
        } else {
            answerTextView.visibility = View.VISIBLE
            showAnswerButton.text = "답 숨기기"
        }
        isAnswerVisible = !isAnswerVisible
    }

    private fun onAnswerSelected(isKnown: Boolean) {
        quizService.updateQuizStatus(isKnown)
        if (!quizService.moveToNextQuiz()) {
            CoroutineScope(Dispatchers.IO).launch {
                quizService.saveQuizStatus()
                runOnUiThread{ showResult() }
            }
        } else {
            showQuiz()
        }
    }

    private fun showResult() {
        val intent = Intent(this, QuizResultActivity::class.java).apply {
            val (knownCount, totalCount) = quizService.getResult()
            putExtra("known", knownCount)
            putExtra("totalCount", totalCount)
        }
        startActivity(intent)
        finish()
    }
}