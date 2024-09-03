package com.example.knowing_simple.ui.quiz

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.ui.quiz.adapter.QuizAdapter
import com.example.knowing_simple.ui.quiz.adapter.QuizEditAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizEditActivity : AppCompatActivity() {

    private lateinit var quizRecyclerView: RecyclerView
    private lateinit var quizAdapter: QuizEditAdapter
    private lateinit var deleteButton: Button
    private lateinit var selectAllButton: Button
    private var isAllSelected = false
    private var categoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_edit)

        categoryId = intent.getIntExtra("categoryId", -1)

        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        quizRecyclerView.layoutManager = LinearLayoutManager(this)

        quizAdapter = QuizEditAdapter(listOf(), this)
        quizRecyclerView.adapter = quizAdapter

        deleteButton = findViewById(R.id.deleteButton)
        selectAllButton = findViewById(R.id.selectAllButton)

        loadQuizData()

        deleteButton.setOnClickListener {
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
            val quizDao = QuizDatabase.getDatabase(this@QuizEditActivity).quizDao()
            val quizList = if (categoryId != -1) {
                quizDao.getQuizzesByCategoryId(categoryId) // 특정 카테고리의 퀴즈만 가져옴
            } else {
                quizDao.getAllQuizzes() // 기본값으로 전체 퀴즈를 가져옴
            }

            withContext(Dispatchers.Main) {
                quizAdapter.updateData(quizList)
            }
        }
    }

    private fun deleteSelectedQuizzes() {
        val selectedIds = quizAdapter.getSelectedQuizIds()
        if (selectedIds.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val quizDao = QuizDatabase.getDatabase(this@QuizEditActivity).quizDao()
                quizDao.deleteQuizzesByIds(selectedIds.toList())

                withContext(Dispatchers.Main) {
                    loadQuizData()
                    isAllSelected = false
                    selectAllButton.text = "전체 선택"

                    // 퀴즈를 삭제한 후 결과 설정
                    setResult(Activity.RESULT_OK)
                    Toast.makeText(this@QuizEditActivity, "선택한 퀴즈가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}