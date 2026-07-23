package dji.sampleV5.aircraft.chanchodetector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DetectionOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val greenPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val redPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private val textPaint = Paint().apply {
        color = Color.RED
        textSize = 40f
        style = Paint.Style.FILL
    }

    private var tracks: List<TrackedObject> = emptyList()

    fun setTracks(newTracks: List<TrackedObject>) {
        tracks = newTracks
        invalidate() // Redibujar
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (track in tracks) {
            val paint = if (track.state == DetectionState.CONFIRMED_COW) redPaint else greenPaint
            
            // Convertir coordenadas normalizadas (0..1) a píxeles de pantalla
            val left = track.rect.left * width
            val top = track.rect.top * height
            val right = track.rect.right * width
            val bottom = track.rect.bottom * height
            
            canvas.drawRect(left, top, right, bottom, paint)
            
            if (track.state == DetectionState.CONFIRMED_COW) {
                canvas.drawText("COW", left, top - 10f, textPaint)
            }
        }
    }
}
