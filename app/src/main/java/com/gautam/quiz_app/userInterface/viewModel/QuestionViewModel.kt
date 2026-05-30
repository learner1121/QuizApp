package com.gautam.quiz_app.userInterface.viewModel

import Question
import androidx.lifecycle.*
import com.gautam.quiz_app.data.model.AiQuestion
import com.gautam.quiz_app.data.repository.LocalQuestionRepository
import com.gautam.quiz_app.data.repository.QuestionRepository
import com.gautam.quiz_app.data.repository.ResultRepository
import com.gautam.quiz_app.roomDb.QuestionResult
import com.gautam.quiz_app.roomDb.QuestionsLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repo: QuestionRepository,
    private val localRepo: LocalQuestionRepository,
    private val resultRepo: ResultRepository
) : ViewModel() {

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> get() = _questions

    // ---------------- AI STATE ---------------- //

    private val _aiQuestions = MutableLiveData<List<AiQuestion>>()
    val aiQuestions: LiveData<List<AiQuestion>> get() = _aiQuestions

    private val _aiLoading = MutableLiveData<Boolean>()
    val aiLoading: LiveData<Boolean> get() = _aiLoading

    private val _aiError = MutableLiveData<String?>()
    val aiError: LiveData<String?> get() = _aiError

    fun setAiError(message: String) {
        _aiError.value = message
    }

    // ---------------- NORMAL QUESTIONS ---------------- //

    fun getQuestion(section: String, limit: Int) {
        viewModelScope.launch {
            val cached = withContext(Dispatchers.IO) {
                localRepo.getQuestionsBySection(section)
            }

            if (cached.isNotEmpty()) {
                _questions.value = cached.map {
                    Question(
                        questionText = it.questionText,
                        section = it.section,
                        options = it.options,
                        correctAnswer = it.correctAnswer,
                        marks = it.marks
                    )
                }
            } else {
                try {
                    val response = repo.getQuestion(section, limit)
                    if (response.isSuccessful) {
                        response.body()?.let { list ->
                            _questions.value = list

                            list.forEach { question ->
                                val local = QuestionsLocal(
                                    questionText = question.questionText,
                                    options = question.options,
                                    correctAnswer = question.correctAnswer,
                                    section = question.section,
                                    marks = question.marks
                                )
                                localRepo.addLocal(local)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // ---------------- AI FUNCTION ---------------- //

    fun generateAiQuestion(
        topic: String,
        count: Int,
        difficulty: String
    ) {
        viewModelScope.launch {

            _aiLoading.value = true
            _aiError.value = null
            _aiQuestions.value = emptyList()

            try {
                val response = repo.generateQuestion(topic, difficulty, count)

                if (response.isSuccessful) {
                    _aiQuestions.value = response.body() ?: emptyList()
                } else {
                    _aiError.value = "Failed: ${response.code()}"
                }

            } catch (e: Exception) {
                _aiError.value = e.message
            }

            _aiLoading.value = false
        }
    }

    // ---------------- REST (UNCHANGED) ---------------- //

    fun addQuestion(section: String, question: Question, limit: Int) {
        viewModelScope.launch {
            repo.addQuestion(section, question)
            getQuestion(section, limit)
        }
    }

    private val _randomQuestion = MutableLiveData<List<Question>>()
    val randomQuestion: LiveData<List<Question>> = _randomQuestion

    fun randomQuestion(section: String, limit: Int) {
        viewModelScope.launch {
            try {
                val response = repo.randomQuestions(section, limit)
                if (response.isSuccessful) {
                    _randomQuestion.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addResult(result: QuestionResult) {
        viewModelScope.launch {
            resultRepo.addScore(result)
        }
    }

    private val _allResults = MutableLiveData<List<QuestionResult>>()
    val allResults: LiveData<List<QuestionResult>> = _allResults

    fun getScore() {
        viewModelScope.launch {
            _allResults.value = resultRepo.getScore()
        }
    }
}