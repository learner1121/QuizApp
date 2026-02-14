package com.gautam.quiz_app.userInterface.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gautam.quiz_app.roomDb.QuestionDB
import com.gautam.quiz_app.roomDb.QuestionResult
import com.gautam.quiz_app.ui.theme.interFontFamily
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.gautam.quiz_app.R
import com.gautam.quiz_app.ui.theme.mitrFontFamily
import com.gautam.quiz_app.ui.theme.poppinsFontFamily


@Composable
fun QuestionFetch(
    viewModel: QuestionViewModel = viewModel(),
    section: String,
    navHostController: NavHostController,
    limit: Int
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val cardWidth = screenWidth - 10

    val context = LocalContext.current
    val questionList by viewModel.questions.observeAsState(emptyList())
    viewModel.getQuestion(section, limit)

    val selectedOptions = remember { mutableStateMapOf<Int, String>() }
    var score by remember { mutableStateOf<Int?>(null) }
    var subMitted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Header Row (same as random quiz)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    painter = painterResource(R.drawable.backarrow),
                    contentDescription = "Back_Arrow",
                    modifier = Modifier.size(25.dp)
                )
            }
            Spacer(Modifier.width(6.dp))

            Text(
                "$section Quiz",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.DarkGray,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = interFontFamily,
                    fontSize = 18.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            if (questionList.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                itemsIndexed(questionList) { index, q ->

                    Column(Modifier.fillMaxWidth()) {

                        Card(
                            Modifier.width(cardWidth.dp),
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {

                            Text(
                                "Q${index + 1}. ${q.questionText}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontFamily = mitrFontFamily
                                ),
                                modifier = Modifier.padding(12.dp)
                            )

                            Spacer(Modifier.height(8.dp))

                            q.options.forEach { option ->
                                val selected = selectedOptions[index] == option

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .selectable(
                                            selected = selected,
                                            onClick = { selectedOptions[index] = option }
                                        )
                                        .padding(start = 12.dp, bottom = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = { selectedOptions[index] = option }
                                    )

                                    Text(
                                        option,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.W400,
                                            fontSize = 12.sp,
                                            color = Color.DarkGray,
                                            fontFamily = poppinsFontFamily
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.height(6.5.dp))

                            if (subMitted) {
                                Text(
                                    "Correct Answer : ${q.correctAnswer}",
                                    color = Color(0xFF8BC34A),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                    }
                }
            }

            // Submit Button Section
            item {
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        subMitted = true

                        score = questionList.indices.count { i ->
                            selectedOptions[i] == questionList[i].correctAnswer
                        }

                        val result = QuestionResult(
                            section = section,
                            score = score ?: 0,
                            totalQuestion = questionList.size,
                            date = System.currentTimeMillis()
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.addResult(result)
                        }

                        Toast.makeText(
                            context,
                            "Your Score: ${score}/${questionList.size}",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = questionList.isNotEmpty() && score == null
                ) {
                    Text("Submit Quiz")
                }

                score?.let {
                    Text(
                        "Score: $it/${questionList.size}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.DarkGray,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    Spacer(Modifier.height(8.dp))
}

@Composable
fun RandomQuestionFetch(viewModel: QuestionViewModel = viewModel(),
                        section: String,
                        navHostController: NavHostController,
                        limit: Int
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val cardwidth = screenWidth - 10;

    val context = LocalContext.current
    val QuestionList by viewModel.randomQuestion.observeAsState(emptyList())
    viewModel.randomQuestion(section, limit)
    val selectedOptions =
        remember { mutableStateMapOf<Int, String>() } // Tracks selected option per question
    var score by remember { mutableStateOf<Int?>(null) } // Stores quiz score, null until submit
    var subMitted by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navHostController.navigate("RandomSectionScreen") }) {
                Icon(painter = painterResource(R.drawable.backarrow,
                    ), "Back_Arrow",
                    Modifier.size(25.dp))
            }
            Spacer(Modifier.width(6.dp))
            Text(
                "Random $section Quiz",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.DarkGray,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = interFontFamily,
                    fontSize = 18.sp

                )
            )

        }

        // Spacer(Modifier.weight(0.1f))

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()
            .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            ) {
            if (QuestionList.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                itemsIndexed(QuestionList)
                { Index, que ->
                    Column(Modifier.fillMaxWidth()) {

                        Card(
                            Modifier
                                .width(cardwidth.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Text("Q${Index + 1}. ${que.questionText}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    fontFamily = mitrFontFamily
                                ))

                            Spacer(Modifier.height(8.dp))
                            que.options.forEach { option ->
                                val selected = selectedOptions[Index] == option
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.selectable(
                                        selected = selected,
                                        onClick = { selectedOptions[Index] = option }
                                    )
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = { selectedOptions[Index] = option },
                                    )
                                    Text(option,

                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.W400,
                                            fontSize = 12.sp,
                                            color = Color.DarkGray,
                                            fontFamily = poppinsFontFamily
                                        )
                                        )
                                }
                            }
                            Spacer(Modifier.height(6.5.dp))
                            if (subMitted) {
                                Text(
                                    "Correct Answer : ${que.correctAnswer}",
                                    color = Color(0xFF8BC34A)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.5.dp))

                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        subMitted = !subMitted
                        // Calculate total score
                        score = QuestionList.indices.count { i ->
                            selectedOptions[i] == QuestionList[i].correctAnswer
                        }

                        val result = QuestionResult(
                            section = section,
                            score = score ?: 0,
                            totalQuestion = QuestionList.size,
                            date = System.currentTimeMillis()
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            val db = QuestionDB.getDatabase(context)
                            db.quizResultDao().addScore(result)
                        }
                        Toast.makeText(
                            context,
                            "Your Score: $score/${QuestionList.size}",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = QuestionList.isNotEmpty() && score == null // Enable only if questions exist and not submitted
                ) {
                    Text("Submit Quiz")

                }

                // Display score after submission
                score?.let {
                    Text(
                        "Score: $it/${QuestionList.size}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.DarkGray,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}