package com.setruth.timemanager.ui.screen.home

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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.setruth.timemanager.ui.navigation.NavigationViewModel
import kotlinx.coroutines.delay

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeView(
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val dateState by homeViewModel.dateState.collectAsState()
    val activity = LocalContext.current as Activity

    LaunchedEffect(uiState.immersionShow) {
        homeViewModel.sendUIIntent(UIIntent.ChangeLoadingState(true))
        if (uiState.immersionShow) {
            navigationViewModel.changeBottomState(false)
        } else {
            navigationViewModel.changeBottomState(true)
        }

        delay(500)
        // todo 屏幕旋转有问题
        setScreenOrientation(uiState, activity)

        homeViewModel.sendUIIntent(UIIntent.ChangeLoadingState(false))
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Switch(
                checked = uiState.immersionShow,
                onCheckedChange = {
                    homeViewModel.sendUIIntent(
                        UIIntent.ChangeImmersionState(!uiState.immersionShow)
                    )
                }
            )
        }
        // todo 提取到 viewmodel 重建后状态丢失
        var show by remember {
            mutableStateOf(false)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }
                ) { show = !show },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !uiState.immersionShow,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                val timeState by homeViewModel.timeState.collectAsState()
                if (show) {
                    Column(modifier = Modifier) {
                        Text(
                            text = "${dateState.date} ${dateState.week}",
                            modifier = Modifier.padding(bottom = 15.dp, start = 5.dp)
                        )
                        ShowTime(timeState)
                    }
                } else {
                    Clock(timeState)
                }
            }
        }
    }
}

@Composable
private fun ShowTime(
    timeState: TimeState,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        TimeTag(content = timeState.hour)
        Text(text = ":", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        TimeTag(content = timeState.minute)
        Text(text = ":", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        TimeTag(content = timeState.second)
    }
}

private fun setScreenOrientation(uiState: UIState, activity: Activity) {
    if (uiState.immersionShow) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    } else {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}

@Composable
fun TimeTag(modifier: Modifier = Modifier, content: Int = 0) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier
            .padding(horizontal = 5.dp)
    ) {
        Text(
            text = content.toString(),
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .size(60.dp, 60.dp)
                .padding(vertical = 10.dp, horizontal = 12.dp)
        )
    }
}
