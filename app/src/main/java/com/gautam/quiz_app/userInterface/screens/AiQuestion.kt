package com.gautam.quiz_app.userInterface.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gautam.quiz_app.data.model.AiQuestion
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

@Composable
fun AiQuizScreen(questions: List<AiQuestion>, onBack: () -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf(mapOf<Int, String>()) }
    var submitted by remember { mutableStateOf(false) }

    if (submitted) {
        QuizResultScreen(questions, userAnswers, onRetake = {
            userAnswers = mapOf(); submitted = false; onBack()
        })
        return
    }

    val q = questions.getOrNull(currentIndex) ?: return
    val total = questions.size
    val chosen = userAnswers[currentIndex]

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
            Spacer(Modifier.weight(1f))
            Text("${currentIndex + 1} / $total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        LinearProgressIndicator(
            progress = (currentIndex + 1).toFloat() / total,
            modifier = Modifier.fillMaxWidth().height(4.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Question ${currentIndex + 1}", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(q.question ?: "", style = MaterialTheme.typography.titleMedium, lineHeight = 24.sp)
            Spacer(Modifier.height(16.dp))

            q.options.forEach { option ->
                val isChosen = chosen == option
                val isCorrect = option == q.correctAnswer
                val containerColor = when {
                    chosen == null -> MaterialTheme.colorScheme.surface
                    isCorrect -> Color(0xFFE1F5EE)
                    isChosen -> Color(0xFFFAECE7)
                    else -> MaterialTheme.colorScheme.surface
                }
                val borderColor = when {
                    chosen == null && !isChosen -> MaterialTheme.colorScheme.outlineVariant
                    isCorrect -> Color(0xFF0F6E56)
                    isChosen -> Color(0xFF993C1D)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }

                Surface(
                    onClick = { if (chosen == null) userAnswers = userAnswers + (currentIndex to option) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = containerColor,
                    border = BorderStroke(0.5.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(option, modifier = Modifier.weight(1f), fontSize = 14.sp)
                        if (chosen != null) {
                            if (isCorrect) Icon(Icons.Default.Check, null, tint = Color(0xFF0F6E56), modifier = Modifier.size(18.dp))
                            else if (isChosen) Icon(Icons.Default.Close, null, tint = Color(0xFF993C1D), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            if (chosen != null && !q.explanation.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Box(Modifier.width(3.dp).fillMaxHeight().background(Color(0xFF0F6E56), RoundedCornerShape(2.dp)))
                        Spacer(Modifier.width(10.dp))
                        Text(q.explanation, fontSize = 13.sp, lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Nav buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (currentIndex > 0) {
                OutlinedButton(onClick = { currentIndex-- }, modifier = Modifier.weight(1f)) {
                    Text("← Prev")
                }
            }
            if (currentIndex < total - 1) {
                Button(onClick = { currentIndex++ }, modifier = Modifier.weight(1f)) {
                    Text("Next →")
                }
            } else {
                Button(
                    onClick = { submitted = true },
                    modifier = Modifier.weight(1f),
                    enabled = userAnswers.size == total
                ) { Text("Submit") }
            }
        }
    }
}

@Composable
fun QuizResultScreen(questions: List<AiQuestion>, answers: Map<Int, String>, onRetake: () -> Unit) {
    val score = questions.indices.count { answers[it] == questions[it].correctAnswer }
    val pct = (score * 100) / questions.size

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$score/${questions.size}", fontSize = 52.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary)
            Text("$pct% correct", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            val (badge, badgeColor) = when {
                pct >= 70 -> "Great job!" to Color(0xFF0F6E56)
                pct >= 40 -> "Keep practicing" to Color(0xFF854F0B)
                else -> "Needs improvement" to Color(0xFF993C1D)
            }
            Surface(shape = RoundedCornerShape(50), color = badgeColor.copy(alpha = .1f)) {
                Text(badge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = badgeColor, fontSize = 13.sp)
            }
        }

        Text("Review answers", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        questions.forEachIndexed { i, q ->
            val ans = answers[i]
            val correct = ans == q.correctAnswer
            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
                shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            if (correct) Icons.Default.Check else Icons.Default.Close, null,
                            tint = if (correct) Color(0xFF0F6E56) else Color(0xFF993C1D),
                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(q.question ?: "", fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp)
                    }
                    // show options with color coding
                    // ... same coloring logic as quiz screen
                    if (!q.explanation.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(q.explanation, fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
                    }
                }
            }
        }

        Button(onClick = onRetake,
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
            shape = RoundedCornerShape(12.dp)) {
            Text("Try again", fontSize = 16.sp)
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiQuizScreenPreview() {
    val sampleQuestions = listOf(
        AiQuestion(
            question = "What is DBMS?",
            options = listOf("Database", "Software", "Language", "Protocol"),
            correctAnswer = "Software",
            explanation = "DBMS is software used to manage databases"
        ),
        AiQuestion(
            question = "Which is NoSQL?",
            options = listOf("MySQL", "MongoDB", "Oracle", "PostgreSQL"),
            correctAnswer = "MongoDB",
            explanation = "MongoDB is a NoSQL database"
        )
    )

    MaterialTheme {
        AiQuizScreen(
            questions = sampleQuestions,
            onBack = {}
        )
    }
}