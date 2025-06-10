package com.nutomic.syncthingandroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val YellowPrimaryLight = Color(0xFF6d5e0f)
val YellowOnPrimaryLight = Color(0xFFffffff)
val YellowPrimaryContainerLight = Color(0xFFf8e287)
val YellowOnPrimaryContainerLight = Color(0xFF534600)

val YellowPrimaryDark = Color(0xFFdbc66e)
val YellowOnPrimaryDark = Color(0xFF3a3000)
val YellowPrimaryContainerDark = Color(0xFF534600)
val YellowOnPrimaryContainerDark = Color(0xFFf8e287)

@Composable
fun yellowPrimary(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) YellowPrimaryDark else YellowPrimaryLight
}

@Composable
fun yellowOnPrimary(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) YellowOnPrimaryDark else YellowOnPrimaryLight
}

@Composable
fun yellowPrimaryContainer(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) YellowPrimaryContainerDark else YellowPrimaryContainerLight
}

@Composable
fun yellowOnPrimaryContainer(darkTheme: Boolean = isSystemInDarkTheme()): Color {
    return if (darkTheme) YellowOnPrimaryContainerDark else YellowOnPrimaryContainerLight
}
