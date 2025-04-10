package com.m7019e.couchpotato.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4), // Purple
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFEADDFF), // Light purple
    onPrimaryContainer = Color(0xFF21005D), // Dark purple
    secondary = Color(0xFF625B71), // Grayish purple
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFFE8DEF8), // Light grayish purple
    onSecondaryContainer = Color(0xFF1D192B), // Dark grayish purple
    tertiary = Color(0xFF7D5260), // Rose
    onTertiary = Color(0xFFFFFFFF), // White
    tertiaryContainer = Color(0xFFFFD8E4), // Light rose
    onTertiaryContainer = Color(0xFF31111D), // Dark rose
    background = Color(0xFFFFFBFE), // Near-white
    onBackground = Color(0xFF1C1B1F), // Dark gray
    surface = Color(0xFFFFFBFE), // Same as background
    onSurface = Color(0xFF1C1B1F), // Dark gray
    surfaceVariant = Color(0xFFE7E0EC), // Light grayish purple
    onSurfaceVariant = Color(0xFF49454F), // Medium gray
    outline = Color(0xFF79747E), // Gray for outlines
    error = Color(0xFFB3261E), // Red
    onError = Color(0xFFFFFFFF), // White
    errorContainer = Color(0xFFF9DEDC), // Light red
    onErrorContainer = Color(0xFF410E0B) // Dark red
)

// Baseline M3 Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF), // Light purple
    onPrimary = Color(0xFF381E72), // Dark purple
    primaryContainer = Color(0xFF4F378B), // Medium purple
    onPrimaryContainer = Color(0xFFEADDFF), // Light purple
    secondary = Color(0xFFCCC2DC), // Light grayish purple
    onSecondary = Color(0xFF332D41), // Dark grayish purple
    secondaryContainer = Color(0xFF4A4458), // Medium grayish purple
    onSecondaryContainer = Color(0xFFE8DEF8), // Light grayish purple
    tertiary = Color(0xFFEFB8C8), // Light rose
    onTertiary = Color(0xFF492532), // Dark rose
    tertiaryContainer = Color(0xFF633B48), // Medium rose
    onTertiaryContainer = Color(0xFFFFD8E4), // Light rose
    background = Color(0xFF1C1B1F), // Dark gray
    onBackground = Color(0xFFE6E1E5), // Light gray
    surface = Color(0xFF1C1B1F), // Same as background
    onSurface = Color(0xFFE6E1E5), // Light gray
    surfaceVariant = Color(0xFF49454F), // Medium gray
    onSurfaceVariant = Color(0xFFCAC4D0), // Light grayish purple
    outline = Color(0xFF938F99), // Light gray for outlines
    error = Color(0xFFF2B8B5), // Light red
    onError = Color(0xFF601410), // Dark red
    errorContainer = Color(0xFF8C1D18), // Medium red
    onErrorContainer = Color(0xFFF9DEDC) // Light red
)

@Composable
fun CouchPotatoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}