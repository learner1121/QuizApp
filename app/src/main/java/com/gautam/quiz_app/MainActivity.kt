package com.gautam.quiz_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.gautam.quiz_app.ui.theme.QuizAppTheme
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel
import androidx.credentials.CredentialManager
import com.gautam.quiz_app.navigation.AppHost

class MainActivity : ComponentActivity() {

    //  Credential Manager instance
    private val credentialManager by lazy {
        CredentialManager.create(this)
    }

    //  ViewModel
 val questionViewModel: QuestionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            QuizAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                   AppHost(innerPadding)

                }
            }
        }
    }
}
