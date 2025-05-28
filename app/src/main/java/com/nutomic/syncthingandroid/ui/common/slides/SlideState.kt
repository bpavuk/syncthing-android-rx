package com.nutomic.syncthingandroid.ui.common.slides

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

    fun nextSlide() {
        if (currentSlide < maxSlides) currentSlide++
    }

    fun previousSlide() {
        if (currentSlide > 1) currentSlide--
    }
}

@Composable
fun rememberSlideState(maxSlides: Int, currentSlide: Int): SlideState {
    val slideState = remember { SlideState(maxSlides, currentSlide) }

    return slideState
}
