package com.gautam.quiz_app.data.model

data class GenerateRequest(
    val topic: String,
    val difficulty: String,
    val count: Int
)