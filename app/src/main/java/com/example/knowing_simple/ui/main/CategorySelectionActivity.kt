package com.example.knowing_simple.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.local.QuizDatabase
import com.example.knowing_simple.ui.main.adapter.CategoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategorySelectionActivity : AppCompatActivity() {

    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var applyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView)
        applyButton = findViewById(R.id.btnApplySelection)

        categoryRecyclerView.layoutManager = LinearLayoutManager(this)

        val categoryDao = QuizDatabase.getDatabase(this).categoryDao()
        CoroutineScope(Dispatchers.IO).launch {
            val categories = categoryDao.getAllCategories()
            withContext(Dispatchers.Main) {
                categoryAdapter = CategoryAdapter(categories)
                categoryRecyclerView.adapter = categoryAdapter
            }
        }

        applyButton.setOnClickListener {
            val selectedCategoryIds = categoryAdapter.getSelectedCategories()
            val resultIntent = Intent()
            resultIntent.putIntegerArrayListExtra("selectedCategoryIds", ArrayList(selectedCategoryIds))
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}