package com.gautam.quiz_app.data.remote

import Question
import com.gautam.quiz_app.data.model.AiQuestion
import com.gautam.quiz_app.data.model.GenerateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizApi {

    @GET("api/questions/{section}/{limit}")
    suspend fun getQuestion(
        @Path("section") section: String,
        @Path("limit") limit: Int
    ): Response<List<Question>>

    @POST("questions/{section}")
    suspend fun addQuestion(
        @Path("section") section: String,
        @Body question: Question
    ): Response<Question>

    @GET("api/questions/random/{section}/{limit}")
    suspend fun randomQuestions(
        @Path("section") section: String,
        @Path("limit") limit: Int
    ): Response<List<Question>>

    @POST("api/questions/generate")
    suspend fun generateQuestion(
        @Body request: GenerateRequest
    ): Response<List<AiQuestion>>
}