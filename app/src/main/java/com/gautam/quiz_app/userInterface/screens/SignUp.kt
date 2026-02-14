package com.gautam.quiz_app.userInterface.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gautam.quiz_app.auth.FirebaseAuthManager

@Composable
fun SignUpUi() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val activity = context as Activity  // ✔ proper Activity reference

    val firebaseManager = remember { FirebaseAuthManager(activity) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LogoWithSlogan()

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.Cyan,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            ),
            placeholder = { Text("email") }
        )
        Spacer(Modifier.height(6.5.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.Cyan,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            ),
            placeholder = { Text("password") }
        )
        Spacer(Modifier.height(6.5.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.Cyan,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray
            ),
            placeholder = { Text("confirmPassword") }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                if (validateInputs(email, password, confirmPassword, context)) {

                    firebaseManager.sendEmailLink(email) { success, error ->
                        if (success) {
                            Toast.makeText(context, "Magic link sent!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, error ?: "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        ) {
            Text("Sign up with Email & Password")
        }
        Row {
            Text("Already have an Account?")
            Text(
                "login",
                Modifier.clickable {
                    Toast.makeText(context, "Add Route", Toast.LENGTH_SHORT).show()
                },
                color = Color.Blue
            )
        }
    }
}

// 🔥 Returns true if valid, false if not
fun validateInputs(email: String, password: String, confirmPassword: String, context: Context): Boolean {
    return when {
        email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
            Toast.makeText(context, "All fields are mandatory", Toast.LENGTH_SHORT).show()
            false
        }

        password != confirmPassword -> {
            Toast.makeText(context, "Passwords must match", Toast.LENGTH_SHORT).show()
            false
        }

        password.length < 6 -> {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            false
        }

        else -> true
    }
}

@Preview
@Composable
fun PreviewSignUp() {
    // Preview cannot show Activity, so skip Activity-related code
}
