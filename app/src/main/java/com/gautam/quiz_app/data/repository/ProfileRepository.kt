// data/repository/ProfileRepository.kt
package com.gautam.quiz_app.data.repository

import com.gautam.quiz_app.data.model.UserProfile
import com.gautam.quiz_app.data.remote.ProfileApi
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: ProfileApi
) {
    suspend fun getProfile(userId: String): Result<UserProfile> = runCatching {
        val response = api.getProfile(userId)
        if (response.isSuccessful) response.body()
            ?: error("Empty profile response")
        else error("Server error: ${response.code()}")
    }
}