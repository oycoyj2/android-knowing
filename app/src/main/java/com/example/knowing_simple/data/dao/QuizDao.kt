package com.example.knowing_simple.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    @Query("SELECT * FROM quiz_table WHERE categoryId = :categoryId")
    suspend fun getQuizzesByCategoryId(categoryId: Int): List<Quiz> // 카테고리 ID로 퀴즈 가져오기

    @Query("SELECT * FROM quiz_table WHERE categoryId = :categoryId AND isKnown = 0")
    suspend fun getUnknownQuizzesByCategoryId(categoryId: Int): List<Quiz>

    @Query("SELECT * FROM quiz_table WHERE categoryId IN (:categoryIds)")
    suspend fun getQuizzesByCategoryIds(categoryIds: List<Int>): List<Quiz>

    @Query("SELECT * FROM quiz_table WHERE isKnown = 0 AND categoryId IN (:categoryIds)")
    suspend fun getUnknownQuizzesByCategoryIds(categoryIds: List<Int>): List<Quiz>

    @Query("SELECT COUNT(*) FROM quiz_table WHERE categoryId = :categoryId")
    suspend fun getQuizCountByCategory(categoryId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quizzes: List<Quiz>)

    @Query("SELECT COUNT(*) FROM quiz_table WHERE categoryId = :categoryId")
    suspend fun getQuizCountByCategoryId(categoryId: Int): Int

    @Query("SELECT COUNT(*) FROM quiz_table WHERE categoryId = :categoryId AND isKnown = 1")
    suspend fun getKnownQuizCountByCategoryId(categoryId: Int): Int
}
