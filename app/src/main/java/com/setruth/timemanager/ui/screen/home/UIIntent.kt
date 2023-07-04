package com.setruth.timemanager.ui.screen.home

sealed class UIIntent {
    data class ChangeImmersionState(val value: Boolean) : UIIntent()
    data class ChangeLoadingState(val value: Boolean) : UIIntent()
}