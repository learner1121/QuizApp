package com.gautam.quiz_app.userInterface.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizSetupScreen(onGenerate: (topic: String, count: Int, difficulty: String) -> Unit) {
    val topics = listOf("Data Structures","Algorithms","Operating Systems",
        "Computer Networks","DBMS","System Design","Machine Learning",
        "Web Development","Cybersecurity","Cloud Computing")
    val counts = listOf(10, 20, 30)
    val difficulties = listOf("Easy","Medium","Hard")

    var selectedTopic by remember { mutableStateOf(topics[0]) }
    var customTopic by remember { mutableStateOf("") }
    var selectedCount by remember { mutableStateOf(10) }
    var selectedDiff by remember { mutableStateOf("Medium") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("AI Quiz", style = MaterialTheme.typography.headlineMedium)
        Text("Generate a quiz on any topic",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(20.dp))
        SectionLabel("Topic")
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            topics.forEach { topic ->
                PillChip(
                    label = topic,
                    selected = selectedTopic == topic && customTopic.isEmpty(),
                    onClick = {
                        selectedTopic = topic
                        customTopic = ""
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("or enter manually",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = customTopic,
            onValueChange = { customTopic = it },
            placeholder = { Text("e.g. Blockchain, Compilers…") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(Modifier.height(16.dp))
        SectionLabel("Number of questions")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            counts.forEach { c ->
                PillChip(
                    label = c.toString(),
                    selected = selectedCount == c,
                    onClick = { selectedCount = c }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        SectionLabel("Difficulty")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            difficulties.forEach { d ->
                PillChip(
                    label = d,
                    selected = selectedDiff == d,
                    onClick = { selectedDiff = d },
                    activeColor = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                val topic = customTopic.ifBlank { selectedTopic }
                onGenerate(topic, selectedCount, selectedDiff)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Generate quiz", fontSize = 16.sp) }
    }
}

@Composable
fun PillChip(label: String, selected: Boolean, onClick: () -> Unit,
             activeColor: Color = MaterialTheme.colorScheme.primary) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) activeColor else MaterialTheme.colorScheme.surfaceVariant,
        border = if (!selected) BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 8.dp))
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizSetupScreenPreview() {
    MaterialTheme {
        QuizSetupScreen(
            onGenerate = { _, _, _ -> }
        )
    }
}