package dji.sampleV5.aircraft.chanchodetector

import android.content.Context
import android.graphics.RectF
import android.os.*
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import dji.sampleV5.aircraft.databinding.ActivityChanchoDetectorBinding
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.interfaces.ICameraStreamManager
import dji.v5.manager.interfaces.ICameraStreamManager.FrameFormat
import dji.v5.manager.interfaces.ICameraStreamManager.ScaleType
import dji.sdk.keyvalue.value.common.ComponentIndexType

class ChanchoDetectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChanchoDetectorBinding
    private var frameProcessor: FrameProcessor = MotionDetector()
    private var vibrator: Vibrator? = null
    private var lastVibrationTime: Long = 0
    private val VIBRATION_COOLDOWN = 3000L // 3 segundos entre vibraciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChanchoDetectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        setupVideo()
        setupButtons()
    }

    private fun setupVideo() {
        binding.videoSurface.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // Decimos a DJI que renderice el video en nuestra superficie
                MediaDataCenter.getInstance().cameraStreamManager.putCameraStreamSurface(
                    ComponentIndexType.LEFT_OR_MAIN,
                    holder.surface,
                    binding.videoSurface.width,
                    binding.videoSurface.height,
                    ScaleType.CENTER_INSIDE
                )
                startFrameAnalysis()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                MediaDataCenter.getInstance().cameraStreamManager.removeCameraStreamSurface(holder.surface)
                stopFrameAnalysis()
            }
        })
    }

    private fun startFrameAnalysis() {
        // Pedimos frames en formato YUV (Luma) para analizar movimiento
        MediaDataCenter.getInstance().cameraStreamManager.addFrameListener(
            ComponentIndexType.LEFT_OR_MAIN,
            FrameFormat.YUV420_888,
            frameListener
        )
    }

    private fun stopFrameAnalysis() {
        MediaDataCenter.getInstance().cameraStreamManager.removeFrameListener(frameListener)
    }

    private val frameListener = ICameraStreamManager.CameraFrameListener { frameData, offset, length, width, height, format ->
        // Análisis de frame (Corre en un hilo de fondo de DJI)
        val regions = frameProcessor.process(frameData, width, height)
        
        // Convertimos regiones a tracks para el overlay
        val tracks = regions.mapIndexed { index, rectF ->
            TrackedObject(index, rectF, DetectionState.UNKNOWN_MOVING)
        }

        // UI Updates
        runOnUiThread {
            binding.detectionOverlay.setTracks(tracks)
            binding.fpsText.text = "Movimiento detectado: ${regions.size} regiones"
            
            if (regions.isNotEmpty()) {
                triggerVibration()
            }
        }
    }

    private fun triggerVibration() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastVibrationTime > VIBRATION_COOLDOWN) {
            lastVibrationTime = currentTime
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(200)
            }
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnExport.setOnClickListener {
            // TODO: Implementar exportación de dataset
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        frameProcessor.stop()
        stopFrameAnalysis()
    }
}
