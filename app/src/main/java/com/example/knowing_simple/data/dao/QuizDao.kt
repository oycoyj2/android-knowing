package com.example.knowing_simple.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.knowing_simple.data.model.Quiz

@Dao
interface QuizDao {
    @Insert
    suspend fun insertQuiz(quiz: Quiz): Long

    @Query("SELECT * FROM quiz_table")
    suspend fun getAllQuizzes(): List<Quiz>

    @Query("DELETE FROM quiz_table")
    suspend fun deleteAllQuizzes()

    @Query("SELECT COUNT(*) FROM quiz_table")
    suspend fun getQuizCount(): Int // 퀴즈 개수 확인

    @Query("DELETE FROM quiz_table WHERE id IN (:ids)")
    suspend fun deleteQuizzesByIds(ids: List<Int>)

    @Query("SELECT * FROM quiz_table WHERE isKnown = 0")
    suspend fun getUnknownQuizzes(): List<Quiz>

    @Query("SELECT COUNT(*) FROM quiz_table WHERE isKnown = 0")
    suspend fun getUnknownQuizCount(): Int

    @Update
    suspend fun updateQuizzes(quizzes: List<Quiz>) // 퀴즈 상태 업데이트

}
