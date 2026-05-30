package com.gautam.quiz_app.roomDb



import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QuestionTable")
data class QuestionsLocal(
    @PrimaryKey(autoGenerate = true)    val id            : Int          = 0,
    @ColumnInfo(name = "questionText")  val questionText  : String?      = null,
    @ColumnInfo(name = "section")       val section       : String?      = null,
    @ColumnInfo(name = "options")       val options       : List<String> = emptyList(),
    @ColumnInfo(name = "correctAnswer") val correctAnswer : String?      = null,
    @ColumnInfo(name = "difficulty")    val difficulty    : String?      = null,
    @ColumnInfo(name = "explanation")   val explanation   : String?      = null,
    @ColumnInfo(name = "marks")         val marks         : Int?         = null,
    @ColumnInfo(name = "isActive")      val isActive      : Boolean      = true
)