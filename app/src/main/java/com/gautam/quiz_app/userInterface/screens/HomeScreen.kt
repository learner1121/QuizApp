package com.gautam.quiz_app.userInterface.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gautam.quiz_app.ui.theme.inriaSerifFontFamily
import kotlinx.coroutines.launch
import com.gautam.quiz_app.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val cardWidth = screenWidth - 20

    val style = MaterialTheme.typography.titleMedium
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet (Modifier.width(250.dp)){
                Column (Modifier.padding(16.dp)){
                    Button(onClick = {navController.navigate("HomeScreen")}) {Text("Home", Modifier.fillMaxWidth(), style = style) }
                    Button(onClick = {navController.navigate("SectionScreen")}) {Text("Sections", Modifier.fillMaxWidth(), style = style) }
                    Button(onClick = {navController.navigate("RandomSectionScreen")}) {Text("RandomQuestion", Modifier.fillMaxWidth(), style = style) }
                    Button(onClick = {navController.navigate("Profile")}) {Text("User's Profile", Modifier.fillMaxWidth(), style = style) }
                    //Button(onClick = {navController.navigate("SignUp")}) {Text("feedBack", Modifier.fillMaxWidth(), style = style) }
                    //Button(onClick = {navController.navigate("Result")}) {Text("Result", Modifier.fillMaxWidth(), style = style) }
                }
            }
        },drawerState =drawerState
    ) {
        Scaffold (
            topBar = {
                TopAppBar(title = {Text("QuizApp")},
                    navigationIcon = {
                        IconButton(onClick = {scope.launch  {drawerState.open()} }) {
                            Icon(Icons.Default.Menu,"MenuIcon")
                        }
                    })
            },
        modifier = Modifier.background(Color.White)
        ){ innerPadding->
            Column(Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Spacer(Modifier.height(50.dp))
                LogoWithSlogan()

                Spacer(Modifier.height(50.dp))
                ContentCards("Normal Quiz",Color(0xFFBBDEFB),cardWidth.dp,
                    { navController.navigate("SectionScreen") })
                Spacer(Modifier.height(8.dp))
                ContentCards("Random Quiz",Color(0xFFC8E6C9), width =cardWidth.dp,
                    onClick = {navController.navigate("RandomSectionScreen")})

            }
        }
    }
}

@Composable
fun ContentCards(
    text: String,
    color: Color,
    width: Dp,
    onClick:()->Unit
){
    Card(modifier = Modifier
        .width(width)
        .height(100.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color,
            disabledContainerColor = Color.Blue

        ),
        border = BorderStroke(2.dp,Color.Cyan),
        onClick = {onClick()}
    ){
        Box(Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Text(text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        fontFamily = inriaSerifFontFamily,
                        color = Color.White
                    ))
            }
        }

    }

}

@Composable
fun LogoWithSlogan(){
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo&Text",
            contentScale = ContentScale.Fit,

        )
    }

