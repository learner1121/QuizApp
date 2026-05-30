package com.gautam.quiz_app.data.remote

import com.gautam.quiz_app.data.model.HistoryEntry
import com.gautam.quiz_app.data.model.Question
import com.gautam.quiz_app.data.model.QuizResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface QuizApi {

    @GET("api/quiz/{section}")
    suspend fun getQuestion(
        @Path("section") section: String,
        @Query("difficulty") difficulty: String,
        @Query("limit") limit: Int
    ): Response<QuizResponse>

    @POST("questions/{section}")
    suspend fun addQuestion(
        @Path("section") section: String,
        @Body question: Question
    ): Response<Question>

    @GET("api/quiz/{section}/random")
    suspend fun randomQuestions(
        @Path("section") section: String,
        @Query("difficulty") difficulty: String,
        @Query("limit") limit: Int
    ): Response<QuizResponse>

    // data/remote/QuizApi.kt — add this endpoint
    @POST("api/history")
    suspend fun postResult(@Body entry: HistoryEntry): Response<Unit>



}