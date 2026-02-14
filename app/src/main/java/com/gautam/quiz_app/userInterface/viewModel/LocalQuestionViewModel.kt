package com.gautam.quiz_app.userInterface.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gautam.quiz_app.data.repository.LocalQuestionRepository
import com.gautam.quiz_app.roomDb.QuestionsLocal
import kotlinx.coroutines.launch

class LocalQuestionViewModel(private val repo: LocalQuestionRepository): ViewModel() {

    //val localQuestion: LiveData<List<QuestionsLocal>> = repo.allQuestion

    fun insert(questionsLocal: QuestionsLocal) = viewModelScope.launch{
        repo.addLocal(questionsLocal)
    }

}
class LocalQuestionViewModelFactory(
    private val repo: LocalQuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocalQuestionViewModel::class.java)) {
            return LocalQuestionViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
