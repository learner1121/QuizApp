package com.gautam.quiz_app.userInterface.viewModel

import Question
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gautam.quiz_app.data.repository.LocalQuestionRepository
import com.gautam.quiz_app.data.repository.QuestionRepository
import com.gautam.quiz_app.data.repository.ResultRepository
import com.gautam.quiz_app.roomDb.QuestionResult
import com.gautam.quiz_app.roomDb.QuestionsLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher


class QuestionViewModel( private val repo: QuestionRepository,
    private val localRepo: LocalQuestionRepository,
    private val resultRepo: ResultRepository
    ) : ViewModel(){

    private val _questions = MutableLiveData<List<Question>>()
    val questions : LiveData<List<Question>> get() = _questions


    fun getQuestion(section: String, limit: Int) {
        viewModelScope.launch {
            val cached = withContext(Dispatchers.IO){
                localRepo.getQuestionsBySection(section).also {
                    Log.d("LocalRepo", "Cached questions size: ${it.size}")
                }
            }

            if (cached.isNotEmpty()){
                _questions.value = cached.map {
                    Question(
                        questionText = it.questionText,
                        section = it.section,
                        options = it.options,
                        correctAnswer = it.correctAnswer,
                        marks = it.marks
                    )
                }
            }
            else{
                try {
                    val response = withContext(Dispatchers.IO){repo.getQuestion(section,limit)}
                    if (response.isSuccessful ) {
                        response.body()?.let { list ->
                            _questions.value = list
                            // Step 3: Save to local DB
                            list.forEach { question ->
                                val localQuestion = QuestionsLocal(
                                    questionText = question.questionText,
                                    options = question.options,
                                    correctAnswer = question.correctAnswer,
                                    section = question.section,
                                    marks = question.marks
                                )
                                localRepo.addLocal(localQuestion)
                            }
                        }
                    } else {
                        println("Error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun addQuestion(section: String, question: Question,limit: Int){
        viewModelScope.launch {
           try {
               repo.addQuestion(section,question)
               getQuestion(section,limit)
           }catch (e: Exception){
               e.printStackTrace()
           }
        }
    }
    // Get Random Question
    private val _randomQuestion = MutableLiveData<List<Question>>()
    val randomQuestion : LiveData<List<Question>> get() = _randomQuestion

    fun randomQuestion(section: String, limit: Int){
        viewModelScope.launch {
            try {
                val  respone1 = repo.randomQuestions(section,limit)
                if (respone1.isSuccessful){
                    _randomQuestion.value = respone1.body()
                }
            } catch (e: Exception) {
               e.printStackTrace()
            }
        }
    }

    fun addResult(result: QuestionResult){
        viewModelScope.launch {
            try {
                resultRepo.addScore(result)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    private val _allResults = MutableLiveData<List<QuestionResult>>()
    val allResults: LiveData<List<QuestionResult>> = _allResults
    fun getScore(){
        viewModelScope.launch {
            try {
               _allResults.value = resultRepo.getScore()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
class QuestionViewModelFactory(
    private val repo: QuestionRepository,
    private val localRepo: LocalQuestionRepository,
    private val resultRepo: ResultRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            return QuestionViewModel(repo, localRepo,resultRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

