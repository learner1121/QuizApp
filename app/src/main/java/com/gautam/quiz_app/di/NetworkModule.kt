package com.gautam.quiz_app.di


import com.gautam.quiz_app.data.remote.HistoryApi
import com.gautam.quiz_app.data.remote.LeaderboardApi
import com.gautam.quiz_app.data.remote.ProfileApi
import com.gautam.quiz_app.data.remote.QuizApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)

object NetworkModule {
    @Provides
    @Singleton
    fun provideBaseUrl(): String{
        return "https://quiz-app-h6ip.onrender.com/"
    }

    // for debugging api calls
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor{
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    }

    // okhttp client
    @Provides
    @Singleton
    fun provideOkhttpClient(
        loginInterceptor: HttpLoggingInterceptor
    ): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(loginInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    // retrofit
    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // QuizApi
    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit) : QuizApi{
        return retrofit.create(QuizApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHistoryApi(retrofit: Retrofit): HistoryApi {
        return retrofit.create(HistoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLeaderboardApi(retrofit: Retrofit): LeaderboardApi {
        return retrofit.create(LeaderboardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }


}