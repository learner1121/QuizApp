package com.gautam.quiz_app.userInterface.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser

@Composable
fun MainScreen(
    user: FirebaseUser?,             // Firebase user after login
    onSignOutClick: () -> Unit       // Callback for sign-out
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (user != null) {
                Text(
                    text = "Welcome, ${user.displayName ?: "User"}",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: ${user.email ?: "No email"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onSignOutClick) {
                    Text("Sign Out")
                }
            } else {
                Text(
                    text = "No user logged in",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
