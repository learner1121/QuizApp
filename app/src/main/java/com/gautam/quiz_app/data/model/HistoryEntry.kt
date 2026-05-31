// data/model/HistoryEntry.kt
package com.gautam.quiz_app.data.model

import com.google.gson.annotations.SerializedName

data class HistoryEntry(
    @SerializedName("section")
    val section: String,

    @SerializedName("difficulty")
    val difficulty: String,

    @SerializedName("totalQuestions")
    val totalQuestions: Int,

    @SerializedName("attemptedQuestions")
    val attemptedQuestions: Int,

    @SerializedName("correctAnswers")
    val correctAnswers: Int,

    @SerializedName("wrongAnswers")
    val wrongAnswers: Int,

    @SerializedName("score")
    val score: Int,

    @SerializedName("timeTaken")
    val timeTaken: Int
)