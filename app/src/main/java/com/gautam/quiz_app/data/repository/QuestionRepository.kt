package com.gautam.quiz_app.data.repository


import android.util.Log
import com.gautam.quiz_app.data.model.HistoryEntry
import com.gautam.quiz_app.data.model.Question
import com.gautam.quiz_app.data.model.QuizResponse
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
    suspend fun getQuestion(section :String , limit: Int,difficulty: String): Response<QuizResponse> = api.getQuestion(section,difficulty, limit)

    suspend fun postResult(entry: HistoryEntry) = api.postResult(entry)
    suspend fun addQuestion (section: String,question: Question) =api.addQuestion(section,question)
    suspend fun randomQuestions(section: String, limit: Int,difficulty: String): Response<QuizResponse> = api.randomQuestions(section,difficulty,limit)


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