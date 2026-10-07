package dev.pointfix.sample

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp

enum class Kind { Dress, Jacket, Hoodie, Jeans, Shoe }

data class Product(
    val slug: String, val name: String, val category: String, val price: Int, val oldPrice: Int?,
    val kind: Kind, val tile: Color, val color: Color,
)

val categories = listOf("All", "Dresses", "Jackets", "Hoodies", "Jeans", "Shoes")

val products = listOf(
    Product("linen-wrap-dress", "Linen Wrap Dress", "Dresses", 68, 89, Kind.Dress, Color(0xFFF7E1DC), Color(0xFFE98F7E)),
    Product("corduroy-bomber", "Oversized Corduroy Bomber", "Jackets", 96, null, Kind.Jacket, Color(0xFFF3E8D2), Color(0xFFC08A4B)),
    Product("cloud-fleece-hoodie", "Cloud Fleece Hoodie", "Hoodies", 48, null, Kind.Hoodie, Color(0xFFE2E9F6), Color(0xFF7E9BD8)),
    Product("wide-leg-jeans", "Wide Leg Jeans", "Jeans", 58, 72, Kind.Jeans, Color(0xFFDDE6EE), Color(0xFF4F6F95)),
    Product("court-sneaker", "Court Sneaker", "Shoes", 88, 120, Kind.Shoe, Color(0xFFE6F3DA), Color(0xFF5E9E3C)),
    Product("satin-slip-dress", "Satin Slip Dress", "Dresses", 54, null, Kind.Dress, Color(0xFFEDE3F5), Color(0xFF9C7CC4)),
    Product("cropped-denim-jacket", "Cropped Denim Jacket", "Jackets", 79, 110, Kind.Jacket, Color(0xFFDFE8F1), Color(0xFF5B7FA8)),
    Product("zip-tech-hoodie", "Zip Tech Hoodie", "Hoodies", 62, 75, Kind.Hoodie, Color(0xFFE4E5E3), Color(0xFF3B3F3D)),
    Product("straight-fit-jeans", "Straight Fit Jeans", "Jeans", 64, null, Kind.Jeans, Color(0xFFE0E4EA), Color(0xFF2F3B4E)),
    Product("chunky-runner", "Chunky Runner", "Shoes", 95, null, Kind.Shoe, Color(0xFFF6E7D8), Color(0xFFE07A3A)),
)

/** Flat garment illustration in a unit square; points are fractions of the drawing size. */
@Composable
fun Garment(kind: Kind, color: Color, modifier: Modifier = Modifier) = Canvas(modifier) {
    val s = minOf(size.width, size.height)
    val origin = Offset((size.width - s) / 2, (size.height - s) / 2)
    fun p(x: Float, y: Float) = Offset(origin.x + x * s, origin.y + y * s)
    fun path(vararg pts: Float) = Path().apply {
        moveTo(p(pts[0], pts[1]).x, p(pts[0], pts[1]).y)
        for (i in 2 until pts.size step 2) p(pts[i], pts[i + 1]).let { lineTo(it.x, it.y) }
        close()
    }
    val shade = lerp(color, Color.Black, 0.22f)
    val line = Stroke(width = s * 0.022f, cap = StrokeCap.Round)
    drawOval(Color.Black.copy(alpha = 0.07f), p(0.16f, 0.92f), Size(s * 0.68f, s * 0.07f))
    when (kind) {
        Kind.Dress -> {
            drawPath(path(.38f, .06f, .44f, .06f, .5f, .14f, .56f, .06f, .62f, .06f, .65f, .34f, .60f, .42f, .84f, .92f, .16f, .92f, .40f, .42f, .35f, .34f), color)
            drawLine(shade, p(.38f, .40f), p(.62f, .40f), line.width * 1.6f)
        }
        Kind.Jacket, Kind.Hoodie -> {
            if (kind == Kind.Hoodie) drawOval(shade, p(.36f, .02f), Size(s * .28f, s * .18f))
            drawPath(path(.34f, .10f, .42f, .06f, .5f, .12f, .58f, .06f, .66f, .10f, .90f, .26f, .96f, .78f, .82f, .80f, .78f, .44f, .78f, .92f, .22f, .92f, .22f, .44f, .18f, .80f, .04f, .78f, .10f, .26f), color)
            if (kind == Kind.Jacket) {
                drawLine(shade, p(.5f, .14f), p(.5f, .92f), line.width, StrokeCap.Round)
                drawPath(path(.42f, .06f, .5f, .14f, .44f, .30f), shade)
                drawPath(path(.58f, .06f, .5f, .14f, .56f, .30f), shade)
            } else {
                drawRoundRect(shade, p(.33f, .60f), Size(s * .34f, s * .16f), androidx.compose.ui.geometry.CornerRadius(s * .04f))
                drawLine(shade, p(.46f, .14f), p(.45f, .34f), line.width, StrokeCap.Round)
                drawLine(shade, p(.54f, .14f), p(.55f, .34f), line.width, StrokeCap.Round)
            }
        }
        Kind.Jeans -> {
            drawPath(path(.27f, .06f, .73f, .06f, .80f, .92f, .56f, .92f, .5f, .36f, .44f, .92f, .20f, .92f), color)
            drawLine(shade, p(.27f, .14f), p(.73f, .14f), line.width)
            drawLine(shade, p(.5f, .14f), p(.5f, .30f), line.width)
        }
        Kind.Shoe -> {
            drawPath(path(.08f, .70f, .10f, .40f, .22f, .34f, .34f, .40f, .54f, .52f, .80f, .56f, .92f, .66f, .92f, .74f, .08f, .74f), color)
            drawRoundRect(Color.White, p(.06f, .72f), Size(s * .88f, s * .09f), androidx.compose.ui.geometry.CornerRadius(s * .04f))
            for (i in 0..2) drawLine(Color.White.copy(alpha = .85f), p(.36f + i * .07f, .44f + i * .035f), p(.42f + i * .07f, .40f + i * .035f), line.width, StrokeCap.Round)
        }
    }
}
