package com.example.knowing_simple.ui.quiz

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.data.service.CategorySelectionQuizService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategorySelectionQuizActivity : AppCompatActivity() {

    private lateinit var quizService: CategorySelectionQuizService
    private lateinit var questionTextView: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var showAnswerButton: Button

    private var currentAnswer: String = ""
    private var currentIsKnown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val onlyUnknown = intent.getBooleanExtra("onlyUnknown", false)
        val selectedCategoryIds = intent.getIntegerArrayListExtra("selectedCategoryIds")?.toList()


        questionTextView = findViewById<TextView>(R.id.questionTextView)
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
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
            showAnswerDialog()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 퀴즈 종료 여부를 묻는 확인 다이얼로그 표시
                showQuitConfirmationDialog()
            }
        })
    }

    private fun showQuiz() {
        val currentQuiz = quizService.getCurrentQuiz()
        questionTextView.text = currentQuiz.question
        currentAnswer = currentQuiz.answer
        showAnswerButton.text = "답변 열기"
    }

    private fun showAnswerDialog() {
        // Dialog 생성 및 설정
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
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

    private fun showQuitConfirmationDialog() {
        // 확인 다이얼로그 생성
        val builder = AlertDialog.Builder(this)
        builder.setTitle("퀴즈 종료")
        builder.setMessage("정말로 퀴즈를 그만두시겠습니까?\n(현재까지 푼 문제의 상태는 저장됩니다)")

        // "예" 버튼 클릭 시 퀴즈 상태 저장 후 종료
        builder.setPositiveButton("예") { _, _ ->
            saveQuizStateAndExit()
        }

        // "아니오" 버튼 클릭 시 다이얼로그만 닫기
        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }

        // 다이얼로그 표시
        builder.show()
    }

    private fun saveQuizStateAndExit() {
        // 현재 문제 상태 저장
        quizService.updateQuizStatus(currentIsKnown)
        CoroutineScope(Dispatchers.IO).launch {
            quizService.saveQuizStatus()
            runOnUiThread {
                finish() // 액티비티 종료
            }
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
