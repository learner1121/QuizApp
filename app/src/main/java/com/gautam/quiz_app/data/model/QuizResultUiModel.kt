// data/model/QuizResultUiModel.kt
package com.gautam.quiz_app.data.model

data class QuizResultUiModel(
    val section    : String,
    val difficulty : String,
    val score      : Int,
    val total      : Int,
    val timeTaken  : Int,           // seconds
    val answers    : Map<Int, String>, // index → chosen option (null key = skipped)
    val questions  : List<Question>
) {
    val correct    : Int    get() = questions.indices.count { answers[it] == questions[it].correctAnswer }
    val wrong      : Int    get() = questions.indices.count { i -> answers[i] != null && answers[i] != questions[i].correctAnswer }
    val skipped    : Int    get() = questions.indices.count { answers[it] == null }
    val percentage : Float  get() = if (total == 0) 0f else (correct * 100f) / total

    val badge: String get() = when {
        percentage >= 90 -> "Expert"
        percentage >= 70 -> "Advanced"
        percentage >= 50 -> "Intermediate"
        else             -> "Beginner"
    }
}