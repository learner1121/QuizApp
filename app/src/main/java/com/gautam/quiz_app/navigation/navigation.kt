package com.gautam.quiz_app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gautam.quiz_app.data.model.QuizResultUiModel
import com.gautam.quiz_app.userInterface.screens.HistoryScreen
import com.gautam.quiz_app.userInterface.screens.HomeScreen
import com.gautam.quiz_app.userInterface.screens.LeaderboardScreen
import com.gautam.quiz_app.userInterface.screens.ProfileScreen
/*import com.gautam.quiz_app.userInterface.screens.QuestionFetch*/
import com.gautam.quiz_app.userInterface.screens.QuizResultScreen
import com.gautam.quiz_app.userInterface.screens.QuizScreen
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
            SectionScreen( navHostController)
        }

        /*composable(
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
        }*/

        composable("login") { LoginScreen(navHostController) }



        // Replace the old "aiSetup" composable and add these two routes:

// ── Quiz Setup (Normal + Random) ───────────────────────────────────────────────
        composable(
            route = "quizSetup/{section}/{isRandom}",
            arguments = listOf(
                navArgument("section")  { type = NavType.StringType },
                navArgument("isRandom") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val section  = backStackEntry.arguments?.getString("section")  ?: ""
            val isRandom = backStackEntry.arguments?.getBoolean("isRandom") ?: false

            QuizSetupScreen(
                navController    = navHostController,
                section          = section,
                isRandom         = isRandom
            )
        }

// ── Quiz Play ──────────────────────────────────────────────────────────────────
        composable(
            route = "quizPlay/{section}/{difficulty}/{questionCount}/{timerPerQuestion}/{isRandom}",
            arguments = listOf(
                navArgument("section")          { type = NavType.StringType },
                navArgument("difficulty")       { type = NavType.StringType },
                navArgument("questionCount")    { type = NavType.IntType    },
                navArgument("timerPerQuestion") { type = NavType.IntType    },
                navArgument("isRandom")         { type = NavType.BoolType   }
            )
        ) { backStackEntry ->
            val section          = backStackEntry.arguments?.getString("section")          ?: ""
            val difficulty       = backStackEntry.arguments?.getString("difficulty")       ?: "Easy"
            val questionCount    = backStackEntry.arguments?.getInt("questionCount")       ?: 10
            val timerPerQuestion = backStackEntry.arguments?.getInt("timerPerQuestion")    ?: 60
            val isRandom         = backStackEntry.arguments?.getBoolean("isRandom")        ?: false

            // Replace with your QuizScreen composable when it exists
            // QuizScreen(section, difficulty, questionCount, timerPerQuestion, isRandom, navHostController)
            QuizScreen(
                navController    = navHostController,
                section          = section,
                difficulty       = difficulty,
                questionCount    = questionCount,
                timerPerQuestion = timerPerQuestion,
                isRandom         = isRandom
            )
        }
        // AppHost.kt — replace the stub quizResult composable

        composable(
            route = "quizResult/{section}/{difficulty}",
            arguments = listOf(
                navArgument("section")    { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val section    = backStackEntry.arguments?.getString("section")    ?: ""
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Easy"

            val quizState by questionViewModel.quizUiState.collectAsState()

            // Build result model from shared ViewModel state
            val result = QuizResultUiModel(
                section = section,
                difficulty = difficulty,
                score = quizState.answers.values
                    .zip(quizState.questions)
                    .count { (ans, q) -> ans == q.correctAnswer },
                total = quizState.questions.size,
                timeTaken = quizState.timeTaken,
                answers = quizState.answers,
                questions = quizState.questions
            )

            QuizResultScreen(
                navController = navHostController,
                result        = result,
                viewModel     = questionViewModel
            )
        }

        composable("RandomSectionScreen") {
            RandomSectionScreen(navHostController)
        }
       /* composable(
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
        }*/
        composable("history") {
            HistoryScreen(navController = navHostController)
        }

        composable("leaderboard") {
            LeaderboardScreen(navController = navHostController)
        }
        composable("profile") {
            ProfileScreen(navController = navHostController)
        }
    }
}