package com.example.movie

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

private val NetflixRed = Color(0xFFE50914)
private val BackgroundBlack = Color(0xFF141414)
private val SurfaceBlack = Color(0xFF000000)
private val TextWhite = Color(0xFFE5E5E5)
private val TextLightGray = Color(0xFF999999)

private val DarkColorScheme = darkColorScheme(
    primary = NetflixRed,
    onPrimary = Color.White,
    background = BackgroundBlack,
    onBackground = TextWhite,
    surface = SurfaceBlack,
    onSurface = TextWhite,
    secondary = TextLightGray,
    onSecondary = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = NetflixRed,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    secondary = Color.Gray,
    onSecondary = Color.Black
)

private val NetflixTypography = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        // no color here
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
    )
    // You can add more styles if needed
)

@Composable
fun NetflixTheme(
    useDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = NetflixTypography,
        content = content
    )
}
