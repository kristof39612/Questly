package hu.bme.aut.szoftverarch.questly.graphics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum


@Suppress("DEPRECATION")
fun getBitmapFromVectorDrawable(context: Context, drawableId: Int, status: StatusEnum = StatusEnum.APPROVED): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)!!.mutate()

    when(status){
        StatusEnum.APPROVED -> drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        StatusEnum.PENDING -> drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        StatusEnum.REJECTED -> drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }
    //drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

    // Create a bitmap with ARGB_8888 config for transparency support
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    when(status){
        StatusEnum.APPROVED -> canvas.drawColor(Color.BLACK)
        StatusEnum.PENDING -> canvas.drawColor(Color.YELLOW)
        StatusEnum.REJECTED -> canvas.drawColor(Color.RED)
    }

    // Draw the white-colored drawable on top of the black background
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

fun taskPointIcon(taskPoint: TaskPoint): Int {
    return when (taskPoint.task::class.java.simpleName) {
        "TextPromptTask" -> R.drawable.ic_abc
        "GoToPointTask" -> R.drawable.ic_walking
        "SingleChoiceTask" -> R.drawable.ic_selection
        else -> R.drawable.ic_home
    }
}

fun taskTypeIcon(taskType: String): Int {
    return when (taskType) {
        "Text Prompt Task" -> R.drawable.ic_abc
        "Go To a Point Task" -> R.drawable.ic_walking
        "Single Choice Task" -> R.drawable.ic_selection
        else -> R.drawable.ic_home
    }
}