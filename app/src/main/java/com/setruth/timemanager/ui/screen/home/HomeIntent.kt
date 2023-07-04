package com.setruth.timemanager.ui.screen.home

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds



@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val weekList = listOf("一", "二", "三", "四", "五", "六", "七")

    private val _dateState = MutableStateFlow(DateState())
    val dateState: StateFlow<DateState>
        get() = _dateState

    private val _timeState = MutableStateFlow(TimeState())
    val timeState: StateFlow<TimeState>
        get() = _timeState

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState>
        get() = _uiState

    init {
        initTime()
        initImmersionState()
    }

    private fun initImmersionState() {
        launchIO {
            val show = dataStore.data.map {
                it[DataStoreCont.IMMERSION_STATE] ?: false
            }.first()
            if (show) {
                _uiState.value = _uiState.value.copy(immersionShow = true)
            } else {
                _uiState.value = _uiState.value.copy(immersionShow = false)
            }
        }
    }

    fun sendUIIntent(uiIntent: UIIntent) {
        when (uiIntent) {
            is UIIntent.ChangeImmersionState -> {
                _uiState.value = _uiState.value.copy(immersionShow = uiIntent.value)
                launchIO {
                    dataStore.edit {
                        it[DataStoreCont.IMMERSION_STATE] = uiIntent.value
                    }
                }
            }

            is UIIntent.ChangeLoadingState -> _uiState.value = _uiState.value.copy(loadingShow = uiIntent.value)
        }
    }

    private fun initTime() {
        val now = LocalDateTime.now()
        _timeState.value = _timeState.value.copy(
            hour = now.hour,
            minute = now.minute,
            second = now.second
        )
        _uiState.value = _uiState.value.copy(
            timeMode = if (now.hour > 12) TimeMode.PM else TimeMode.AM
        )

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
        tickerFlow(1.seconds)
            .map { LocalDateTime.now() }
            .distinctUntilChanged { old, new ->
                old.second == new.second
            }
            .onEach {
                _timeState.value = TimeState(it.hour, it.minute, it.second)
            }
            .launchIn(viewModelScope)
    }

    private fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private fun launchIO(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { block() }
    }
}
