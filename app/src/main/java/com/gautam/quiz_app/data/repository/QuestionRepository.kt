package com.gautam.quiz_app.data.repository


import Question
import android.content.Context
import android.util.Log
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.lifecycle.LiveData
import com.gautam.quiz_app.data.model.AiQuestion
import com.gautam.quiz_app.data.model.GenerateRequest
import com.gautam.quiz_app.data.remote.QuizApi
import com.gautam.quiz_app.roomDb.QuestionDAO
import com.gautam.quiz_app.roomDb.QuestionResult
import com.gautam.quiz_app.roomDb.QuestionResultDao
import com.gautam.quiz_app.roomDb.QuestionsLocal
import retrofit2.Response
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api: QuizApi
) {

    //Retrofit API
    suspend fun getQuestion(section :String , limit: Int) = api.getQuestion(section,limit)
    suspend fun addQuestion (section: String,question: Question) =api.addQuestion(section,question)
    suspend fun randomQuestions(section: String, limit: Int) = api.randomQuestions(section,limit)

    // ai question
    suspend fun generateQuestion(
        topic: String,
        difficulty: String,
        count: Int
    ): Response<List<AiQuestion>> {

        return api.generateQuestion(
            GenerateRequest(topic, difficulty, count)
        )
    }
}

class LocalQuestionRepository @Inject constructor(private val dao: QuestionDAO){
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
class ResultRepository @Inject constructor(private val dao: QuestionResultDao){
    suspend fun addScore(questionResult: QuestionResult){
        Log.d("Room Add Result","Inserted score : ${questionResult.section}")
        dao.addScore(questionResult)
    }
    suspend fun getScore(): List<QuestionResult>{
        Log.d("Room Get Result","Getting score")
        return dao.getScore()
    }
}