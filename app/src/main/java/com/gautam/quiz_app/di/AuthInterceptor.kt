package com.gautam.quiz_app.di

import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseInstanceProvider.firebaseAuthInstance.currentUser

        // If no user logged in, proceed without token
        val token: String? = if (user != null) {
            runBlocking {
                try {
                    user.getIdToken(false).await().token
                } catch (e: Exception) {
                    null
                }
            }
        } else null

        val request = chain.request().newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
        }.build()

        return chain.proceed(request)
    }
}