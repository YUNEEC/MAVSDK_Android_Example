package io.mavsdk.androidclient.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.mavsdk.System
import io.mavsdk.androidclient.App
import io.reactivex.disposables.Disposable

open class BaseDroneFragment : Fragment() {
    var mDrone: System?=null

    open fun onDroneReady(drone:System) {
        mDrone = drone
    }

    override fun onStart() {
        super.onStart()
        val app: App = activity?.application as App
        app.mDroneLiveData.observe(viewLifecycleOwner, Observer<System> { drone ->
            onDroneReady(drone)
        })
    }
}