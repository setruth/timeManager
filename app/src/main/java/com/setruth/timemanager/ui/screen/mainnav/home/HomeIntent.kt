package com.setruth.timemanager.ui.screen.mainnav.home

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.setruth.timemanager.model.DataStoreCont
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject


sealed class UIIntent {
    data class ChangeImmersionState(val value: Boolean) : UIIntent()
    data class ChangeLoadingState(val value: Boolean) : UIIntent()
    data class ChangeTimeMaxState(val value: Boolean) : UIIntent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val WeekList = listOf("一", "二", "三", "四", "五", "六", "七")
    private val _dateState = MutableStateFlow(DateState())
    val dateState: StateFlow<DateState> = _dateState
    private val _timeState = MutableStateFlow(TimeState())
    val timeState: StateFlow<TimeState> = _timeState
    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState

    init {
        initTime()
        viewModelScope.launch {
            initImmersionState()
        }
    }

    private suspend fun initImmersionState() = withContext(Dispatchers.IO) {
        val show = dataStore.data.map {
            it[DataStoreCont.IMMERSION_STATE] ?: false
        }.first()
        if (show) {
            _uiState.value = _uiState.value.copy(immersionShow = true)
        } else {
            _uiState.value = _uiState.value.copy(immersionShow = false)
        }
    }

    fun sendUIIntent(uiIntent: UIIntent) {
        when (uiIntent) {
            is UIIntent.ChangeImmersionState -> {
                _uiState.value = _uiState.value.copy(immersionShow = uiIntent.value)
                viewModelScope.launch(Dispatchers.IO) {
                    dataStore.edit {
                        it[DataStoreCont.IMMERSION_STATE] = uiIntent.value
                    }
                }
            }
            is UIIntent.ChangeLoadingState -> _uiState.value = _uiState.value.copy(loadingShow = uiIntent.value)
            is UIIntent.ChangeTimeMaxState -> _uiState.value = _uiState.value.copy(timeMax = uiIntent.value)
        }
    }

    private fun initTime() {
        val now = LocalDateTime.now()
        _timeState.value = _timeState.value.copy(hour = now.hour, minute = now.minute, second = now.second)
        _uiState.value = _uiState.value.copy(timeMode = if (now.hour > 12) TimeMode.PM else TimeMode.AM)

        updateTime()
        updateDateAndWeek(now)
    }

    private fun updateDateAndWeek(now: LocalDateTime) {
            _dateState.value = _dateState.value.copy(
            date = "${now.year}/${now.monthValue}/${now.dayOfMonth}",
            week = "(${weekList[now.dayOfWeek.value - 1]})"
        )
    }

    private fun updateTime() {
        viewModelScope.launch(Dispatchers.IO) {
            _timeState.apply {
                while (true) {
                    val nowSecond = value.second + 1
                    if (nowSecond == 60) {
                        value = value.copy(second = 0)
                        val nowMinute = value.minute + 1
                        if (nowMinute == 60) {
                            value = value.copy(minute = 0)
                            val nowHour = value.hour + 1
                            val timeMode = if (nowHour > 12) {
                                TimeMode.PM
                            } else {
                                TimeMode.AM
                            }
                            Log.e("TAG", "updateTime:$timeMode ")
                            _uiState.value = _uiState.value.copy(timeMode = timeMode)
                            value = if (nowHour == 24) {
                                updateDateAndWeek(LocalDateTime.now())
                                value.copy(hour = 0)
                            } else {
                                value.copy(hour = nowHour)
                            }
                        } else {
                            value = value.copy(minute = nowMinute)
                        }
                    } else {
                        value = value.copy(second = nowSecond)
                    }
                    delay(1000)
                }
            }

        }
    }
}
