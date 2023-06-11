package com.setruth.timemanager.ui.screen.mainnav.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.setruth.timemanager.ui.components.LoadingProgress
import com.setruth.timemanager.ui.screen.mainnav.MainViewModel
import com.setruth.timemanager.ui.theme.TimeManagerTheme
import kotlinx.coroutines.delay

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeView(mainViewModel: MainViewModel) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val timeState by homeViewModel.timeState.collectAsState()
    val uiState by homeViewModel.uiState.collectAsState()
    val dateState by homeViewModel.dateState.collectAsState()
    val activity = LocalContext.current as Activity
    LaunchedEffect(uiState.immersionShow) {
        homeViewModel.sendUIIntent(UIIntent.ChangeLoadingState(true))
        if (uiState.immersionShow) {
            mainViewModel.changeBottomState(false)
        } else {
            mainViewModel.changeBottomState(true)
        }
        delay(500)
        if (uiState.immersionShow) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        homeViewModel.sendUIIntent(UIIntent.ChangeLoadingState(false))
    }
    val timeHour = if (!uiState.timeMax) {
        if (timeState.hour > 12) timeState.hour - 12 else timeState.hour
    } else {
        timeState.hour
    }
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),

                ) {
                AnimatedVisibility(
                    visible = !uiState.immersionShow,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Row {
                        val pmActive = when (uiState.timeMode) {
                            TimeMode.PM -> true
                            TimeMode.AM -> false
                        }
                        OutlinedButton(
                            onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                                containerColor = if (pmActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                            ), shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                        ) {
                            Text(
                                text = "PM",
                                color = if (pmActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        OutlinedButton(
                            onClick = { /*TODO*/ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!pmActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
                        ) {
                            Text(
                                text = "AM",
                                color = if (!pmActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                    }
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Switch(
                    checked = uiState.immersionShow,
                    onCheckedChange = { homeViewModel.sendUIIntent(UIIntent.ChangeImmersionState(!uiState.immersionShow)) }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    AnimatedVisibility(
                        visible = !uiState.immersionShow,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Text(
                            text = "${dateState.date} ${dateState.week}",
                            modifier = Modifier.padding(bottom = 15.dp, start = 5.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        TimeTag(timeHour) {
                            homeViewModel.sendUIIntent(UIIntent.ChangeTimeMaxState(!uiState.timeMax))
                        }
                        Text(text = ":",fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        TimeTag(timeState.minute)
                        Text(text = ":",fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        TimeTag(timeState.second)
                    }
                }
            }
        }
        if (uiState.loadingShow) {
            LoadingProgress()
        }
    }
}


@Composable
fun TimeTag(content: Int, click: () -> Unit = {}) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .clickable {
                click()
            }
    ) {
        Text(
            text = content.toString(),
            fontSize = 30.sp,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
        )
    }
}

@Preview
@Composable
fun HomeViewPriView() {
    TimeManagerTheme {
//        HomeView(mainViewModel)
    }
}
