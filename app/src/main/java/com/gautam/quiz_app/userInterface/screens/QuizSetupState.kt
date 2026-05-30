package com.gautam.quiz_app.userInterface.screens



data class QuizSetupState(
    val difficulty: String = "Easy",
    val questionCount: Int = 10,
    val timerPerQuestion: Int = 60
)