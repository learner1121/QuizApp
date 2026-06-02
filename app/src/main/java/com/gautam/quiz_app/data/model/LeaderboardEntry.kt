
package com.gautam.quiz_app.data.model

import com.google.gson.annotations.SerializedName

/*
data class LeaderboardEntry(
    @SerializedName("userId")    val userId    : String = "",
    @SerializedName("name")      val name      : String = "Anonymous",
    @SerializedName("score")     val score     : Int    = 0,
    @SerializedName("quizCount") val quizCount : Int    = 0,
    @SerializedName("rank")      val rank      : Int    = 0,
    @SerializedName("section")   val section   : String = "Overall"
)*/
data class LeaderboardEntry(
    @SerializedName("_id")
    val userId: String = "",

    @SerializedName("userName")
    val name: String = "Anonymous",

    @SerializedName("totalScore")
    val score: Int = 0,

    @SerializedName("totalQuizzesPlayed")
    val quizCount: Int = 0,

    @SerializedName("rank")
    val rank: Int = 0
)
