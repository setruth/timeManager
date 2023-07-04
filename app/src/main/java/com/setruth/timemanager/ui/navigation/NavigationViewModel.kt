package com.setruth.timemanager.ui.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel : ViewModel() {
    private val _navBottomState = MutableStateFlow(true)
    val navBottomState: StateFlow<Boolean> = _navBottomState

    fun changeBottomState(state: Boolean) {
        _navBottomState.value = state
    }
}