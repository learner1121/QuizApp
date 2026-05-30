package com.gautam.quiz_app.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [QuestionsLocal::class, QuestionResult::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QuestionDB : RoomDatabase() {

    abstract fun questionDao(): QuestionDAO
    abstract fun quizResultDao(): QuestionResultDao
}