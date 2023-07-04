package com.setruth.timemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.setruth.timemanager.ui.navigation.NavigationPage
import com.setruth.timemanager.ui.screen.start.StartPageView
import com.setruth.timemanager.ui.theme.TimeManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    sealed class Route(val route: String) {
        object Start : Route("start")
        object Main : Route("main")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeManagerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Route.Start.route) {
                    composable(Route.Start.route) {
                        StartPageView(navController)
                    }
                    composable(Route.Main.route) {
                        NavigationPage()
                    }
                }
            }
        }
    }
}
