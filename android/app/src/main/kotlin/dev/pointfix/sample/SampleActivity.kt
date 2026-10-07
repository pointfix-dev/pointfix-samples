package dev.pointfix.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import dev.pointfix.CaptureHandle
import dev.pointfix.Pointfix

val Lime = Color(0xFF8BE05A)
val Ink = Color(0xFF121413)

private val LightColors = lightColorScheme(
    primary = Lime, onPrimary = Ink,
    background = Color(0xFFF1F2EF), onBackground = Ink,
    surface = Color.White, onSurface = Ink,
    surfaceVariant = Color(0xFFE8EAE6), onSurfaceVariant = Color(0xFF878C88),
)
private val DarkColors = darkColorScheme(
    primary = Lime, onPrimary = Ink,
    background = Color(0xFF0D0E0E), onBackground = Color(0xFFF1F2EF),
    surface = Color(0xFF1A1C1B), onSurface = Color(0xFFF1F2EF),
    surfaceVariant = Color(0xFF262928), onSurfaceVariant = Color(0xFF9A9F9B),
)

class SampleActivity : ComponentActivity() {
    private var capture: CaptureHandle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors) { ShopScreen() }
        }
        capture = Pointfix.install(this, screen = "Shop")
    }
    override fun onDestroy() { capture?.close(); capture = null; super.onDestroy() }
}
