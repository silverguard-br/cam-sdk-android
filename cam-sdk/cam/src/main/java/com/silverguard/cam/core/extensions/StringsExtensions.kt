package com.silverguard.cam.core.extensions

import androidx.core.graphics.toColorInt

fun String.toColorOrDefault(default: Int): Int {
    return try {
        this.toColorInt()
    } catch (e: IllegalArgumentException) {
        default
    }
}