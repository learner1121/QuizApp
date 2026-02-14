package com.gautam.quiz_app.data.repository


import Question
import android.content.Context
import android.util.Log
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.lifecycle.LiveData
import com.gautam.quiz_app.data.remote.RetrofitInstance
import com.gautam.quiz_app.roomDb.QuestionDAO
import com.gautam.quiz_app.roomDb.QuestionResult
import com.gautam.quiz_app.roomDb.QuestionResultDao
import com.gautam.quiz_app.roomDb.QuestionsLocal

class QuestionRepository() {

    //Retrofit API
    suspend fun getQuestion(section :String , limit: Int) = RetrofitInstance.api.getQuestion(section,limit)
    suspend fun addQuestion (section: String,question: Question) = RetrofitInstance.api.addQuestion(section,question)
    suspend fun randomQuestions(section: String, limit: Int) = RetrofitInstance.api.randomQuestions(section,limit)
}

class LocalQuestionRepository(private val dao: QuestionDAO){
    //RoomDatabase
    //Read

    suspend fun getQuestionsBySection(section: String): List<QuestionsLocal> {
        Log.d("RoomDebug", "Fetching questions for section: $section")
        return dao.getQuestion(section)
    }

    //Add Question
    suspend fun addLocal(questionsLocal: QuestionsLocal){
        Log.d("RoomDebug", "Inserting question: ${questionsLocal.questionText}")
        dao.addQuestion(questionsLocal)
    }
}
class ResultRepository(private val dao: QuestionResultDao){
    suspend fun addScore(questionResult: QuestionResult){
        Log.d("Room Add Result","Inserted score : ${questionResult.section}")
        dao.addScore(questionResult)
    }
    suspend fun getScore(): List<QuestionResult>{
        Log.d("Room Get Result","Getting score")
        return dao.getScore()
    }
}