package com.gautam.quiz_app.userInterface.screens

import Question
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

@Composable
fun addQuestion(viewModel: QuestionViewModel = viewModel(),modifier: Modifier ,limit: Int) {
    val context = LocalContext.current
    var questionText by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var option3 by remember { mutableStateOf("") }
    var option4 by remember { mutableStateOf("") }
    var marks by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text("Question") }
        )
        OutlinedTextField(
            value = section,
            onValueChange = { section = it },
            label = { Text("section") }
        )
        OutlinedTextField(
            value = option1,
            onValueChange = { option1 = it },
            label = { Text("option1") }
        )
        OutlinedTextField(
            value = option2,
            onValueChange = { option2 = it },
            label = { Text("option2") }
        )
        OutlinedTextField(
            value = option3,
            onValueChange = { option3 = it },
            label = { Text("option3") }
        )
        OutlinedTextField(
            value = option4,
            onValueChange = { option4 = it },
            label = { Text("option4") }
        )
        OutlinedTextField(
            value = marks,
            onValueChange = { marks = it },
            label = { Text("marks") }
        )

        val options = listOf(option1, option2, option3, option4)
        var selectedIndex by remember { mutableStateOf(-1) }
        options.forEachIndexed { index, opt ->
            if (opt.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                    Text(opt, Modifier.padding(8.dp))
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            if(questionText.isNotEmpty() && section.isNotEmpty() && selectedIndex != -1){
                val question = Question(
                    questionText = questionText,
                    section = section,
                    options = options,
                    correctAnswer = options[selectedIndex],
                    marks = marks.toInt(),
                )
                viewModel.addQuestion(section,question,limit)
                Toast.makeText(context,"Question Added", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Add Question")
        }
    }
}