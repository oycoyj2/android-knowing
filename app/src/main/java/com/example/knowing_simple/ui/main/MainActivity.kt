package com.example.knowing_simple.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.ui.addquiz.AddQuizActivity
import com.example.knowing_simple.ui.quiz.QuizActivity
import com.example.knowing_simple.ui.quiz.QuizListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var startQuizButton: Button
    private lateinit var startUnknownQuizButton: Button
    private lateinit var addQuizButton: Button
    private lateinit var quizListButton: Button
    private lateinit var category1Button: Button
    private lateinit var category2Button: Button
    private lateinit var categorySelectionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 버튼 초기화
        startQuizButton = findViewById(R.id.btnStartQuiz)
        startUnknownQuizButton = findViewById(R.id.btnStartUnknownQuiz)
        addQuizButton = findViewById(R.id.btnAddQuiz)
        quizListButton = findViewById(R.id.btnQuizList)
        category1Button = findViewById(R.id.btnCategory1)
        category2Button = findViewById(R.id.btnCategory2)
        categorySelectionButton = findViewById(R.id.btnCategorySelection)

        // 모든 문제 풀기 버튼 클릭 시 동작
        startQuizButton.setOnClickListener {
            checkAndStartQuiz()
        }

        // 모르는 문제만 풀기 버튼 클릭 시 동작
        startUnknownQuizButton.setOnClickListener {
            startQuiz(true)
        }

        // 퀴즈 추가 버튼 클릭 시 동작
        addQuizButton.setOnClickListener {
            val intent = Intent(this, AddQuizActivity::class.java)
            startActivity(intent)
        }

        // 문제 목록 버튼 클릭 시 동작
        quizListButton.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            startActivity(intent)
        }

        // 카테고리 1 버튼 클릭 시 동작
        category1Button.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            intent.putExtra("categoryId", 1)
            startActivity(intent)
        }

        // 카테고리 2 버튼 클릭 시 동작
        category2Button.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            intent.putExtra("categoryId", 2)
            startActivity(intent)
        }

        categorySelectionButton.setOnClickListener {
            val intent = Intent(this, CategorySelectionActivity::class.java)
            startActivity(intent)
        }
    }



    // 퀴즈가 있는지 확인하고, 없으면 알림을 띄우는 함수
    private fun checkAndStartQuiz() {
        CoroutineScope(Dispatchers.IO).launch {
            val quizDao = QuizDatabase.getDatabase(this@MainActivity).quizDao()
            val quizCount = quizDao.getQuizCount()
            withContext(Dispatchers.Main) {
                if (quizCount > 0) {
                    // 퀴즈가 있을 때만 퀴즈 화면으로 이동
                    val intent = Intent(this@MainActivity, QuizActivity::class.java)
                    startActivity(intent)
                } else {
                    // 퀴즈가 없을 때 알림 표시
                    Toast.makeText(this@MainActivity, "퀴즈 데이터가 없습니다. 먼저 퀴즈를 추가하세요.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 퀴즈 데이터를 초기화하는 메서드
    private fun resetQuizData() {
        CoroutineScope(Dispatchers.IO).launch {
            val quizDao = QuizDatabase.getDatabase(this@MainActivity).quizDao()
            quizDao.deleteAllQuizzes()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "퀴즈 데이터 초기화가 완료되었습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startQuiz(onlyUnknown: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val quizDao = QuizDatabase.getDatabase(this@MainActivity).quizDao()
            val quizCount = if (onlyUnknown) quizDao.getUnknownQuizCount() else quizDao.getQuizCount()
            withContext(Dispatchers.Main) {
                if (quizCount > 0) {
                    // 퀴즈가 있을 때만 퀴즈 화면으로 이동
                    val intent = Intent(this@MainActivity, QuizActivity::class.java)
                    intent.putExtra("onlyUnknown", onlyUnknown)
                    startActivity(intent)
                } else {
                    // 퀴즈가 없을 때 알림 표시
                    Toast.makeText(this@MainActivity, "해당 조건에 맞는 퀴즈 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}