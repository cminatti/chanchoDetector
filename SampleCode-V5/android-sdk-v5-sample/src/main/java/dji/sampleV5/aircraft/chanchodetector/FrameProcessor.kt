package dji.sampleV5.aircraft.chanchodetector

import android.graphics.RectF

/**
 * Interfaz común para procesadores de imágenes (Movimiento, YOLO, etc.)
 */
interface FrameProcessor {
    /**
     * Procesa un frame de video y devuelve una lista de regiones detectadas
     * @param frameData Datos del frame (usualmente YUV)
     * @param width Ancho del frame
     * @param height Alto del frame
     * @return Lista de rectángulos con las detecciones
     */
    fun process(frameData: ByteArray, width: Int, height: Int): List<RectF>
    
    /**
     * Libera recursos si es necesario
     */
    fun stop() {}
}
