package com.gautam.quiz_app.roomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuestionTable")
data class QuestionsLocal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Question")  val questionText: String?,
    @ColumnInfo(name = "Section") val section: String?,
    @ColumnInfo(name = "Options") val options: List<String>,
    @ColumnInfo(name = "Correct Answer") val correctAnswer: String?,
    @ColumnInfo("Marks") val marks: Int?
)