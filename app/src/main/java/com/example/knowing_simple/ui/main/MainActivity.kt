package com.example.knowing_simple.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.ui.addquiz.AddQuizActivity
import com.example.knowing_simple.ui.quiz.CategorySelectionQuizActivity
import com.example.knowing_simple.ui.quiz.SingleCategoryQuizActivity
import com.example.knowing_simple.ui.quiz.QuizListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var startQuizButton: Button
    private lateinit var startUnknownQuizButton: Button
    private lateinit var addQuizButton: Button
    private lateinit var category1Button: Button
    private lateinit var category2Button: Button
    private lateinit var category3Button: Button
    private lateinit var category4Button: Button
    private lateinit var category5Button: Button
    private lateinit var category6Button: Button
    private lateinit var category7Button: Button
    private lateinit var categorySelectionButton: Button

    private var selectedCategoryIds: List<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 버튼 초기화
        startQuizButton = findViewById(R.id.btnStartQuiz)
        startUnknownQuizButton = findViewById(R.id.btnStartUnknownQuiz)
        addQuizButton = findViewById(R.id.btnAddQuiz)
        category1Button = findViewById(R.id.btnCategory1)
        category2Button = findViewById(R.id.btnCategory2)
        category3Button = findViewById(R.id.btnCategory3)
        category4Button = findViewById(R.id.btnCategory4)
        category5Button = findViewById(R.id.btnCategory5)
        category6Button = findViewById(R.id.btnCategory6)
        category7Button = findViewById(R.id.btnCategory7)
        categorySelectionButton = findViewById(R.id.btnCategorySelection)


        categorySelectionButton.setOnClickListener {
            val intent = Intent(this, CategorySelectionActivity::class.java)
            intent.putIntegerArrayListExtra("selectedCategoryIds", ArrayList(selectedCategoryIds ?: emptyList()))
            categorySelectionLauncher.launch(intent)
        }

        startQuizButton.setOnClickListener {
            startCategorySelectionQuiz(false)
        }

        startUnknownQuizButton.setOnClickListener {
            startCategorySelectionQuiz(true)
        }

        // 퀴즈 추가 버튼 클릭 시 동작
        addQuizButton.setOnClickListener {
            val intent = Intent(this, AddQuizActivity::class.java)
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

        category3Button.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            intent.putExtra("categoryId", 3)
            startActivity(intent)
        }

        category4Button.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            intent.putExtra("categoryId", 4)
            startActivity(intent)
        }

        category5Button.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            intent.putExtra("categoryId", 5)
            startActivity(intent)
        }

        category6Button.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            intent.putExtra("categoryId", 6)
            startActivity(intent)
        }

        category7Button.setOnClickListener {
            val intent = Intent(this, QuizListActivity::class.java)
            intent.putExtra("categoryId", 7)
            startActivity(intent)
        }



    }

    private val categorySelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedCategoryIds = result.data?.getIntegerArrayListExtra("selectedCategoryIds")
            }
        }

    private fun startCategorySelectionQuiz(onlyUnknown: Boolean) {
        val intent = Intent(this, CategorySelectionQuizActivity::class.java).apply {
            putExtra("onlyUnknown", onlyUnknown)
            // selectedCategoryIds가 null인 경우 빈 리스트로 대체
            val categoryIds = ArrayList(selectedCategoryIds ?: listOf())
            putIntegerArrayListExtra("selectedCategoryIds", categoryIds)
        }
        startActivity(intent)
    }


    private fun startSingleCategoryQuiz(onlyUnknown: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val quizDao = QuizDatabase.getDatabase(this@MainActivity).quizDao()
            val quizCount = if (onlyUnknown) quizDao.getUnknownQuizCount() else quizDao.getQuizCount()
            withContext(Dispatchers.Main) {
                if (quizCount > 0) {
                    // 퀴즈가 있을 때만 퀴즈 화면으로 이동
                    val intent = Intent(this@MainActivity, SingleCategoryQuizActivity::class.java)
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
