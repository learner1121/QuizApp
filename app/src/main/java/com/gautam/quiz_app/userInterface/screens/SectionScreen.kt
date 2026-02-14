package com.gautam.quiz_app.userInterface.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gautam.quiz_app.R
import com.gautam.quiz_app.ui.theme.interFontFamily
import com.gautam.quiz_app.ui.theme.poppinsFontFamily
import com.gautam.quiz_app.userInterface.viewModel.QuestionViewModel

@Composable
fun SectionScreen(viewModel: QuestionViewModel, navHostController: NavHostController){

    val sections = listOf(
        "OOPs" to R.drawable.oops,
        "OS"   to R.drawable.os,
        "DBMS" to R.drawable.dbms,
        "CN"   to R.drawable.cn
    )
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            ) {
            IconButton(onClick = {navHostController.navigate("HomeScreen")}) {
                Icon(Icons.Filled.Home,"Home",
                    Modifier.size(24.dp))
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Select Section",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                modifier = Modifier.padding(8.dp)
            )
            Spacer(Modifier.weight(1f))
        }

        sections.forEach { (sectionName, imageRes) ->
            Card(
                onClick = { navHostController.navigate("QuestionFetch/$sectionName") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = CardDefaults.elevatedShape
            ) {
                Box {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = sectionName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$sectionName Quiz",
                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White,
                                fontFamily = poppinsFontFamily,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.5.dp))

        }
    }
}
