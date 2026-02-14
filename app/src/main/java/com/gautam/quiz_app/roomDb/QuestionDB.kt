package com.gautam.quiz_app.roomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import androidx.room.Room

@Database(entities = [QuestionsLocal::class, QuestionResult::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class QuestionDB : RoomDatabase(){
    abstract fun questionDao(): QuestionDAO
    abstract fun quizResultDao(): QuestionResultDao

    companion object {
        @Volatile
        private var INSTANCE : QuestionDB? = null

        fun getDatabase(context: Context): QuestionDB{
            return INSTANCE?:synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuestionDB::class.java,
                    "quiz_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

