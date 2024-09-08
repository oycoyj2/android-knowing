package com.example.knowing_simple.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.model.Category
import android.widget.TextView

class CategoryAdapter(
    private val categories: List<Category>,
    private val initiallySelectedCategoryIds: Set<Int>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val selectedCategoryIds = initiallySelectedCategoryIds.toMutableSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_selection, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun getSelectedCategories(): Set<Int> {
        return selectedCategoryIds
    }

    fun selectAll() {
        selectedCategoryIds.clear()
        categories.forEach { selectedCategoryIds.add(it.id) }
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedCategoryIds.clear()
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryCheckBox: CheckBox = itemView.findViewById(R.id.categoryCheckBox)
        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: Category) {
            // 텍스트 설정
            categoryNameTextView.text = category.name

            // 체크박스 상태 설정
            categoryCheckBox.setOnCheckedChangeListener(null) // 리스너 초기화
            categoryCheckBox.isChecked = selectedCategoryIds.contains(category.id)

            // 아이템 전체 클릭 시 체크박스 상태 변경
            itemView.setOnClickListener {
                val isChecked = !categoryCheckBox.isChecked
                categoryCheckBox.isChecked = isChecked
                if (isChecked) {
                    selectedCategoryIds.add(category.id)
                } else {
                    selectedCategoryIds.remove(category.id)
                }
            }

            // 체크박스 자체 클릭 시 상태 변경
            categoryCheckBox.setOnClickListener {
                val isChecked = categoryCheckBox.isChecked
                if (isChecked) {
                    selectedCategoryIds.add(category.id)
                } else {
                    selectedCategoryIds.remove(category.id)
                }
            }
        }
    }
}
