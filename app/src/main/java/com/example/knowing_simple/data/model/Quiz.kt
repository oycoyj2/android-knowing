package com.example.knowing_simple.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_table")
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    var isKnown: Boolean = false
)