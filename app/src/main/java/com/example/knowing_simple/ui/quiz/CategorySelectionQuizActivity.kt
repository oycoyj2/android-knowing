package com.example.knowing_simple.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.data.service.CategorySelectionQuizService
import com.example.knowing_simple.data.service.SingleCategoryQuizService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategorySelectionQuizActivity : AppCompatActivity() {

    private lateinit var quizService: CategorySelectionQuizService
    private lateinit var questionTextView: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private var isAnswerVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val onlyUnknown = intent.getBooleanExtra("onlyUnknown", false)
        val selectedCategoryIds = intent.getIntegerArrayListExtra("selectedCategoryIds")?.toList()

        // 선택된 카테고리 ID가 제대로 전달되고 있는지 확인
        Log.d("CategorySelectionQuiz", "Selected Category IDs: $selectedCategoryIds")
        Log.d("CategorySelectionQuiz", "Only Unknown: $onlyUnknown")

        questionTextView = findViewById(R.id.questionTextView)
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
        answerTextView = findViewById(R.id.answerTextView)
        showAnswerButton = findViewById(R.id.showAnswerButton)

        val quizDao = QuizDatabase.getDatabase(this).quizDao()
        quizService = CategorySelectionQuizService(quizDao)

        CoroutineScope(Dispatchers.IO).launch {
            quizService.loadQuizzes(onlyUnknown, selectedCategoryIds ?: listOf())
            withContext(Dispatchers.Main) {
                if (quizService.getQuizzes().isEmpty()) {
                    Toast.makeText(this@CategorySelectionQuizActivity, "모르는 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showQuiz()
                }
            }
        }

        yesButton.setOnClickListener {
            onAnswerSelected(true)
        }

        noButton.setOnClickListener {
            onAnswerSelected(false)
        }

        showAnswerButton.setOnClickListener {
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
                withContext(Dispatchers.Main) {
                    showResult()
                    finish()
                }
            }
        } else {
            showQuiz()
        }
    }

    private fun showResult() {
        val intent = Intent(this, QuizResultActivity::class.java).apply {
            val (knownCount, totalCount) = quizService.getResult()
            putExtra("knownCount", knownCount)
            putExtra("totalCount", totalCount)
        }
        startActivity(intent)
        finish()
    }
}
