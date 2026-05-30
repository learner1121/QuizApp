
package com.gautam.quiz_app.data.model

import com.google.gson.annotations.SerializedName

data class History(
    @SerializedName("_id")        val id         : String  = "",
    @SerializedName("userId")     val userId     : String  = "",
    @SerializedName("section")    val section    : String  = "",
    @SerializedName("difficulty") val difficulty : String  = "",
    @SerializedName("score")      val score      : Int     = 0,
    @SerializedName("total")      val total      : Int     = 0,
    @SerializedName("timeTaken")  val timeTaken  : Int     = 0,
    @SerializedName("date")       val date       : Long    = 0L
) {
    val percentage : Float  get() = if (total == 0) 0f else (score * 100f) / total
    val timeFormatted : String get() {
        val m = timeTaken / 60
        val s = timeTaken % 60
        return if (m > 0) "${m}m ${s}s" else "${s}s"
    }
}