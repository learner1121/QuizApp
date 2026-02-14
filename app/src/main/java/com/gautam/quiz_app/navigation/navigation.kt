package com.gautam.quiz_app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app.ui.LoginScreen
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.gautam.quiz_app.data.repository.LocalQuestionRepository
import com.gautam.quiz_app.data.repository.QuestionRepository
import com.gautam.quiz_app.data.repository.ResultRepository
import com.gautam.quiz_app.roomDb.QuestionDB
import com.gautam.quiz_app.userInterface.screens.HomeScreen
import com.gautam.quiz_app.userInterface.screens.ProfileScreen
import com.gautam.quiz_app.userInterface.screens.QuestionFetch
import com.gautam.quiz_app.userInterface.screens.RandomQuestionFetch
import com.gautam.quiz_app.userInterface.screens.RandomSectionScreen
import com.gautam.quiz_app.userInterface.screens.ResultCalculation
import com.gautam.quiz_app.userInterface.screens.SectionScreen
import com.gautam.quiz_app.userInterface.screens.SignUpUi
import com.gautam.quiz_app.userInterface.screens.addQuestion
import com.gautam.quiz_app.userInterface.viewModel.LocalQuestionViewModel
import com.gautam.quiz_app.userInterface.viewModel.LocalQuestionViewModelFactory
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppHost(innerPadding: PaddingValues) {

    val navHostController = rememberNavController()
    val context = LocalContext.current
    val dao = QuestionDB.getDatabase(context).questionDao()
    val repo = LocalQuestionRepository(dao)
    val resultDao = QuestionDB.getDatabase(context).quizResultDao()

    val questionViewModel: QuestionViewModel = viewModel(
        factory = QuestionViewModelFactory(
            QuestionRepository(),
            localRepo = LocalQuestionRepository(dao),
            resultRepo = ResultRepository(resultDao)
        )
    )

    val localViewModel: LocalQuestionViewModel = viewModel(
        factory = LocalQuestionViewModelFactory(repo)
    )


    val user = remember { FirebaseInstanceProvider.firebaseAuthInstance.currentUser }
    val startDestination = if (user != null) "HomeScreen" else "login"

    NavHost(navHostController, startDestination = startDestination){

        composable("HomeScreen") { HomeScreen(navHostController) }

        //QuestionAdd Route not implemented yet only for admin
        composable("addQue") { addQuestion(
            modifier = Modifier,
            viewModel = questionViewModel,
            limit = 1
        ) }

        composable("SectionScreen") { SectionScreen(questionViewModel,navHostController) }
        //get section by question
        composable (route = "QuestionFetch/{section}",
            arguments = listOf(navArgument("section"){type = NavType.StringType})
        )
        {backStackEntry->
            val section = backStackEntry.arguments?.getString("section")?:""
            QuestionFetch(
                viewModel = questionViewModel,
                section = section,
                navHostController =navHostController,
                limit = 10
            )

        }

        //Random Question fetch routes
        composable("RandomSectionScreen") { RandomSectionScreen(navHostController)}

        composable (route ="RandomSection/{section}",
            arguments = listOf(navArgument("section"){type= NavType.StringType}))
        {backStackEntry->
            val section = backStackEntry.arguments?.getString("section") ?: ""
            RandomQuestionFetch(
                viewModel = questionViewModel,
                section = section,
                navHostController = navHostController,
                limit = 20
            )
        }

        //result later integration after basic working version
       // composable("Result") { ResultCalculation(questionViewModel) }
//        //result tabulation local
//        composable (route = "Result/{section}",
//            arguments = listOf(navArgument("section"){type = NavType.StringType})
//        ){ backStackEntry->
//            val section = backStackEntry.arguments?.getString("section") ?: ""
//            ResultCalculation(
//                navController = navHostController,
//                viewModel = questionViewModel
//            )
//        }


        //login
        composable("login") { LoginScreen(navHostController) }

        //Profile try with developer Profile
        composable ("Profile"){ ProfileScreen(navHostController) }



        }
    }
