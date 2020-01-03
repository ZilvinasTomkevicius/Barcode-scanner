package app.georentate.barcodescanner.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.georentate.barcodescanner.R
import app.georentate.barcodescanner.view.MainActivity.Companion.context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_compass.*
import java.lang.Math.toDegrees
import kotlin.math.roundToInt

class CompassActivity : AppCompatActivity(), SensorEventListener, LocationListener {

    private lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var gravity: Sensor
    lateinit var gyroscope: Sensor
    lateinit var linearAcceleration: Sensor
    lateinit var rotationVector: Sensor
    lateinit var magnetometer: Sensor

    lateinit var locationManager: LocationManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var currentDegree = 0.0f
    var lastAccelerometer = FloatArray(3)
    var lastMagnetometer = FloatArray(3)
    var lastAccelerometerSet = false
    var lastMagnetometerSet = false

    lateinit var image: ImageView
    lateinit var xValue: TextView
    lateinit var yValue: TextView
    lateinit var zValue: TextView
    lateinit var screenPosition: TextView

    lateinit var networkLocation: TextView
    lateinit var gpsLocation: TextView
    lateinit var degreesText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)?.let {
            this.gravity = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
            this.gyroscope = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
            this.linearAcceleration = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            this.rotationVector = it
        }

        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
            this.magnetometer = it
        }

        image = findViewById(R.id.compass)
        xValue = findViewById(R.id.x)
        yValue = findViewById(R.id.y)
        zValue = findViewById(R.id.z)
        screenPosition = findViewById(R.id.position)
        networkLocation = findViewById(R.id.network)
        gpsLocation = findViewById(R.id.gps)
        degreesText = findViewById(R.id.degrees)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        when(event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                setOrientationText(event)
            }
        }

        if (event?.sensor === accelerometer) {
            lowPass(event.values, lastAccelerometer)
            lastAccelerometerSet = true
        } else if (event?.sensor === magnetometer) {
            lowPass(event.values, lastMagnetometer)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                    currentDegree,
                    -degree,
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                image.startAnimation(rotateAnimation)
                currentDegree = -degree
                degreesText.text = currentDegree.roundToInt().toString()
                if(currentDegree.roundToInt() == 0) {
                    startActivity(Intent(this@CompassActivity, CameraActivity::class.java))
                    finish()
                }
            }
        }
    }

    fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f

        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
    }

    fun setOrientationText(event: SensorEvent?) {
        if(event!!.values[0].roundToInt() == 0 && event.values[1].roundToInt() == 0 && event.values[2].roundToInt() == 10)
            position.text = "UP SCREEN"
        else if(event.values[0].roundToInt() == 0 && event.values[1].roundToInt() == 10 && event.values[2].roundToInt() == 0)
            position.text = "SCREEN VERTICAL UP"
        else if(event.values[0].roundToInt() == 0 && event.values[1].roundToInt() == -10 && event.values[2].roundToInt() == 0)
            position.text = "SCREEN VERTICAL DOWN"
        else if(event.values[0].roundToInt() == 0 && event.values[1].roundToInt() == 0 && event.values[2].roundToInt() == -10){
            position.text = "DOWN SCREEN"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.finishAffinity()
            } else{
                this.finish()
                System.exit(0)
            }
        } else if(event.values[0].roundToInt() == 10 && event.values[1].roundToInt() == 0 && event.values[2].roundToInt() == 0)
            position.text = "LEFT SIDE DOWN"
        else if(event.values[0].roundToInt() == -10 && event.values[1].roundToInt() == 0 && event.values[2].roundToInt() == 0)
            position.text = "RIGHT SIDE DOWN"
        else
            position.text = "ROLLING"
        xValue.text = "X: " + event.values[0].roundToInt().toString()
        yValue.text = "Y: " + event.values[1].roundToInt().toString()
        zValue.text = "Z: " + event.values[2].roundToInt().toString()
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SENSOR_DELAY_GAME)

        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        else {
            this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1f, this)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    networkLocation.text = "Network: " + location!!.latitude.toString() + ", " + location.longitude.toString()
                }
        }

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
    }

    override fun onLocationChanged(location: Location?) {
        if(location != null)
            gpsLocation.text = "GPS: " + location.latitude.toString() + ", " + location.longitude.toString()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CompassActivity, MainActivity::class.java))
        finish()
    }
}
