package com.example.knowing_simple.data.service

import com.example.knowing_simple.data.dao.QuizDao
import com.example.knowing_simple.data.model.Quiz


class QuizService(private val quizDao: QuizDao) {

    private var currentQuizIndex = 0
    private var correctAnswers = 0
    private var quizzes: List<Quiz> = emptyList()

    suspend fun loadQuizzes() {
        quizzes = quizDao.getAllQuizzes().shuffled() // 퀴즈를 랜덤으로 섞음
    }

    fun getCurrentQuiz(): Quiz {
        return quizzes[currentQuizIndex]
    }

    fun checkAnswer(answer: Boolean): Boolean {
        val correctAnswer = quizzes[currentQuizIndex].answer.equals("true", ignoreCase = true)
        if (correctAnswer == answer) {
            correctAnswers++
            return true
        }
        return false
    }

    fun moveToNextQuiz(): Boolean {
        if (currentQuizIndex < quizzes.size - 1) {
            currentQuizIndex++
            return true
        }
        return false
    }

    fun getResult(): Int {
        return correctAnswers
    }

    fun getTotalQuizzes(): Int {
        return quizzes.size
    }
}