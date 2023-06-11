package com.setruth.timemanager.model

import androidx.datastore.preferences.core.booleanPreferencesKey

object DataStoreCont{
    const val DATA_STORE_NAME = "app"
    val IMMERSION_STATE = booleanPreferencesKey("immersionState")
}
