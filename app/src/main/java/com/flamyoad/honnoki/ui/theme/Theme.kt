package com.flamyoad.honnoki.ui.theme
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

// refer to night/colors.xml
private val DarkColorScheme = darkColorScheme(
    primary = Color(0x161E22),
    secondary = Color(0x040D19),
    tertiary = Color(0x4ebdfa)
)

// refer to values/colors.xml
private val LightColorScheme = lightColorScheme(
    primary = Color(0x364952),
    secondary = Color(0x09203f),
    tertiary = Color(0x4ebdfa)
)

@Composable
fun HonnokiAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    /**
     * i dont like the status bar color changes here. let's comment it out.
     */
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.primary.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
//        }
//    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}