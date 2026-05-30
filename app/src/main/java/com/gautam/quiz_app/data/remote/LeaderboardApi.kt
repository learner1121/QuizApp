package com.gautam.quiz_app.data.remote

import com.gautam.quiz_app.data.model.LeaderboardEntry
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface LeaderboardApi {

    @GET("api/leaderboard")
    suspend fun getOverall(): Response<List<LeaderboardEntry>>

    @GET("api/leaderboard/{section}")
    suspend fun getBySection(
        @Path("section") section: String
    ): Response<List<LeaderboardEntry>>
}