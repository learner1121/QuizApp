package com.gautam.quiz_app.data.model

import com.google.gson.annotations.SerializedName

data class AiQuestion(
    @SerializedName("question") val question: String?,
    @SerializedName("options") val options: List<String>,
    @SerializedName("correctAnswer") val correctAnswer: String?,
    @SerializedName("explanation") val explanation: String?,
)
