package com.gautam.quiz_app.userInterface.viewModel




import com.gautam.quiz_app.data.model.Question

data class QuizUiState(
    val questions    : List<Question> = emptyList(),
    val currentIndex : Int            = 0,
    val answers      : Map<Int, String> = emptyMap(),  // index → chosen option
    val timeLeft     : Int            = 0,
    val timeTaken    : Int              = 0,
    val isLoading    : Boolean        = true,
    val error        : String?        = null
)