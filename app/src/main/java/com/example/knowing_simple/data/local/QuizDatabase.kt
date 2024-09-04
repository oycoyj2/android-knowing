package com.example.knowing_simple.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.knowing_simple.data.dao.CategoryDao
import com.example.knowing_simple.data.dao.QuizDao
import com.example.knowing_simple.data.model.Category
import com.example.knowing_simple.data.model.Quiz
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Quiz::class, Category::class], version = 4, exportSchema = false)
abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: QuizDatabase? = null

        fun getDatabase(context: Context): QuizDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 여기서 기본 카테고리를 추가합니다.
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).categoryDao().insertCategory(Category(name = "카테고리1"))
                                getDatabase(context).categoryDao().insertCategory(Category(name = "카테고리2"))
                                getDatabase(context).categoryDao().insertCategory(Category(name = "카테고리3"))
                                getDatabase(context).categoryDao().insertCategory(Category(name = "카테고리4"))
                                getDatabase(context).categoryDao().insertCategory(Category(name = "카테고리5"))
                                getDatabase(context).categoryDao().insertCategory(Category(name = "카테고리6"))
                                // 필요한 다른 기본 카테고리들도 여기에서 추가할 수 있습니다.
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // 데이터베이스 스키마가 변경될 때 초기화
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}