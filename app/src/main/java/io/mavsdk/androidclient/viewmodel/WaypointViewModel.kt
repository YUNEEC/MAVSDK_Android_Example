package io.mavsdk.androidclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.geometry.LatLng

class WaypointViewModel : ViewModel() {
    val mWaypointLiveData: MutableLiveData<ArrayList<LatLng>> = MutableLiveData(ArrayList<LatLng>())

    fun addWaypoint(waypoint:LatLng) {
        val currentWaypointList: ArrayList<LatLng> = mWaypointLiveData.value!!
        currentWaypointList.add(waypoint)
        mWaypointLiveData.postValue(currentWaypointList)
    }
}