// userInterface/viewModel/LeaderboardUiState.kt
package com.gautam.quiz_app.userInterface.viewModel

import com.gautam.quiz_app.data.model.LeaderboardEntry

sealed interface LeaderboardUiState {
    data object Loading                              : LeaderboardUiState
    data object Guest                                : LeaderboardUiState
    data object Empty                                : LeaderboardUiState
    data class  Success(val entries: List<LeaderboardEntry>) : LeaderboardUiState
    data class  Error(val message: String)           : LeaderboardUiState
}