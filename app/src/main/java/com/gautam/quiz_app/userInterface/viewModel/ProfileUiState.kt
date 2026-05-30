package com.gautam.quiz_app.userInterface.viewModel

import com.gautam.quiz_app.data.model.UserProfile

sealed interface ProfileUiState {
    data object Loading                        : ProfileUiState
    data object Guest                          : ProfileUiState
    data class  Success(val profile: UserProfile,
                        val displayName : String,
                        val email       : String,
                        val photoUrl    : String)  : ProfileUiState
    data class  Error(val message: String)     : ProfileUiState
}