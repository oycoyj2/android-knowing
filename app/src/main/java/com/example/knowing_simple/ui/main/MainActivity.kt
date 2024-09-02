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
    private lateinit var addQuizButton: Button
    private lateinit var resetQuizButton: Button
    private lateinit var quizListButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 버튼 초기화
        startQuizButton = findViewById(R.id.btnStartQuiz)
        addQuizButton = findViewById(R.id.btnAddQuiz)
        quizListButton = findViewById(R.id.btnQuizList)

        // 퀴즈 시작 버튼 클릭 시 동작
        startQuizButton.setOnClickListener {
            checkAndStartQuiz()
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

    private fun showResetConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("퀴즈 초기화")
        builder.setMessage("정말로 모든 퀴즈 데이터를 초기화하시겠습니까?")
        builder.setPositiveButton("네") { dialog, _ ->
            resetQuizData()
            dialog.dismiss()
        }
        builder.setNegativeButton("아니요") { dialog, _ ->
            dialog.dismiss() // 사용자가 "아니요"를 누르면 대화상자 닫기
        }
        val dialog = builder.create()
        dialog.show()
    }


}