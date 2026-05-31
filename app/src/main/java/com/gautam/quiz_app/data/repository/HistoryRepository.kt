// data/repository/HistoryRepository.kt
package com.gautam.quiz_app.data.repository

import com.gautam.quiz_app.data.model.History
import com.gautam.quiz_app.data.model.HistoryEntry
import com.gautam.quiz_app.data.remote.HistoryApi
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val api: HistoryApi
) {
    suspend fun getHistory(): Result<List<History>> = runCatching {
        val response = api.getHistory()

        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            error("Server error: ${response.code()}")
        }
    }

    suspend fun postResult(entry: HistoryEntry): Result<Unit> = runCatching {
        val response = api.postResult(entry)
        if (!response.isSuccessful) error("Failed to save result: ${response.code()}")
    }
}