package hu.bme.aut.szoftverarch.questly.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat


@Suppress("DEPRECATION")
fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)!!.mutate()

    drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

    // Create a bitmap with ARGB_8888 config for transparency support
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.BLACK)

    // Draw the white-colored drawable on top of the black background
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}
