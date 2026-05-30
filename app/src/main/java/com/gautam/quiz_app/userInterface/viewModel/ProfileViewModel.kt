package com.gautam.quiz_app.userInterface.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.gautam.quiz_app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val auth = FirebaseInstanceProvider.firebaseAuthInstance

    init { load() }

    fun load() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _uiState.value = ProfileUiState.Guest
            return
        }

        _uiState.value = ProfileUiState.Loading

        viewModelScope.launch {
            repo.getProfile(firebaseUser.uid).fold(
                onSuccess = { profile ->
                    _uiState.value = ProfileUiState.Success(
                        profile     = profile,
                        displayName = firebaseUser.displayName
                            ?: profile.name.ifBlank { "User" },
                        email       = firebaseUser.email
                            ?: profile.email,
                        photoUrl    = firebaseUser.photoUrl?.toString()
                            ?: profile.photoUrl
                    )
                },
                onFailure = {
                    _uiState.value = ProfileUiState.Error(
                        it.message ?: "Failed to load profile"
                    )
                }
            )
        }
    }

    fun logout(onComplete: () -> Unit) {
        auth.signOut()
        _uiState.value = ProfileUiState.Guest
        onComplete()
    }
}