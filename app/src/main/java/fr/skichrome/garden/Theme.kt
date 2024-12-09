package fr.skichrome.garden

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource

@Composable
fun MyGardenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
)
{
    val lightColors = lightColorScheme(
        primary = colorResource(id = R.color.blue_500),
        primaryContainer = colorResource(id = R.color.blue_500_dark),
        secondary = colorResource(id = R.color.blue_500_dark),
        background = colorResource(id = R.color.blue_500),
        onPrimary = colorResource(id = R.color.white),
        onSecondary = colorResource(id = R.color.white),
    )
    val darkColors = darkColorScheme(
        primary = colorResource(id = R.color.orange_500),
        primaryContainer = colorResource(id = R.color.orange_500_light),
        secondary = colorResource(id = R.color.orange_500_dark),
        onPrimary = colorResource(id = R.color.black),
        onSecondary = colorResource(id = R.color.black),
        onBackground = colorResource(id = R.color.black),
        surface = colorResource(id = R.color.orange_500),
        onSurface = colorResource(id = R.color.white),
    )
    val colors = if (darkTheme) darkColors else lightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}