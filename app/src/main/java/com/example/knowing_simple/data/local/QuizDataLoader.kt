package com.example.knowing_simple.data.local

import android.content.Context
import com.example.knowing_simple.data.dao.QuizDao
import com.example.knowing_simple.data.model.Quiz
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object QuizDataLoader {

    suspend fun loadQuizDataFromJson(context: Context): List<Quiz> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("quiz_data.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<Quiz>>() {}.type
        gson.fromJson(jsonString, type)
    }

    suspend fun insertQuizzesFromJson(context: Context, quizDao: QuizDao) {
        // JSON에서 퀴즈 데이터를 로드하고 데이터베이스에 삽입
        val quizzes = loadQuizDataFromJson(context)
        quizDao.insertAll(quizzes)
    }
}