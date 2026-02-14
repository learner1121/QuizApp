package com.gautam.quiz_app.userInterface.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

@Composable
fun ResultCalculation(viewModel: QuestionViewModel) {
    val results by viewModel.allResults.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.getScore()
    }

    LazyColumn {
        items(results) { result ->
            Text("Section: ${result.section}, Score: ${result.score}")
        }
    }
}

