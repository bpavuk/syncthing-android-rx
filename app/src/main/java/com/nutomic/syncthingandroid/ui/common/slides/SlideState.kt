package com.nutomic.syncthingandroid.ui.common.slides

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class SlideState internal constructor(
    maxSlides: Int,
    currentSlide: Int
) {
    var maxSlides by mutableIntStateOf(maxSlides)
        private set

    var currentSlide by mutableIntStateOf(currentSlide)
        private set

    val canGoBack
        get() = currentSlide > 1
    var backBlocked by mutableStateOf(false)

    val canGoForward
        get() = currentSlide <= maxSlides
    var forwardBlocked by mutableStateOf(false)

    val displayFinish
        get() = currentSlide == maxSlides

    fun nextSlide() {
        if (currentSlide < maxSlides && !forwardBlocked) currentSlide++
    }

    fun previousSlide() {
        if (canGoBack && !backBlocked) currentSlide--
    }
}

@Composable
fun rememberSlideState(maxSlides: Int, currentSlide: Int): SlideState {
    if (maxSlides < 1) throw IllegalStateException("maxSlides cannot be less than 1")
    if (currentSlide < 1) throw IllegalStateException("currentSlide cannot be less than 1")
    if (currentSlide > maxSlides) throw IllegalStateException("currentSlide cannot be bigger " +
            "than maxSlides")

    val slideState = remember { SlideState(maxSlides, currentSlide) }

    return slideState
}
