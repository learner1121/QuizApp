package com.gautam.quiz_app.data.model

data class LeaderboardResponse(
    val success: Boolean,
    val data: List<LeaderboardEntry>
)