package com.example.knowing_simple.ui.addquiz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.data.model.Category
import com.example.knowing_simple.data.model.Quiz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddQuizActivity : AppCompatActivity() {

    private lateinit var etQuestion: EditText
    private lateinit var etAnswer: EditText
    private lateinit var btnAddQuiz: Button
    private lateinit var categorySpinner: Spinner
    private lateinit var quizDatabase: QuizDatabase
    private lateinit var categoryList: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_quiz)

        // 뷰 초기화
        etQuestion = findViewById(R.id.etQuestion)
        etAnswer = findViewById(R.id.etAnswer)
        btnAddQuiz = findViewById(R.id.btnAddQuiz)
        categorySpinner = findViewById(R.id.categorySpinner)

        // 데이터베이스 인스턴스 가져오기
        quizDatabase = QuizDatabase.getDatabase(this)

        // 카테고리 로드 및 스피너 설정
        CoroutineScope(Dispatchers.IO).launch {
            categoryList = quizDatabase.categoryDao().getAllCategories() // 모든 카테고리 가져오기
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@AddQuizActivity, R.layout.item_categoory_spinner_selection, categoryList.map { it.name })
                adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
                categorySpinner.adapter = adapter
            }
        }

        // 퀴즈 추가 버튼 클릭 리스너 설정
        btnAddQuiz.setOnClickListener {
            val question = etQuestion.text.toString()
            val answer = etAnswer.text.toString()
            val selectedCategoty = categoryList[categorySpinner.selectedItemPosition] //선택된 카테고리

            if (question.isNotEmpty() && answer.isNotEmpty()) {
                // 퀴즈를 Room 데이터베이스에 삽입
                val quiz = Quiz(question = question, answer = answer, categoryId = selectedCategoty.id)
                insertQuizIntoDatabase(quiz)

                // 입력 필드 초기화
                etQuestion.text.clear()
                etAnswer.text.clear()

                Toast.makeText(this, "퀴즈가 추가되었습니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "질문과 답변을 모두 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun insertQuizIntoDatabase(quiz: Quiz) {
        // Room 데이터베이스에 퀴즈를 삽입하는 작업을 비동기적으로 수행
        CoroutineScope(Dispatchers.IO).launch {
            quizDatabase.quizDao().insertQuiz(quiz)
        }
    }
}