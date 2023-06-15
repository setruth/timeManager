package com.setruth.timemanager.ui.screen.mainnav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.setruth.timemanager.R
import com.setruth.timemanager.config.MainNavRoute
import com.setruth.timemanager.ui.screen.mainnav.countdown.CountDownView
import com.setruth.timemanager.ui.screen.mainnav.home.HomeView
import com.setruth.timemanager.ui.screen.mainnav.stopwatch.StopWatchView
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel:ViewModel(){
    private val _navBottomState = MutableStateFlow(true)
    val navBottomState: StateFlow<Boolean> = _navBottomState

    fun changeBottomState(state:Boolean){
         _navBottomState.value = state
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainNavView() {
    val mainViewModel:MainViewModel= viewModel()
    val navBottomState by mainViewModel.navBottomState.collectAsState()
    val navList = listOf(
        Pair("主页", R.drawable.home),
        Pair("倒计时", R.drawable.count),
        Pair("秒表", R.drawable.second)
    )
    var nowActiveIndex by remember {
        mutableStateOf(0)
    }
    val mainNavController = rememberNavController()
    mainNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                HOME -> {
                    nowActiveIndex = 0
                }

                COUNT_DOWN -> {
                    nowActiveIndex = 1
                }

                STOP_WATCH -> {
                    nowActiveIndex = 2
                }
            }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = navBottomState, enter = scaleIn()+ fadeIn(), exit = scaleOut()+ fadeOut()
            ) {
                NavigationBar {
                    navList.forEachIndexed { index, pair ->
                        NavigationBarItem(
                            selected = nowActiveIndex == index,
                            onClick = {
                                nowActiveIndex = when (index) {
                                    0 -> {
                                        mainNavController.mainNavTo(MainNavRoute.HOME)
                                        index
                                    }

                                    1 -> {
                                        mainNavController.mainNavTo(MainNavRoute.COUNT_DOWN)
                                        index
                                    }

                                    2 -> {
                                        mainNavController.mainNavTo(MainNavRoute.STOP_WATCH)
                                        index
                                    }

                                    else -> {
                                        -1
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(id = pair.second),
                                    contentDescription = pair.first,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            label = {
                                Text(text = pair.first)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }
            }
        }) {
        Box(modifier = Modifier.padding(it)) {
            NavHost(navController = mainNavController, startDestination = MainNavRoute.HOME) {
                composable(MainNavRoute.HOME) {
                    HomeView(mainViewModel)
                }
                composable(MainNavRoute.COUNT_DOWN) {
                    CountDownView()
                }
                composable(MainNavRoute.STOP_WATCH) {
                    StopWatchView()
                }
            }
        }
    }
}

fun NavHostController.mainNavTo(route: String) {
    this.navigate(route) {
        popUpTo(this@mainNavTo.graph.findStartDestination().id)
        launchSingleTop = true
    }
}
