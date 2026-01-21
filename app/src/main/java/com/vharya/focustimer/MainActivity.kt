package com.vharya.focustimer

import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var vLabelTimerOff: TextView
    private lateinit var vLabelTimerOn: TextView

    private lateinit var vInputHour: EditText
    private lateinit var vInputMinute: EditText
    private lateinit var vInputSecond: EditText

    private lateinit var vStartButton: Button
    private lateinit var vStopButton: Button

    private lateinit var vPreset1Button: Button
    private lateinit var vPreset2Button: Button
    private lateinit var vPreset3Button: Button

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null

    private var vibrator: Vibrator? = null

    private var countDownTimerJob: Job? = null
    private var millisRemaining: Long = 0

    private var isFaceDown = false
    private var isNear = false
    private var isTimerRunning = false
    private var isAlarming = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        vLabelTimerOff = findViewById(R.id.label_set_time)
        vLabelTimerOn = findViewById(R.id.label_time_left)

        vInputHour = findViewById(R.id.input_hour)
        vInputMinute = findViewById(R.id.input_minute)
        vInputSecond = findViewById(R.id.input_second)

        vStartButton = findViewById(R.id.button_start)
        vStopButton = findViewById(R.id.button_stop)

        vPreset1Button = findViewById(R.id.preset_button1)
        vPreset2Button = findViewById(R.id.preset_button2)
        vPreset3Button = findViewById(R.id.preset_button3)

        vStartButton.setOnClickListener {
            Toast.makeText(this, "Put phone face down to start", Toast.LENGTH_SHORT).show()
        }

        vStopButton.setOnClickListener {
            hardStopTimer()
        }

        vPreset1Button.setOnClickListener {
            val durationArr = vPreset1Button.text.split(':')

            val hours = (durationArr[0].toIntOrNull() ?: 0).hours
            val minutes = (durationArr[1].toIntOrNull() ?: 0).minutes
            val seconds = (durationArr[2].toIntOrNull() ?: 0).seconds

            val duration = (hours + minutes + seconds).inWholeMilliseconds

            setTimer(duration)
        }
        vPreset2Button.setOnClickListener {
            val durationArr = vPreset2Button.text.split(':')

            val hours = (durationArr[0].toIntOrNull() ?: 0).hours
            val minutes = (durationArr[1].toIntOrNull() ?: 0).minutes
            val seconds = (durationArr[2].toIntOrNull() ?: 0).seconds

            val duration = (hours + minutes + seconds).inWholeMilliseconds

            setTimer(duration)
        }
        vPreset3Button.setOnClickListener {
            val durationArr = vPreset3Button.text.split(':')

            val hours = (durationArr[0].toIntOrNull() ?: 0).hours
            val minutes = (durationArr[1].toIntOrNull() ?: 0).minutes
            val seconds = (durationArr[2].toIntOrNull() ?: 0).seconds

            val duration = (hours + minutes + seconds).inWholeMilliseconds

            setTimer(duration)
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun setTimer(duration: Long) {
        millisRemaining = duration

        duration.milliseconds.toComponents { hours, minutes, seconds, _ ->
            vInputHour.setText(hours.toString().padStart(2, '0'))
            vInputMinute.setText(minutes.toString().padStart(2, '0'))
            vInputSecond.setText(seconds.toString().padStart(2, '0'))
        }
    }

    private fun startTimerLogic() {
        val h = vInputHour.text.toString().toIntOrNull() ?: 0
        val m = vInputMinute.text.toString().toIntOrNull() ?: 0
        val s = vInputSecond.text.toString().toIntOrNull() ?: 0
        millisRemaining = (h.hours + m.minutes + s.seconds).inWholeMilliseconds

        if (millisRemaining <= 0) {
            isTimerRunning = false
            return
        }

        updateUIState(false)

        countDownTimerJob = lifecycleScope.launch {
            val endTime = SystemClock.elapsedRealtime() + millisRemaining
            var lastTotalSeconds = -1L

            while (SystemClock.elapsedRealtime() < endTime) {
                val now = SystemClock.elapsedRealtime()
                millisRemaining = endTime - now

                val totalSeconds = millisRemaining / 1000

                if (totalSeconds != lastTotalSeconds) {
                    lastTotalSeconds = totalSeconds
                    updateTimerUI(millisRemaining)
                }

                delay(100)
            }

            updateUIState(true)

            millisRemaining = 0
            updateTimerUI(0)
            isTimerRunning = false
            isAlarming = true
            setScreenBrightness(-1f)
            startLoopingVibration()
        }
    }

    private fun startLoopingVibration() {
        val timings = longArrayOf(0, 500, 500)

        vibrator?.vibrate(VibrationEffect.createWaveform(timings, 0))
    }

    private fun hardStopTimer() {
        countDownTimerJob?.cancel()
        countDownTimerJob = null

        vibrator?.cancel()

        if (!isAlarming) {
            setScreenBrightness(-1f)
        }

        isTimerRunning = false
        isAlarming = false
        millisRemaining = 0

        updateUIState(true)
        updateTimerUI(0)
    }

    private fun pauseTimerLogic() {
        countDownTimerJob?.cancel()
        countDownTimerJob = null
        isTimerRunning = false
        setScreenBrightness(-1f)
    }

    private fun updateTimerUI(remaining: Long) {
        val displayMillis = if (remaining < 0) 0 else remaining
        displayMillis.milliseconds.toComponents { hours, minutes, seconds, _ ->
            vInputHour.setText(hours.toString().padStart(2, '0'))
            vInputMinute.setText(minutes.toString().padStart(2, '0'))
            vInputSecond.setText(seconds.toString().padStart(2, '0'))
        }
    }

    private fun updateUIState(allowEdit: Boolean) {
        if (allowEdit) {
            vInputHour.inputType = InputType.TYPE_CLASS_NUMBER
            vInputMinute.inputType = InputType.TYPE_CLASS_NUMBER
            vInputSecond.inputType = InputType.TYPE_CLASS_NUMBER

            vInputHour.setSelectAllOnFocus(true)
            vInputMinute.setSelectAllOnFocus(true)
            vInputSecond.setSelectAllOnFocus(true)

            vLabelTimerOff.visibility = View.VISIBLE
            vLabelTimerOn.visibility = View.GONE
            vStartButton.visibility = View.VISIBLE
            vStopButton.visibility = View.GONE

            vPreset1Button.isClickable = true
            vPreset2Button.isClickable = true
            vPreset3Button.isClickable = true
        } else {
            vInputHour.inputType = InputType.TYPE_NULL
            vInputMinute.inputType = InputType.TYPE_NULL
            vInputSecond.inputType = InputType.TYPE_NULL

            vInputHour.setSelectAllOnFocus(false)
            vInputMinute.setSelectAllOnFocus(false)
            vInputSecond.setSelectAllOnFocus(false)

            vLabelTimerOff.visibility = View.GONE
            vLabelTimerOn.visibility = View.VISIBLE
            vStartButton.visibility = View.GONE
            vStopButton.visibility = View.VISIBLE

            vPreset1Button.isClickable = false
            vPreset2Button.isClickable = false
            vPreset3Button.isClickable = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (isAlarming) return

        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> isFaceDown = event.values[2] < -8.0
            Sensor.TYPE_LIGHT -> isNear = event.values[0] < 5.0
        }

        if (isNear && isFaceDown) {
            if (!isTimerRunning) {
                isTimerRunning = true
                setScreenBrightness(0.01f)
                startTimerLogic()
            }
        } else {
            if (isTimerRunning) {
                pauseTimerLogic()
            }
        }
    }

    private fun setScreenBrightness(value: Float) {
        val lp = window.attributes
        lp.screenBrightness = value
        window.attributes = lp
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        lightSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}