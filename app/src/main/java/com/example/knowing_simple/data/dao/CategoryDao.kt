package com.example.knowing_simple.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.knowing_simple.data.model.Category


@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category): Long

    @Query("SELECT * FROM category_table")
    suspend fun getAllCategories(): List<Category>

    @Query("SELECT COUNT(*) FROM category_table")
    suspend fun getCategoryCount(): Int

    @Query("SELECT COUNT(*) FROM quiz_table WHERE categoryId = :categoryId")
    suspend fun getQuizCountForCategory(categoryId: Int): Int
}