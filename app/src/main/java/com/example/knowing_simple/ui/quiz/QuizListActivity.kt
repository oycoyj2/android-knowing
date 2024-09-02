package com.example.knowing_simple.ui.quiz

import android.os.Bundle
import android.widget.Button
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
    private lateinit var  deleteButton: Button
    private lateinit var selectAllButton: Button
    private var isAllSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        quizRecyclerView.layoutManager = LinearLayoutManager(this)

        quizAdapter = QuizAdapter(listOf(), this)
        quizRecyclerView.adapter = quizAdapter

        deleteButton = findViewById(R.id.deleteButton)
        selectAllButton = findViewById(R.id.selectAllButton)

        loadQuizData()

        deleteButton.setOnClickListener{
            deleteSelectedQuizzes()
        }

        selectAllButton.setOnClickListener {
            if (isAllSelected) {
                quizAdapter.deselectAll()
                selectAllButton.text = "전체 선택"
            } else {
                quizAdapter.selectAll()
                selectAllButton.text = "전체 해제"
            }
            isAllSelected = !isAllSelected
        }

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

    private fun deleteSelectedQuizzes() {
        val selectedIds = quizAdapter.getSelectedQuizIds()
        if (selectedIds.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val quizDao = QuizDatabase.getDatabase(this@QuizListActivity).quizDao()
                quizDao.deleteQuizzesByIds(selectedIds.toList()) // 선택된 퀴즈 삭제

                withContext(Dispatchers.Main) {
                    loadQuizData() // 삭제 후 목록 갱신
                    isAllSelected = false
                    selectAllButton.text = "전체 선택"
                }
            }
        }
    }
}