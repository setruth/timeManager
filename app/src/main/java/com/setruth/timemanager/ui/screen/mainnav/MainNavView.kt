package com.setruth.timemanager.ui.screen.mainnav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.setruth.timemanager.R
import com.setruth.timemanager.ui.screen.mainnav.countdown.CountDownView
import com.setruth.timemanager.ui.screen.mainnav.home.HomeView
import com.setruth.timemanager.ui.screen.mainnav.stopwatch.StopWatchView
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _navBottomState = MutableStateFlow(true)
    val navBottomState: StateFlow<Boolean> = _navBottomState

    fun changeBottomState(state: Boolean) {
        _navBottomState.value = state
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainNavView(modifier: Modifier = Modifier, mainViewModel: MainViewModel = viewModel()) {
    val navBottomState by mainViewModel.navBottomState.collectAsState()

    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = navBottomState,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = { navController.navigate(screen.route) },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(25.dp),
                                    painter = painterResource(id = screen.icon),
                                    contentDescription = stringResource(id = screen.resourceId),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            label = {
                                Text(text = stringResource(id = screen.resourceId))
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) { HomeView(mainViewModel) }
                composable(Screen.CountDown.route) { CountDownView() }
                composable(Screen.StopWatch.route) { StopWatchView() }
            }
        }
    }
}

val items = persistentListOf(Screen.Home, Screen.CountDown, Screen.StopWatch)

sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val icon: Int) {
    object Home : Screen("home", R.string.home, R.drawable.home)
    object CountDown : Screen("countDown", R.string.count_down, R.drawable.count)
    object StopWatch : Screen("stopWatch", R.string.stop_watch, R.drawable.second)
}
