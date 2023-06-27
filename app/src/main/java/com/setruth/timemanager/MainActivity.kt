package com.setruth.timemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.setruth.timemanager.config.APPRoute
import com.setruth.timemanager.ui.screen.mainnav.MainNavView
import com.setruth.timemanager.ui.screen.start.StartPageView
import com.setruth.timemanager.ui.theme.TimeManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeManagerTheme {
                val appNavController = rememberNavController()
                NavHost(navController = appNavController, startDestination = APPRoute.START_SCREEN) {
                    composable(APPRoute.START_SCREEN) {
                        StartPageView(appNavController)
                    }
                    composable(APPRoute.MAIN_NAV) {
                        MainNavView()
                    }
                }
            }
        }
    }
}