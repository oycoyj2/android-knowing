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
import com.example.knowing_simple.ui.quiz.adapter.QuizAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizListActivity : AppCompatActivity() {

    private lateinit var quizRecyclerView: RecyclerView
    private lateinit var quizAdapter: QuizAdapter
    private lateinit var  editButton: Button

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

        loadQuizData()

        editButton.setOnClickListener {
            val intent = Intent(this, QuizEditActivity::class.java)
            editQuizLauncher.launch(intent)  // 이전의 startActivityForResult 대체
        }


    }

    private fun loadQuizData() {
        CoroutineScope(Dispatchers.IO).launch {
            val quizDao = QuizDatabase.getDatabase(this@QuizListActivity).quizDao()
            val quizList = quizDao.getAllQuizzes() // 모든 퀴즈를 가져옴

            withContext(Dispatchers.Main) {
                quizAdapter.updateData(quizList) // 어댑터에 데이터 업데이트
            }
        }
    }

}