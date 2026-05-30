// data/remote/ProfileApi.kt
package com.gautam.quiz_app.data.remote

import com.gautam.quiz_app.data.model.UserProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {

    @GET("api/profile/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: String
    ): Response<UserProfile>
}