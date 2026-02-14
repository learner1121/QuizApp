package com.example.app.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gautam.quiz_app.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.gautam.quiz_app.auth.GoogleAuthActivity
import com.gautam.quiz_app.userInterface.screens.LogoWithSlogan
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val loginSuccess = remember { mutableStateOf(false) }

    // checking current user
    val currentUser = FirebaseInstanceProvider.firebaseAuthInstance.currentUser
    if (currentUser != null) {
        // User already logged in → navigate to HomeScreen immediately
        LaunchedEffect(Unit) {
            navController.navigate("HomeScreen") {
                popUpTo("login") { inclusive = true }
            }
        }
        return
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loginSuccess.value = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            LogoWithSlogan()
            Card(
                onClick = {
                    val intent = Intent(context, GoogleAuthActivity::class.java)
                    launcher.launch(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row( modifier = Modifier.fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                    ){
                    Text(text = "Sign in with Google")
                    Spacer(Modifier.width(16.dp))
                    Image(painter = painterResource(R.drawable.google),
                        contentDescription = "Google Button",
                        Modifier.size(24.dp))
                }

            }
            Spacer(Modifier.height(8.dp))
            Card(onClick = {navController.navigate("HomeScreen")},
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(50.dp)) {
                Row(modifier = Modifier.fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center){ Text("Try as a Guest") }
            }
           /* Row() {
                Text("Don't have a account?")
                Text("Create One",
                    Modifier.clickable(onClick = {}),
                    color = Color.Blue)
            }*/

            Spacer(modifier = Modifier.height(20.dp))

            if (loginSuccess.value) {
                LaunchedEffect(Unit) {
                    navController.navigate("HomeScreen") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
        }
    }
}
