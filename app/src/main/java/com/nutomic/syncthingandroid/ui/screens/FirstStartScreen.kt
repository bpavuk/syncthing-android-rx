package com.nutomic.syncthingandroid.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.nutomic.syncthingandroid.ui.common.slides.SlidesController
import com.nutomic.syncthingandroid.ui.common.slides.rememberSlideState
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
    modifier: Modifier = Modifier,
    onIntroFinished: () -> Unit
) {
    // TODO: migrate to Nav3 when it's out of Alpha
    val slideCount = slides.count()
    val slideState = rememberSlideState(slideCount, 1)
    Scaffold(
        modifier = modifier,
        bottomBar = {
            SlidesController(
                modifier = Modifier.fillMaxWidth().safeContentPadding(),
                slideState = slideState,
                onForward = { slideState.nextSlide() },
                onBack = { slideState.previousSlide() },
                onFinish = onIntroFinished,
            )
        }
    ) { innerPadding ->
        when (slides[slideState.currentSlide - 1]) {
            Slide.Welcome -> WelcomeSlide(
                Modifier.padding(innerPadding).safeContentPadding()
            )
            Slide.StoragePermission -> StoragePermissionSlide(
                Modifier.padding(innerPadding).safeContentPadding(),
                slideState = slideState
            )
            Slide.IgnoreDozePermission -> IgnoreDozePermissionSlide(
                Modifier.padding(innerPadding).safeContentPadding(),
                slideState = slideState
            )
            Slide.LocationPermission -> LocationPermissionSlide(
                Modifier.padding(innerPadding).safeContentPadding()
            )
            Slide.NotificationPermission -> NotificationPermissionSlide(
                Modifier.padding(innerPadding).safeContentPadding(),
                slideState = slideState
            )
            Slide.KeyGeneration -> KeyGenerationSlide(
                Modifier.padding(innerPadding).safeContentPadding(),
                slideState = slideState
            )
        }
    }
}

@Preview
@Composable
private fun FirstStartPreview() {
    SyncthingandroidTheme {
        FirstStartScreen(
            slides = listOf(Slide.Welcome, Slide.StoragePermission, Slide.LocationPermission),
            modifier = Modifier.fillMaxSize(),
            onIntroFinished = {}
        )
    }
}
