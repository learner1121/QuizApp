package com.gautam.quiz_app.roomDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuestionResult")
data class QuestionResult(
    @PrimaryKey (autoGenerate = true)val id: Int = 0,
    val section: String?,
    val score: Int,
    val totalQuestion: Int,
    val date: Long
)