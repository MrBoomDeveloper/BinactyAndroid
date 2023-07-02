package com.mrboomdev.platformer.util

fun formatTimer(time: Float, format: String): String {
    val minutes = (time / 60).toInt()
    val remainingSeconds = (time % 60).toInt()
    return format
        .replace("ss", if(remainingSeconds > 9) "$remainingSeconds" else "0${remainingSeconds}")
        .replace("mm", if(minutes > 9) "$minutes" else "0${minutes}")
}