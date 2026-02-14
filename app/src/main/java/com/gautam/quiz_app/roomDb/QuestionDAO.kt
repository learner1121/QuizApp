package com.gautam.quiz_app.roomDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuestion(que: QuestionsLocal)

    @Delete
    suspend fun deleteQuestion(que: QuestionsLocal)

    @Query("SELECT * FROM QuestionTable WHERE section = :section")
    suspend fun getQuestion(section: String): List<QuestionsLocal>
}

@Dao
interface QuestionResultDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addScore(questionResult: QuestionResult)

    @Query("SELECT*FROM QuestionResult Order by date desc ")
    suspend fun getScore(): List<QuestionResult>
}