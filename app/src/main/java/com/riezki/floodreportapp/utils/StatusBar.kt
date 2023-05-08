package com.riezki.floodreportapp.utils

import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

fun setFullScreen(window: Window) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
}

fun setLightStatusBar(window: Window, isLight: Boolean = true) {
    val status = WindowInsetsControllerCompat(window, window.decorView)
    status.isAppearanceLightStatusBars = isLight
}