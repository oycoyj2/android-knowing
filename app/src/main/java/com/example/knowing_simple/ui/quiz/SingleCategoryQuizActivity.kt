package com.example.knowing_simple.ui.quiz

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
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
    private lateinit var showAnswerButton: Button

    private var currentAnswer: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val categoryId = intent.getIntExtra("categoryId", -1)
        val onlyUnknown = intent.getBooleanExtra("onlyUnknown", false)

        // View 초기화
        questionTextView = findViewById(R.id.questionTextView)
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
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
            showAnswerDialog()
        }

    }


    private fun showQuiz() {
        val currentQuiz = singleCategoryQuizService.getCurrentQuiz()
        questionTextView.text = currentQuiz.question
        currentAnswer = currentQuiz.answer
        showAnswerButton.text = "답 확인"
    }

    private fun showAnswerDialog() {
        // Dialog 생성 및 설정
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_answer, null)

        dialog.setContentView(dialogView)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        val answerTextView = dialogView.findViewById<TextView>(R.id.answerTextView)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)

        // 답변 설정
        answerTextView.text = currentAnswer

        // 닫기 버튼 클릭 시 Dialog 닫기
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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