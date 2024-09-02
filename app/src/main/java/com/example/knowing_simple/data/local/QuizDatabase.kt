package com.example.knowing_simple.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.knowing_simple.data.dao.QuizDao
import com.example.knowing_simple.data.model.Quiz

@Database(entities = [Quiz::class], version = 1, exportSchema = false)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}