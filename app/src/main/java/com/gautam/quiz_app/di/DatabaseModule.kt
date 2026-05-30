package com.gautam.quiz_app.di

import android.content.Context
import androidx.room.Room
import com.gautam.quiz_app.roomDb.QuestionDAO
import com.gautam.quiz_app.roomDb.QuestionDB
import com.gautam.quiz_app.roomDb.QuestionResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): QuestionDB {
        return Room.databaseBuilder(
            context,
            QuestionDB::class.java,
            "quiz_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideQuestionDao(db: QuestionDB): QuestionDAO {
        return db.questionDao()
    }

    @Provides
    fun provideResultDao(db: QuestionDB): QuestionResultDao {
        return db.quizResultDao()
    }
}