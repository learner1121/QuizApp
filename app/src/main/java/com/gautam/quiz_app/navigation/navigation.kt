package com.gautam.quiz_app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app.ui.LoginScreen
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.gautam.quiz_app.userInterface.screens.AiQuizScreen
import com.gautam.quiz_app.userInterface.screens.HomeScreen
import com.gautam.quiz_app.userInterface.screens.QuestionFetch
import com.gautam.quiz_app.userInterface.screens.QuizSetupScreen
import com.gautam.quiz_app.userInterface.screens.RandomSectionScreen
import com.gautam.quiz_app.userInterface.screens.SectionScreen
import com.gautam.quiz_app.userInterface.screens.addQuestion
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

@Composable
fun AppHost(innerPadding: PaddingValues) {

    val navHostController = rememberNavController()

    val questionViewModel: QuestionViewModel = hiltViewModel()

    val user = remember { FirebaseInstanceProvider.firebaseAuthInstance.currentUser }
    // val startDestination = if (user != null) "HomeScreen" else "login"

    NavHost(navHostController, startDestination = "HomeScreen") {

        composable("HomeScreen") { HomeScreen(navHostController) }

        composable("addQue") {
            addQuestion(
                modifier = Modifier,
                viewModel = questionViewModel,
                limit = 1
            )
        }

        composable("SectionScreen") {
            SectionScreen(questionViewModel, navHostController)
        }

        composable(
            route = "QuestionFetch/{section}",
            arguments = listOf(navArgument("section") { type = NavType.StringType })
        ) { backStackEntry ->
            val section = backStackEntry.arguments?.getString("section") ?: ""

            QuestionFetch(
                viewModel = questionViewModel,
                section = section,
                navHostController = navHostController,
                limit = 10
            )
        }

        composable("login") { LoginScreen(navHostController) }

        composable("aiSetup") {
            QuizSetupScreen { topic, count, difficulty ->

                // call API
                questionViewModel.generateAiQuestion(topic, count,difficulty)

                // navigate to play screen
                navHostController.navigate("aiPlay")
            }
        }

        //  STEP 2: Play Screen
        composable("aiPlay") {

            val aiQuestions = questionViewModel.aiQuestions.observeAsState()
            val loading = questionViewModel.aiLoading.observeAsState()

            when {
                loading.value == true -> {
                    Box(Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }

                aiQuestions.value != null && aiQuestions.value!!.isNotEmpty() -> {
                    AiQuizScreen(
                        questions = aiQuestions.value!!,
                        onBack = { navHostController.popBackStack() }
                    )
                }

                else -> {
                    Box(Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center){
                        Text("No questions found")
                    }

                }
            }
        }

        composable("RandomSectionScreen") {
            RandomSectionScreen(navHostController)
        }
        composable(
            route = "RandomSection/{section}",
            arguments = listOf(navArgument("section") { type = NavType.StringType })
        ) { backStackEntry ->

            val section = backStackEntry.arguments?.getString("section") ?: ""

            QuestionFetch(
                viewModel = questionViewModel,
                section = section,
                navHostController = navHostController,
                limit = 10
            )
        }
    }
}