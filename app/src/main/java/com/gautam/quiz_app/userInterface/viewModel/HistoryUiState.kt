// userInterface/viewModel/HistoryUiState.kt
package com.gautam.quiz_app.userInterface.viewModel

import com.gautam.quiz_app.data.model.History

sealed interface HistoryUiState {
    data object Loading                          : HistoryUiState
    data object Guest                            : HistoryUiState
    data object Empty                            : HistoryUiState
    data class  Success(
        val entries  : List<History>,
        val best     : History?,
        val worst    : History?
    )                                            : HistoryUiState
    data class  Error(val message: String)       : HistoryUiState
}