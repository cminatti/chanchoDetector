package dji.sampleV5.aircraft.chanchodetector

import android.graphics.RectF
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

class MotionDetector(
    private val width: Int = 160,
    private val height: Int = 90,
    private val threshold: Int = 25,
    private val minAreaPercent: Float = 0.005f
) {
    private var prevFrame: ByteArray? = null
    private val motionRegions = AtomicReference<List<RectF>>(emptyList())

    /**
     * Procesa un frame YUV (solo usamos el canal Y/Luma para movimiento)
     */
    fun processFrame(yData: ByteArray, frameWidth: Int, frameHeight: Int): List<RectF> {
        // 1. Downsample agresivo
        val downsampled = downsample(yData, frameWidth, frameHeight, width, height)

        if (prevFrame == null) {
            prevFrame = downsampled
            return emptyList()
        }

        // 2. Diferencia de frames
        val diff = ByteArray(width * height)
        var motionPixels = 0
        for (i in 0 until (width * height)) {
            val d = abs((downsampled[i].toInt() and 0xFF) - (prevFrame!![i].toInt() and 0xFF))
            if (d > threshold) {
                diff[i] = 255.toByte()
                motionPixels++
            } else {
                diff[i] = 0
            }
        }

        prevFrame = downsampled

        // 3. Agrupamiento simple (segmentación por regiones conectadas básica)
        // En un Motorola G Play, esto debe ser MUY rápido.
        // Usaremos una aproximación de grid/tiles para encontrar regiones calientes.
        val regions = findRegions(diff)
        motionRegions.set(regions)
        return regions
    }

    private fun downsample(src: ByteArray, srcW: Int, srcH: Int, dstW: Int, dstH: Int): ByteArray {
        val out = ByteArray(dstW * dstH)
        val xRatio = srcW.toFloat() / dstW
        val yRatio = srcH.toFloat() / dstH

        for (y in 0 until dstH) {
            for (x in 0 until dstW) {
                val srcX = (x * xRatio).toInt()
                val srcY = (y * yRatio).toInt()
                out[y * dstW + x] = src[srcY * srcW + srcX]
            }
        }
        return out
    }

    private fun findRegions(diff: ByteArray): List<RectF> {
        // Dividimos en un grid de 10x10 para detectar movimiento agrupado
        val gridCols = 10
        val gridRows = 10
        val cellW = width / gridCols
        val cellH = height / gridRows
        val regions = mutableListOf<RectF>()

        for (gy in 0 until gridRows) {
            for (gx in 0 until gridCols) {
                var activeCount = 0
                for (y in (gy * cellH) until ((gy + 1) * cellH)) {
                    for (x in (gx * cellW) until ((gx + 1) * cellW)) {
                        if (diff[y * width + x].toInt() != 0) activeCount++
                    }
                }

                // Si más del 10% de la celda se mueve, es candidata
                if (activeCount > (cellW * cellH * 0.1)) {
                    regions.add(RectF(
                        gx.toFloat() / gridCols,
                        gy.toFloat() / gridRows,
                        (gx + 1).toFloat() / gridCols,
                        (gy + 1).toFloat() / gridRows
                    ))
                }
            }
        }

        // Combinar rectángulos adyacentes (simple merge)
        return mergeRects(regions)
    }

    private fun mergeRects(rects: List<RectF>): List<RectF> {
        if (rects.isEmpty()) return emptyList()
        val merged = mutableListOf<RectF>()
        // Aquí se podría implementar un algoritmo de IOU o agrupación por proximidad.
        // Por ahora devolvemos las regiones detectadas.
        return rects
    }
}
