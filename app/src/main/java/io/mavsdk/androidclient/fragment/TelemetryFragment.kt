package io.mavsdk.androidclient.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.mapbox.mapboxsdk.geometry.LatLng
import io.mavsdk.System
import io.mavsdk.androidclient.App
import io.mavsdk.androidclient.R
import io.mavsdk.androidclient.databinding.TelemetryFragmentBinding
import io.mavsdk.androidclient.viewmodel.TelemetryViewModel
import io.mavsdk.telemetry.Telemetry.*
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class TelemetryFragment : BaseDroneFragment() {
    private var mTelemetryBinding: TelemetryFragmentBinding? = null
    private var mLocationManager: LocationManager? = null
    private var mCurrentControllerPosition: LatLng? = null

    @SuppressLint("MissingPermission")
    private var mRequestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                5f
            ) { location ->
                mCurrentControllerPosition =
                    LatLng(location.latitude, location.longitude, location.altitude)
            }
        } else {
            Toast.makeText(requireContext(), R.string.no_gps_permission, Toast.LENGTH_LONG).show()
        }
    }


    private val mTelemetryVm: TelemetryViewModel by viewModels {
        TelemetryViewModel.Companion.ViewModelFactory(mDrone!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mLocationManager =
            requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), R.string.no_gps_permission, Toast.LENGTH_LONG)
                .show()
            mRequestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                5f
            ) { location ->
                mCurrentControllerPosition =
                    LatLng(location.latitude, location.longitude, location.altitude)
            }
        }
        mTelemetryBinding = TelemetryFragmentBinding.inflate(inflater, container, false)
        return mTelemetryBinding!!.root
    }

    private fun flightModeToString(flightMode: FlightMode): String {
        return when (flightMode) {
            FlightMode.FOLLOW_ME -> "Follow Me"
            FlightMode.LAND -> "Land"
            FlightMode.HOLD -> "Hold"
            FlightMode.MANUAL -> "Manual"
            FlightMode.MISSION -> "Mission"
            FlightMode.OFFBOARD -> "Off board"
            FlightMode.READY -> "Ready"
            FlightMode.RETURN_TO_LAUNCH -> "RTL"
            FlightMode.TAKEOFF -> "Takeoff"
            FlightMode.ACRO -> "ACRO"
            else -> "Unknown"
        }
    }

    private fun observeFlightMode() {
        mTelemetryVm.mFlightModeLiveData.observe(
            viewLifecycleOwner
        ) { flightMode ->
            mTelemetryBinding!!.droneMode.text =
                String.format(getString(R.string.mode), flightModeToString(flightMode))
        }
    }

    private fun observeGpsInfo() {
        mTelemetryVm.mGpsInfoLiveData.observe(
            viewLifecycleOwner
        ) { gpsInfo ->
            mTelemetryBinding!!.droneSatellite.text =
                String.format(
                    getString(R.string.sat),
                    gpsInfo.numSatellites,
                    gpsInfo.fixType.name
                )
        }
    }

    private fun getDistance(src: LatLng, dst: LatLng): Double {
        val earthRaids = 6378137
        val srcLatRad: Double = src.latitude * Math.PI / 180.0
        val dstLatRad: Double = dst.latitude * Math.PI / 180.0
        val latRadDiff: Double = srcLatRad - dstLatRad
        val lonRadDiff: Double = (src.longitude - src.longitude) * Math.PI / 180.0
        val sa2: Double = sin(latRadDiff / 2.0)
        val sb2: Double = sin(lonRadDiff / 2.0)
        val distance: Double =
            2 * earthRaids * asin(sqrt(sa2 * sa2 + cos(srcLatRad) * cos(dstLatRad) * sb2 * sb2))
        val altDiff: Double = src.altitude - dst.altitude
        return sqrt(distance * distance + altDiff * altDiff)
    }

    private fun observePosition() {
        mTelemetryVm.mPositionLiveData.observe(
            viewLifecycleOwner
        ) { position ->
            mTelemetryBinding!!.droneLat.text =
                String.format(getString(R.string.lat), position.latitudeDeg)
            mTelemetryBinding!!.droneLon.text =
                String.format(getString(R.string.lon), position.longitudeDeg)
            mTelemetryBinding!!.droneAlt.text =
                String.format(getString(R.string.alt), position.absoluteAltitudeM)
            mCurrentControllerPosition?.let {
                mTelemetryBinding!!.droneDis.text =
                    String.format(
                        getString(
                            R.string.dis, getDistance(
                                it, LatLng(
                                    position.latitudeDeg, position.longitudeDeg,
                                    position.absoluteAltitudeM.toDouble()
                                )
                            )

                        )
                    )
            }
        }
    }

    private fun observeBattery() {
        mTelemetryVm.mBatteryLiveData.observe(
            viewLifecycleOwner
        ) { battery ->
            mTelemetryBinding!!.droneBattery.text =
                String.format(
                    getString(R.string.battery),
                    (battery.remainingPercent * 100).toInt(),
                    '%',
                    battery.voltageV
                )
        }
    }

    private fun observeAttitude() {
        mTelemetryVm.mAltitudeLiveData.observe(
            viewLifecycleOwner
        ) { attitude ->
            mTelemetryBinding!!.droneYaw.text =
                String.format(getString(R.string.yaw), attitude.yawDeg)
        }
    }

    private fun observeVelocity() {
        mTelemetryVm.mVelocityLiveData.observe(
            viewLifecycleOwner
        ) { velocity ->
            val hSpeed =
                sqrt(velocity.eastMS * velocity.eastMS + velocity.northMS * velocity.northMS)
            mTelemetryBinding!!.droneHSpeed.text =
                String.format(getString(R.string.h_speed), hSpeed)
            mTelemetryBinding!!.droneVSpeed.text =
                String.format(getString(R.string.v_speed), velocity.downMS)
        }
    }

    private fun observeCameraAttitude() {
        mTelemetryVm.mCameraAltitudeLiveData.observe(
            viewLifecycleOwner
        ) { attitude ->
            mTelemetryBinding!!.droneMountYaw.text =
                String.format(getString(R.string.mount_yaw, attitude.yawDeg))
        }
    }

    private fun observeLandingState() {
        mTelemetryVm.mLandedStateLiveData.observe(
            viewLifecycleOwner
        ) { landedState ->
            mTelemetryBinding!!.droneState.text =
                String.format(getString(R.string.state, landedState.name))
        }
    }

    private fun observeHealth() {
        mTelemetryVm.mHealthLiveData.observe(
            viewLifecycleOwner
        ) { health ->
            if (health.isArmable) {
                mTelemetryBinding!!.droneHealth.text =
                    String.format(getString(R.string.health), getString(R.string.armable))
                mTelemetryBinding!!.droneHealth.setTextColor(Color.GREEN)
            } else {
                var healthState = ""
                if (!health.isGlobalPositionOk) healthState += "Global Position Error"
                if (!health.isLocalPositionOk) healthState += " | Local Position Error"
                if (!health.isHomePositionOk) healthState += " | Home Position Error"
                if (!health.isGyrometerCalibrationOk) healthState += " | Gyrometer Error"
                if (!health.isMagnetometerCalibrationOk) healthState += " | Magnetometer Error"
                if (!health.isAccelerometerCalibrationOk) healthState += " | Accelerometer Error"
                mTelemetryBinding!!.droneHealth.text = healthState
                mTelemetryBinding!!.droneHealth.setTextColor(Color.RED)
            }
        }
    }

    override fun onDroneReady(drone: System) {
        super.onDroneReady(drone)
        observeFlightMode()
        observeGpsInfo()
        observePosition()
        observeBattery()
        observeAttitude()
        observeVelocity()
        observeCameraAttitude()
        observeLandingState()
        observeHealth()
    }

    override fun onStart() {
        super.onStart()
        val app: App = activity?.application as App
        app.mDroneLiveData.observe(viewLifecycleOwner) { drone ->
            onDroneReady(drone)
        }
    }
}