package com.example.knowing_simple.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.data.service.SingleCategoryQuizService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleCategoryQuizActivity : AppCompatActivity() {

    private lateinit var singleCategoryQuizService: SingleCategoryQuizService
    private lateinit var questionTextView: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private var isAnswerVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val categoryId = intent.getIntExtra("categoryId", -1)
        val onlyUnknown = intent.getBooleanExtra("onlyUnknown", false)

        // View 초기화
        questionTextView = findViewById(R.id.questionTextView)
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
        answerTextView = findViewById(R.id.answerTextView)
        showAnswerButton = findViewById(R.id.showAnswerButton)

        // QuizService 초기화
        val quizDao = QuizDatabase.getDatabase(this).quizDao()
        singleCategoryQuizService = SingleCategoryQuizService(quizDao)


        // 퀴즈 로드 및 첫 번째 퀴즈 표시
        CoroutineScope(Dispatchers.IO).launch {
            singleCategoryQuizService.loadQuizzes(onlyUnknown, if (categoryId != -1) categoryId else null)
            withContext(Dispatchers.Main) {
                if (singleCategoryQuizService.getQuizzes().isEmpty()) {
                    // 퀴즈가 없으면 경고를 띄우고 메인 화면으로 돌아감
                    Toast.makeText(this@SingleCategoryQuizActivity, "모르는 문제가 없습니다.", Toast.LENGTH_SHORT).show()
                    finish() // 현재 Activity 종료
                } else {
                    showQuiz() // 퀴즈 표시
                }
            }
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
        val currentQuiz = singleCategoryQuizService.getCurrentQuiz()
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
        singleCategoryQuizService.updateQuizStatus(isKnown)
        if (!singleCategoryQuizService.moveToNextQuiz()) {
            CoroutineScope(Dispatchers.IO).launch {
                singleCategoryQuizService.saveQuizStatus()
                runOnUiThread{
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
            val (knownCount, totalCount) = singleCategoryQuizService.getResult()
            putExtra("knownCount", knownCount)
            putExtra("totalCount", totalCount)
        }
        startActivity(intent)
        finish()
    }
}