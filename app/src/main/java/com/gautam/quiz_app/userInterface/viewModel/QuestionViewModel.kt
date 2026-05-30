package com.gautam.quiz_app.userInterface.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gautam.quiz_app.data.model.Question
import com.gautam.quiz_app.data.model.QuizResultUiModel
import com.gautam.quiz_app.data.repository.LocalQuestionRepository
import com.gautam.quiz_app.data.repository.QuestionRepository
import com.gautam.quiz_app.roomDb.QuestionsLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.lastIndex

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repo      : QuestionRepository,
    private val localRepo : LocalQuestionRepository
) : ViewModel() {

    // ── Quiz UI state ──────────────────────────────────────────────────────
    private val _quizUiState = MutableStateFlow(QuizUiState())
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

    private var timerJob: Job? = null

    // ── Fetch questions (normal, section-based) ────────────────────────────
    fun fetchQuestions(section: String, limit: Int, difficulty: String) {
        viewModelScope.launch {
            _quizUiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Always hit the network — difficulty & limit are API concerns
                val response = repo.getQuestion(section, limit, difficulty)

                if (response.isSuccessful) {
                    val questions = response.body()?.data ?: emptyList()

                    // Write-through cache: store whatever the API returned
                    questions.forEach { q -> localRepo.addLocal(q.toLocal()) }

                    _quizUiState.update {
                        QuizUiState(questions = questions, isLoading = false)
                    }
                } else {
                    // Network failed — fall back to Room with client-side filtering
                    fallbackToRoom(section, limit, difficulty)
                }

            } catch (e: Exception) {
                // No network — fall back to Room
                fallbackToRoom(section, limit, difficulty)
            }
        }
    }

    private suspend fun fallbackToRoom(section: String, limit: Int, difficulty: String) {
        val cached = localRepo.getQuestionsBySection(section)

        if (cached.isNotEmpty()) {
            val questions = cached
                .filter { it.difficulty.equals(difficulty, ignoreCase = true) }
                .shuffled()
                .take(limit)
                .map { it.toDomain() }

            if (questions.isNotEmpty()) {
                _quizUiState.update {
                    QuizUiState(questions = questions, isLoading = false)
                }
            } else {
                _quizUiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No cached questions for difficulty: $difficulty"
                    )
                }
            }
        } else {
            _quizUiState.update {
                it.copy(isLoading = false, error = "No internet and no cached questions")
            }
        }
    }

    // ── Fetch questions (random) ───────────────────────────────────────────
    fun fetchRandomQuestions(section: String, limit: Int, difficulty: String) {
        viewModelScope.launch {
            _quizUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = repo.randomQuestions(section, limit, difficulty)
                if (response.isSuccessful) {
                    val questions = response.body()?.data ?: emptyList()  // ← extract .data
                    _quizUiState.update {
                        QuizUiState(questions = questions, isLoading = false)
                    }
                } else {
                    _quizUiState.update {
                        it.copy(isLoading = false, error = "Failed (${response.code()})")
                    }
                }
            } catch (e: Exception) {
                _quizUiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    // ── Timer ──────────────────────────────────────────────────────────────
    fun startTimer(seconds: Int) {
        timerJob?.cancel()
        _quizUiState.update { it.copy(timeLeft = seconds) }

        timerJob = viewModelScope.launch {
            for (remaining in seconds downTo 0) {
                _quizUiState.update { it.copy(timeLeft = remaining) }
                if (remaining == 0) {
                    autoAdvance()
                    break
                }
                delay(1_000L)
            }
        }
    }

    private fun autoAdvance() {
        val state = _quizUiState.value
        val next  = state.currentIndex + 1
        if (next < state.questions.size) {
            _quizUiState.update { it.copy(currentIndex = next) }
        }
    }

    // ── Navigation ─────────────────────────────────────────────────────────
    fun goToNext(timerPerQuestion: Int) {
        timerJob?.cancel()
        _quizUiState.update { state ->
            val next = (state.currentIndex + 1).coerceAtMost(state.questions.lastIndex)
            state.copy(currentIndex = next)
        }
    }

    fun goToPrev() {
        timerJob?.cancel()
        _quizUiState.update { state ->
            val prev = (state.currentIndex - 1).coerceAtLeast(0)
            state.copy(currentIndex = prev)
        }
    }

    fun selectAnswer(option: String) {
        _quizUiState.update { state ->
            val updated = state.answers + (state.currentIndex to option)
            state.copy(answers = updated)
        }
    }

    fun submitQuiz(totalSeconds: Int) {
        timerJob?.cancel()
        _quizUiState.update { it.copy(timeTaken = totalSeconds - it.timeLeft) }
    }

    // ── Reset (call when leaving quiz) ─────────────────────────────────────
    fun resetQuiz() {
        timerJob?.cancel()
        _quizUiState.value = QuizUiState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private fun difficultyToMarks(difficulty: String): Int = when (difficulty) {
        "Medium" -> 2
        "Hard"   -> 3
        else     -> 1
    }

    private fun QuestionsLocal.toDomain() = Question(
        questionText  = questionText,
        section       = section,
        options       = options,
        correctAnswer = correctAnswer,
        difficulty    = difficulty,
        explanation   = explanation,
        marks         = marks,
        isActive      = isActive
    )

    private fun Question.toLocal() = QuestionsLocal(
        questionText  = questionText,
        section       = section,
        options       = options,
        correctAnswer = correctAnswer,
        difficulty    = difficulty,
        explanation   = explanation,
        marks         = marks,
        isActive      = isActive
    )

    // ── Submit result to backend if user is logged in ──────────────────────
    fun submitResultIfAuthenticated(result: QuizResultUiModel) {
        val user = com.gautam.quiz_app.auth.FirebaseInstanceProvider
            .firebaseAuthInstance.currentUser ?: return

        viewModelScope.launch {
            try {
                val payload = com.gautam.quiz_app.data.model.HistoryEntry(
                    userId     = user.uid,
                    section    = result.section,
                    difficulty = result.difficulty,
                    score      = result.correct,
                    total      = result.total,
                    timeTaken  = result.timeTaken,
                    date       = System.currentTimeMillis()
                )
                repo.postResult(payload)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}