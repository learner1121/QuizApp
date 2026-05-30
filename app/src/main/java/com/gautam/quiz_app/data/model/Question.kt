package com.gautam.quiz_app.data.model

import com.google.gson.annotations.SerializedName

data class Question(
        @SerializedName("_id")           val id            : String?      = null,
        @SerializedName("questionText")  val questionText  : String?      = null,
        @SerializedName("section")       val section       : String?      = null,
        @SerializedName("options")       val options       : List<String> = emptyList(),
        @SerializedName("correctAnswer") val correctAnswer : String?      = null,
        @SerializedName("difficulty")    val difficulty    : String?      = null,
        @SerializedName("explanation")   val explanation   : String?      = null,
        @SerializedName("marks")         val marks         : Int?         = null,
        @SerializedName("isActive")      val isActive      : Boolean      = true
)