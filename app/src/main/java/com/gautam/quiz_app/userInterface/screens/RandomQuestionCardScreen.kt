package com.gautam.quiz_app.userInterface.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gautam.quiz_app.ui.theme.interFontFamily
import com.gautam.quiz_app.ui.theme.poppinsFontFamily

@Composable
fun RandomSectionScreen( navHostController: NavHostController){

    val sections = listOf(
        "OOPs" to Color(0xFFBBDEFB),
        "OS"   to Color(0xFFC8E6C9),
        "DBMS" to Color(0xFFFFF9C4),
        "CN"   to Color(0xFFF8BBD0)
    )
    val height = 100.dp
    Column (Modifier.fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            Modifier.fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {navHostController.navigate("HomeScreen")}) {
                Icon(Icons.Filled.Home,"Home",
                    Modifier.size(24.dp))
            }
            Spacer(Modifier.weight(1f))

            Text(
                "Random Questions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Spacer(Modifier.weight(1f))
        }

        sections.forEach { (section, color) ->
            Card(
                onClick = {
                    Modifier.fillMaxWidth()
                    navHostController.navigate("RandomSection/$section")
                }, Modifier.fillMaxWidth()
                    .height(height),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = CardDefaults.elevatedShape
            ) {



                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(color),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Random$section Question",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color(0xFF212121),
                                fontFamily = poppinsFontFamily,
                                fontSize = 14.sp,
                            ))
                    }


            }
            Spacer(Modifier.height(6.5.dp))
        }

    }
}