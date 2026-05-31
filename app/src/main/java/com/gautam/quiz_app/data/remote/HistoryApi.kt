// data/remote/HistoryApi.kt
package com.gautam.quiz_app.data.remote

import com.gautam.quiz_app.data.model.History
import com.gautam.quiz_app.data.model.HistoryEntry
import com.gautam.quiz_app.data.model.HistoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HistoryApi {

    @GET("api/history")
    suspend fun getHistory(): Response<HistoryResponse>

    @POST("api/history")
    suspend fun postResult(
        @Body entry: HistoryEntry
    ): Response<Unit>
}