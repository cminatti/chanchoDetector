package dji.sampleV5.aircraft.chanchodetector

import android.graphics.RectF

enum class DetectionState {
    UNKNOWN_MOVING,
    CONFIRMED_COW,
    CONFIRMED_HOG,
    CONFIRMED_DEER
}

data class TrackedObject(
    val id: Int,
    var rect: RectF, // Coordenadas normalizadas (0.0 a 1.0)
    var state: DetectionState = DetectionState.UNKNOWN_MOVING,
    var confidence: Float = 0f,
    var lastSeen: Long = System.currentTimeMillis(),
    var lastVibrated: Long = 0,
    var motionEnergy: Float = 0f
)
