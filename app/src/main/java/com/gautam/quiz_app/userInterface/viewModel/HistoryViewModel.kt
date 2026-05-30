// userInterface/viewModel/HistoryViewModel.kt
package com.gautam.quiz_app.userInterface.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.gautam.quiz_app.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        val user = FirebaseInstanceProvider.firebaseAuthInstance.currentUser
        if (user == null) {
            _uiState.value = HistoryUiState.Guest
            return
        }

        _uiState.value = HistoryUiState.Loading
        viewModelScope.launch {
            repo.getHistory(user.uid).fold(
                onSuccess = { list ->
                    if (list.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        val sorted = list.sortedByDescending { it.date }
                        _uiState.value = HistoryUiState.Success(
                            entries = sorted,
                            best    = sorted.maxByOrNull { it.percentage },
                            worst   = sorted.minByOrNull { it.percentage }
                        )
                    }
                },
                onFailure = {
                    _uiState.value = HistoryUiState.Error(
                        it.message ?: "Something went wrong"
                    )
                }
            )
        }
    }
}