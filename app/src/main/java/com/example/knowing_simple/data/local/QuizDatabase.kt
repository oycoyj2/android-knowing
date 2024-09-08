package com.example.knowing_simple.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
                                val categoryDao = getDatabase(context).categoryDao()
                                val quizDao = getDatabase(context).quizDao()

                                // 기본 카테고리를 추가
                                populateInitialCategories(categoryDao)

                                // JSON 파일에서 데이터를 로드하고 퀴즈를 추가
                                QuizDataLoader.insertQuizzesFromJson(context, quizDao)
                            }
                        }


                    })
                    .fallbackToDestructiveMigration() // 데이터베이스 스키마가 변경될 때 초기화
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialCategories(categoryDao: CategoryDao) {
            if (categoryDao.getCategoryCount() == 0) {
                categoryDao.insertCategory(Category(1, "컴퓨터 구조"))
                categoryDao.insertCategory(Category(2, "운영체제"))
                categoryDao.insertCategory(Category(3, "데이터베이스"))
                categoryDao.insertCategory(Category(4, "네트워크"))
                categoryDao.insertCategory(Category(5, "자료구조"))
                categoryDao.insertCategory(Category(6, "알고리즘"))
            }
        }
    }
}