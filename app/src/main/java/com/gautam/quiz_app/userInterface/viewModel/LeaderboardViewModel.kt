// userInterface/viewModel/LeaderboardViewModel.kt
package com.gautam.quiz_app.userInterface.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.gautam.quiz_app.data.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Tab definition — order matches TabRow
enum class LeaderboardTab(val label: String, val apiKey: String) {
    Overall("Overall", "overall"),
    OOPs   ("OOPs",    "OOPs"),
    DBMS   ("DBMS",    "DBMS"),
    OS     ("OS",      "OS"),
    CN     ("CN",      "CN")
}

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repo: LeaderboardRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(LeaderboardTab.Overall)
    val selectedTab: StateFlow<LeaderboardTab> = _selectedTab.asStateFlow()

    // One state slot per tab — avoids re-fetching on tab switch
    private val _states = MutableStateFlow(
        LeaderboardTab.entries.associateWith<LeaderboardTab, LeaderboardUiState> {
            LeaderboardUiState.Loading
        }
    )
    val states: StateFlow<Map<LeaderboardTab, LeaderboardUiState>> = _states.asStateFlow()

    // Current user id for row highlighting
    val currentUserId: String? =
        FirebaseInstanceProvider.firebaseAuthInstance.currentUser?.uid

    // Whether the user is a guest
    val isGuest: Boolean =
        FirebaseInstanceProvider.firebaseAuthInstance.currentUser == null

    init {
        if (!isGuest) loadTab(LeaderboardTab.Overall)
    }

    fun selectTab(tab: LeaderboardTab) {
        _selectedTab.value = tab
        // Lazy-load: only fetch if not already loaded
        val current = _states.value[tab]
        if (current is LeaderboardUiState.Loading) loadTab(tab)
    }

    fun retry(tab: LeaderboardTab) {
        setTabState(tab, LeaderboardUiState.Loading)
        loadTab(tab)
    }

    private fun loadTab(tab: LeaderboardTab) {
        viewModelScope.launch {
            val result = if (tab == LeaderboardTab.Overall) repo.getOverall()
            else repo.getBySection(tab.apiKey)

            result.fold(
                onSuccess = { list ->
                    setTabState(
                        tab,
                        if (list.isEmpty()) LeaderboardUiState.Empty
                        else LeaderboardUiState.Success(list)
                    )
                },
                onFailure = {
                    setTabState(tab, LeaderboardUiState.Error(it.message ?: "Unknown error"))
                }
            )
        }
    }

    private fun setTabState(tab: LeaderboardTab, state: LeaderboardUiState) {
        _states.value = _states.value.toMutableMap().also { it[tab] = state }
    }
}