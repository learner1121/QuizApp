package com.gautam.quiz_app.data.repository

import com.gautam.quiz_app.data.model.LeaderboardEntry
import com.gautam.quiz_app.data.remote.LeaderboardApi
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(
    private val api: LeaderboardApi
) {

    suspend fun getOverall(): Result<List<LeaderboardEntry>> = runCatching {
        val response = api.getOverall()

        if (response.isSuccessful) {
            response.body()?.data?: emptyList()
        } else {
            error("Server error: ${response.code()}")
        }
    }

    suspend fun getBySection(section: String): Result<List<LeaderboardEntry>> = runCatching {
        val response = api.getBySection(section)

        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            error("Server error: ${response.code()}")
        }
    }
}