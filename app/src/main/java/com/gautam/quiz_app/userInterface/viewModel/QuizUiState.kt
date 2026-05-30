package com.gautam.quiz_app.userInterface.viewModel




import com.gautam.quiz_app.data.model.Question

data class QuizUiState(
    val questions    : List<Question> = emptyList(),
    val currentIndex : Int            = 0,
    val answers      : Map<Int, String> = emptyMap(), // index → chosen option
    val isLoading    : Boolean        = false,
    val error        : String?        = null,
    val timeLeft     : Int            = 0,
    val timeTaken    : Int            = 0,
    val isSubmitted  : Boolean        = false         // ← NEW
) {
    // Derived: true = correct, false = wrong, null = skipped
    val answerResults: Map<Int, Boolean?>
        get() = questions.mapIndexed { index, question ->
            val chosen = answers[index]
            index to when {
                chosen == null -> null
                chosen == question.correctAnswer -> true
                else -> false
            }
        }.toMap()

    val correctCount : Int get() = answerResults.values.count { it == true }
    val wrongCount   : Int get() = answerResults.values.count { it == false }
    val skippedCount : Int get() = answerResults.values.count { it == null }
}