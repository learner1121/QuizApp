package com.gautam.quiz_app.userInterface.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.gautam.quiz_app.auth.FirebaseInstanceProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController) {
    val user = FirebaseInstanceProvider.firebaseAuthInstance.currentUser
    val name = user?.displayName ?: "Unknown"
    val email = user?.email ?: "No email"
    val photo = user?.photoUrl

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)) {
        if (photo != null) {
            AsyncImage(
                model = photo,
                contentDescription = "Profile photo",
                modifier = Modifier.size(84.dp).clip(CircleShape)
            )
        } else {
            Box(modifier = Modifier.size(84.dp).clip(CircleShape).background(Color.Gray))
        }

        Spacer(Modifier.height(12.dp))
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        Text(text = email, style = MaterialTheme.typography.bodySmall)

        Spacer(Modifier.height(24.dp))

        // Logout button
        Button(onClick = {
            FirebaseAuth.getInstance().signOut()  // Sign out
            navController.navigate("login") {     // Navigate back to login
                popUpTo("Profile") { inclusive = true } // Remove Profile from backstack
            }
        }) {
            Text(text = "Logout")
        }

    }
}
