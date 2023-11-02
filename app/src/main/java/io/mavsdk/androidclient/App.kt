package io.mavsdk.androidclient

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.mavsdk.MavsdkEventQueue
import io.mavsdk.System
import io.mavsdk.mavsdkserver.MavsdkServer

class App : Application() {
    companion object {
        const val TAG: String = "mavsdk-app"
        const val BACKEND_IP_ADDRESS = "127.0.0.1"
        const val MAVLINK_LISTEN_ADD = "udp://0.0.0.0:14560"
    }

    private var mMavsdkServer: MavsdkServer? = null
    val mDroneLiveData: MutableLiveData<System> = MutableLiveData<System>()
    private var mDrone: System? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()
        MavsdkEventQueue.executor().execute {
            mMavsdkServer = MavsdkServer()
            val mavsdkServerPort = mMavsdkServer!!.run(MAVLINK_LISTEN_ADD)
            val drone = System(BACKEND_IP_ADDRESS, mavsdkServerPort)
            Log.d(TAG, "Drone Ready, mavsdk port: $mavsdkServerPort")
            mDroneLiveData.postValue(drone)
            mDrone = drone
        }
    }

    override fun onTerminate() {
        Log.d(TAG, "onTerminate")
        mDrone?.dispose()
        mMavsdkServer?.stop()
        mMavsdkServer?.destroy()
        super.onTerminate()
    }

    override fun onLowMemory() {
        Log.d(TAG, "onLowMemory")
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        Log.d(TAG, "onTrimMemory")
        super.onTrimMemory(level)
    }
}