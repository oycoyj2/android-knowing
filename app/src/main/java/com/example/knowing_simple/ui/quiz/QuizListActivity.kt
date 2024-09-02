package com.example.knowing_simple.ui.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.ui.quiz.adapter.QuizAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizListActivity : AppCompatActivity() {

    private lateinit var quizRecyclerView: RecyclerView
    private lateinit var quizAdapter: QuizAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        quizRecyclerView.layoutManager = LinearLayoutManager(this)

        quizAdapter = QuizAdapter(listOf(), this)
        quizRecyclerView.adapter = quizAdapter

        loadQuizData()
    }

    private fun loadQuizData() {
        CoroutineScope(Dispatchers.IO).launch {
            val quizDao = QuizDatabase.getDatabase(this@QuizListActivity).quizDao()
            val quizList = quizDao.getAllQuizzes() // 모든 퀴즈를 가져옴
            val questionList = quizList.map { it.question } // 문제 데이터만 추출

            withContext(Dispatchers.Main) {
                quizAdapter.updateData(quizList) // 어댑터에 데이터 업데이트
            }
        }
    }
}