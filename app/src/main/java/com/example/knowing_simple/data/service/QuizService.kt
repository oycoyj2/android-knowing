package com.example.knowing_simple.data.service

import com.example.knowing_simple.data.dao.QuizDao
import com.example.knowing_simple.data.model.Quiz


class QuizService(private val quizDao: QuizDao) {

    private var currentQuizIndex = 0
    private var quizzes: List<Quiz> = emptyList()

    suspend fun loadQuizzes() {
        quizzes = quizDao.getAllQuizzes().shuffled() // 퀴즈를 랜덤으로 섞음
    }

    fun getCurrentQuiz(): Quiz {
        return quizzes[currentQuizIndex]
    }

    fun updateQuizStatus(isKnown: Boolean) {
        quizzes[currentQuizIndex].isKnown = isKnown
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
        return Pair(knownCount, quizzes.size) // 아는 문제 수, 전체 문제 수
    }

}