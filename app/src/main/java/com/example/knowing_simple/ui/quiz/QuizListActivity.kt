package com.example.knowing_simple.ui.quiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.ui.addquiz.AddQuizActivityWithoutCategory
import com.example.knowing_simple.ui.quiz.adapter.QuizAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizListActivity : AppCompatActivity() {

    private lateinit var quizRecyclerView: RecyclerView
    private lateinit var quizAdapter: QuizAdapter
    private lateinit var  editButton: Button
    private lateinit var startCategoryQuizButton: Button
    private lateinit var startUnknownCategoryQuizButton: Button
    private lateinit var addQuizButton: Button

    private var categoryId: Int? = null

    // ActivityResultLauncher 선언
    private lateinit var editQuizLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_list)

        // ActivityResultLauncher 초기화
        editQuizLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // QuizEditActivity에서 문제가 수정된 후 돌아온 경우 데이터를 새로 로드
                loadQuizData()
            }
        }

        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        quizRecyclerView.layoutManager = LinearLayoutManager(this)

        quizAdapter = QuizAdapter(listOf(), this)
        quizRecyclerView.adapter = quizAdapter

        editButton = findViewById(R.id.editButton)
        startCategoryQuizButton = findViewById(R.id.startCategoryQuizButton)
        startUnknownCategoryQuizButton = findViewById(R.id.startUnknownCategoryQuizButton)

        categoryId = intent.getIntExtra("categoryId", -1)

        addQuizButton = findViewById(R.id.addQuizButton)
        addQuizButton.setOnClickListener{
            val intent = Intent(this, AddQuizActivityWithoutCategory::class.java)
            intent.putExtra("categoryId", categoryId)
            startActivity(intent)
        }
        loadQuizData()

        editButton.setOnClickListener {
            val intent = Intent(this, QuizEditActivity::class.java)
            intent.putExtra("categoryId", categoryId)
            editQuizLauncher.launch(intent)  // 이전의 startActivityForResult 대체
        }

        startCategoryQuizButton.setOnClickListener {
            startQuiz(categoryId, onlyUnknown = false)
        }

        startUnknownCategoryQuizButton.setOnClickListener {
            startQuiz(categoryId, onlyUnknown = true)
        }

    }

    override fun onResume() {
        super.onResume()
        loadQuizData() // 액티비티가 다시 활성화될 때 데이터를 새로고침
    }

    private fun startQuiz(categoryId: Int?, onlyUnknown: Boolean) {
        val intent = Intent(this, SingleCategoryQuizActivity::class.java).apply {
            putExtra("categoryId", categoryId)
            putExtra("onlyUnknown", onlyUnknown)
        }
        startActivity(intent)
    }

    private fun loadQuizData() {
        CoroutineScope(Dispatchers.IO).launch {
            val quizDao = QuizDatabase.getDatabase(this@QuizListActivity).quizDao()
            val quizList = if (categoryId != null && categoryId != -1) {
                quizDao.getQuizzesByCategoryId(categoryId!!) // 카테고리에 맞는 퀴즈를 가져옴
            } else {
                quizDao.getAllQuizzes() // 모든 퀴즈를 가져옴
            }

            withContext(Dispatchers.Main) {
                quizAdapter.updateData(quizList) // 어댑터에 데이터 업데이트
            }
        }
    }

}