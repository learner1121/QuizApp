// data/model/HistoryEntry.kt
package com.gautam.quiz_app.data.model

import com.google.gson.annotations.SerializedName

data class HistoryEntry(
    @SerializedName("userId")     val userId     : String,
    @SerializedName("section")    val section    : String,
    @SerializedName("difficulty") val difficulty : String,
    @SerializedName("score")      val score      : Int,
    @SerializedName("total")      val total      : Int,
    @SerializedName("timeTaken")  val timeTaken  : Int,
    @SerializedName("date")       val date       : Long
)