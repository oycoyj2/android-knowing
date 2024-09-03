package com.example.knowing_simple.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.knowing_simple.R
import com.example.knowing_simple.data.model.Category

class CategoryAdapter(private val categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private val selectedCategoryIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_selection, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun getSelectedCategories(): List<Int> {
        return selectedCategoryIds.toList()
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

        fun bind(category: Category) {
            categoryCheckBox.text = category.name
            categoryCheckBox.setOnCheckedChangeListener(null)
            categoryCheckBox.isChecked = selectedCategoryIds.contains(category.id)
            categoryCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedCategoryIds.add(category.id)
                } else {
                    selectedCategoryIds.remove(category.id)
                }
            }
        }
    }
}