package io.mavsdk.androidclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.mavsdk.MavsdkEventQueue
import io.mavsdk.System
import io.mavsdk.telemetry.Telemetry.*

class TelemetryViewModel(mDrone: System) : BaseFlowViewModel(mDrone) {
    val mGpsInfoLiveData = MutableLiveData<GpsInfo>()
    val mFlightModeLiveData = MutableLiveData<FlightMode>()
    val mPositionLiveData = MutableLiveData<Position>()
    val mBatteryLiveData = MutableLiveData<Battery>()
    val mAltitudeLiveData = MutableLiveData<EulerAngle>()
    val mVelocityLiveData = MutableLiveData<VelocityNed>()
    val mLandedStateLiveData = MutableLiveData<LandedState>()
    val mHealthLiveData = MutableLiveData<Health>()
    val mCameraAltitudeLiveData = MutableLiveData<EulerAngle>()

    init {
        MavsdkEventQueue.executor().execute {
            subscribeData<GpsInfo>(mDrone.telemetry.gpsInfo, mGpsInfoLiveData)
            subscribeData<FlightMode>(mDrone.telemetry.flightMode, mFlightModeLiveData)
            subscribeData<Position>(mDrone.telemetry.position, mPositionLiveData)
            subscribeData<Battery>(mDrone.telemetry.battery, mBatteryLiveData)
            subscribeData<EulerAngle>(mDrone.telemetry.attitudeEuler, mAltitudeLiveData)
            subscribeData<VelocityNed>(mDrone.telemetry.velocityNed, mVelocityLiveData)
            subscribeData<LandedState>(mDrone.telemetry.landedState, mLandedStateLiveData)
            subscribeData<Health>(mDrone.telemetry.health, mHealthLiveData)
            subscribeData<EulerAngle>(mDrone.telemetry.cameraAttitudeEuler, mCameraAltitudeLiveData)
        }
    }

    companion object {
        class ViewModelFactory (
        private val mDrone: System
        ) : ViewModelProvider.Factory {

            override fun <TelemetryViewModel : ViewModel> create(modelClass: Class<TelemetryViewModel>): TelemetryViewModel {
                return TelemetryViewModel(mDrone) as TelemetryViewModel
            }
        }
    }
}