package com.gautam.quiz_app.data.model

data class QuizResponse(
    val success: Boolean,
    val data: List<Question>
)