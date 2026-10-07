package dev.pointfix.sample

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Looper
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Renders the shop screen to build/reports/sample for visual review. Not an assertion test. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-xxhdpi")
class ShopScreenshotTest {
    private fun render(name: String) {
        val activity = Robolectric.buildActivity(SampleActivity::class.java).setup().visible().get()
        repeat(3) { shadowOf(Looper.getMainLooper()).idle() }
        val root = activity.window.decorView
        val bitmap = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
        root.draw(Canvas(bitmap))
        val dir = File("build/reports/sample").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test fun light() = render("shop-light")

    @Test fun dark() { RuntimeEnvironment.setQualifiers("+night"); render("shop-dark") }
}
