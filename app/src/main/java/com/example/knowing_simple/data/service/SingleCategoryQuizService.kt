package com.example.knowing_simple.data.service

import com.example.knowing_simple.data.dao.QuizDao
import com.example.knowing_simple.data.model.Quiz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SingleCategoryQuizService(private val quizDao: QuizDao) {

    private var currentQuizIndex = 0
    private var quizzes: List<Quiz> = emptyList()

    suspend fun loadQuizzes(onlyUnknown: Boolean, categoryId: Int? = null) {
        quizzes = if (categoryId != null) {
            if (onlyUnknown) {
                quizDao.getUnknownQuizzesByCategoryId(categoryId).shuffled()
            } else {
                quizDao.getQuizzesByCategoryId(categoryId).shuffled()
            }
        } else {
            if (onlyUnknown) {
                quizDao.getUnknownQuizzes().shuffled()
            } else {
                quizDao.getAllQuizzes().shuffled()
            }
        }
    }

    fun getQuizzes(): List<Quiz> {
        return quizzes
    }

    fun getCurrentQuiz(): Quiz {
        return quizzes[currentQuizIndex]
    }

    fun updateQuizStatus(isKnown: Boolean) {
        quizzes[currentQuizIndex].isKnown = isKnown
        CoroutineScope(Dispatchers.IO).launch {
            quizDao.updateQuizzes(listOf(quizzes[currentQuizIndex]))
        }
    }

    fun moveToNextQuiz(): Boolean {
        if (currentQuizIndex < quizzes.size - 1) {
            currentQuizIndex++
            return true
        }
        return false
    }

    suspend fun saveQuizStatus() {
        quizDao.updateQuizzes(quizzes)
    }

    fun getResult(): Pair<Int, Int> {
        val knownCount = quizzes.count { it.isKnown }
        val totalCount = quizzes.size
        return Pair(knownCount, totalCount) // 아는 문제 수, 전체 문제 수
    }
}
