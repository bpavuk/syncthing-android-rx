package com.nutomic.syncthingandroid.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.nutomic.syncthingandroid.ui.common.slides.SlidesController
import com.nutomic.syncthingandroid.ui.screens.firstStart.IgnoreDozePermissionSlide
import com.nutomic.syncthingandroid.ui.screens.firstStart.KeyGenerationSlide
import com.nutomic.syncthingandroid.ui.screens.firstStart.LocationPermissionSlide
import com.nutomic.syncthingandroid.ui.screens.firstStart.NotificationPermissionSlide
import com.nutomic.syncthingandroid.ui.screens.firstStart.StoragePermissionSlide
import com.nutomic.syncthingandroid.ui.screens.firstStart.WelcomeSlide
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme


sealed interface Slide {
    data object Welcome : Slide
    data object StoragePermission : Slide
    data object IgnoreDozePermission : Slide
    data object LocationPermission : Slide
    data object NotificationPermission : Slide
    data object KeyGeneration : Slide
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FirstStartScreen(
    slides: List<Slide>,
    modifier: Modifier = Modifier
) {
    // TODO: migrate to Nav3 when it's out of Alpha
    val slideCount = slides.count()
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            SlidesController(
                modifier = Modifier.fillMaxWidth(),
                backHandler = if (currentSlideIndex <= 0) {
                    null
                } else {
                    {
                        currentSlideIndex -= 1
                    }
                },
                forwardHandler = {
                    when (slides[currentSlideIndex]) {
                        Slide.IgnoreDozePermission -> TODO()
                        Slide.KeyGeneration -> TODO()
                        Slide.LocationPermission -> TODO()
                        Slide.NotificationPermission -> TODO()
                        Slide.StoragePermission -> {

                        }
                        Slide.Welcome -> {
                            currentSlideIndex++
                        }
                    }
                },
                activeSlideNumber = currentSlideIndex + 1,
                slideCount = slideCount
            )
        }
    ) { innerPadding ->
        when (slides[currentSlideIndex]) {
            Slide.Welcome -> WelcomeSlide(Modifier.padding(innerPadding))
            Slide.StoragePermission -> StoragePermissionSlide(Modifier.padding(innerPadding))
            Slide.IgnoreDozePermission -> IgnoreDozePermissionSlide(Modifier.padding(innerPadding))
            Slide.LocationPermission -> LocationPermissionSlide(Modifier.padding(innerPadding))
            Slide.NotificationPermission -> NotificationPermissionSlide(
                Modifier.padding(
                    innerPadding
                )
            )

            Slide.KeyGeneration -> KeyGenerationSlide(Modifier.padding(innerPadding))
        }
    }
}

@Preview
@Composable
private fun FirstStartPreview() {
    SyncthingandroidTheme {
        FirstStartScreen(
            slides = listOf(Slide.Welcome, Slide.StoragePermission, Slide.LocationPermission),
            modifier = Modifier.fillMaxSize()
        )
    }
}
