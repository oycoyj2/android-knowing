package com.example.knowing_simple.ui.addquiz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.data.model.Quiz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddQuizActivityWithoutCategory : AppCompatActivity() {

    private lateinit var questionEditText: EditText
    private lateinit var answerEditText: EditText
    private lateinit var addQuizButton: Button
    private var categoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz_without_category)

        // 전달된 categoryId 받기
        categoryId = intent.getIntExtra("categoryId", -1)

        questionEditText = findViewById(R.id.questionEditText)
        answerEditText = findViewById(R.id.answerEditText)
        addQuizButton = findViewById(R.id.addQuizButton)

        addQuizButton.setOnClickListener {
            val question = questionEditText.text.toString()
            val answer = answerEditText.text.toString()

            if (question.isNotEmpty() && answer.isNotEmpty()) {
                // 퀴즈 데이터베이스에 추가
                CoroutineScope(Dispatchers.IO).launch {
                    val quizDao = QuizDatabase.getDatabase(this@AddQuizActivityWithoutCategory).quizDao()
                    val newQuiz = Quiz(0, question, answer, false, categoryId)
                    quizDao.insertQuiz(newQuiz)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddQuizActivityWithoutCategory, "문제가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                        finish() // 문제 추가 후 종료
                    }
                }
            } else {
                Toast.makeText(this, "문제와 정답을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
