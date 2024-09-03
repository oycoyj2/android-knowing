package com.example.knowing_simple.data.service

import com.example.knowing_simple.data.dao.QuizDao
import com.example.knowing_simple.data.model.Quiz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategorySelectionQuizService(private val quizDao: QuizDao) {

    private var currentQuizIndex = 0
    private var quizzes: List<Quiz> = emptyList()

    suspend fun loadQuizzes(onlyUnknown: Boolean, categoryIds: List<Int>?) {
        // 카테고리 ID가 없거나 빈 경우 모든 카테고리의 퀴즈를 로드
        quizzes = if (categoryIds == null || categoryIds.isEmpty()) {
            if (onlyUnknown) {
                quizDao.getUnknownQuizzes().shuffled()
            } else {
                quizDao.getAllQuizzes().shuffled()
            }
        } else {
            // 선택된 카테고리의 퀴즈만 로드
            if (onlyUnknown) {
                quizDao.getUnknownQuizzesByCategoryIds(categoryIds).shuffled()
            } else {
                quizDao.getQuizzesByCategoryIds(categoryIds).shuffled()
            }
        }
        currentQuizIndex = 0
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