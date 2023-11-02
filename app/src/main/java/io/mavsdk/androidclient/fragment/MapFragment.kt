package io.mavsdk.androidclient.fragment

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.*
import com.mapbox.mapboxsdk.utils.ColorUtils
import io.mavsdk.System
import io.mavsdk.androidclient.MainActivity
import io.mavsdk.androidclient.R
import io.mavsdk.androidclient.databinding.MapFragmentBinding
import io.mavsdk.androidclient.viewmodel.TelemetryViewModel
import io.mavsdk.androidclient.viewmodel.WaypointViewModel

class MapFragment : BaseDroneFragment(), OnMapReadyCallback {
    private var mMapBinding: MapFragmentBinding? = null
    private val mTelemetryVm: TelemetryViewModel by viewModels {
        TelemetryViewModel.Companion.ViewModelFactory(mDrone!!)
    }
    private val mWaypointViewModel: WaypointViewModel by viewModels()

    private var mMapboxMap: MapboxMap? = null
    private var mCircleManager: CircleManager? = null
    private var mSymbolManager: SymbolManager? = null
    private var mCurrentPositionMarker: Symbol? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(this.requireContext(), getString(R.string.access_token))
        mMapBinding = MapFragmentBinding.inflate(inflater, container, false)
        mMapBinding!!.mapView.getMapAsync(this)
        mMapBinding!!.root.post(Runnable { // for instance
            (activity as MainActivity).initMainWindowSize(
                mMapBinding!!.root.measuredWidth,
                mMapBinding!!.root.measuredHeight,
            )
        })
        return mMapBinding!!.root
    }

    override fun onStart() {
        super.onStart()
        mMapBinding!!.mapView.onStart()
    }

     override fun onResume() {
        super.onResume()
        mMapBinding!!.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapBinding!!.mapView.onPause()
    }

    override fun onStop() {
        mMapBinding!!.mapView.onStop()
        super.onStop()
    }

    override fun onLowMemory() {
        mMapBinding!!.mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onDestroy() {
        mMapBinding!!.mapView.onDestroy()
        super.onDestroy()
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.uiSettings.isRotateGesturesEnabled = false
        mapboxMap.uiSettings.isTiltGesturesEnabled = false
        mapboxMap.addOnMapLongClickListener { point: LatLng? ->
            if ((activity as MainActivity).mCurrentMainWindow == MainActivity.MainWindowType.MAP) {
                mWaypointViewModel.addWaypoint(point!!)
            }
            true
        }
        mapboxMap.setStyle(Style.LIGHT) { style: Style ->
            // Add the marker image to map
            style.addImage(
                "marker-icon-id",
                BitmapFactory.decodeResource(
                    this.resources, R.drawable.mapbox_marker_icon_default
                )
            )
            mSymbolManager = SymbolManager(mMapBinding!!.mapView, mMapboxMap!!, style)
            mSymbolManager!!.iconAllowOverlap = true
            mCircleManager = CircleManager(mMapBinding!!.mapView, mMapboxMap!!, style)
        }
        mapboxMap.addOnMapClickListener {
            Log.d(TAG, "map fragment click")
            (activity as MainActivity).onMapLayoutClick()
        }
        mMapboxMap = mapboxMap
    }

    /**
     * Update [mCurrentPositionMarker] position with a new [position].
     *
     * @param newLatLng new position of the vehicle
     */
    private fun updateVehiclePosition(newLatLng: LatLng?) {
        if (newLatLng == null || mMapboxMap == null || mSymbolManager == null) {
            // Not ready
            return
        }

        // Add a vehicle marker and move the camera
        if (mCurrentPositionMarker == null) {
            val symbolOptions = SymbolOptions()
            symbolOptions.withLatLng(newLatLng)
            symbolOptions.withIconImage("marker-icon-id")
            mCurrentPositionMarker = mSymbolManager!!.create(symbolOptions)
            mMapboxMap!!.moveCamera(CameraUpdateFactory.tiltTo(0.0))
            mMapboxMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 14.0))
        } else {
            mCurrentPositionMarker!!.latLng = newLatLng
            mSymbolManager!!.update(mCurrentPositionMarker)
        }
    }


    /**
     * Update the [map] with the current mission plan waypoints.
     *
     * @param latLngs current mission waypoints
     */
    private fun updateMarkers(waypointList: List<LatLng>) {
        mCircleManager!!.deleteAll()
        for (latLng in waypointList) {
            val circleOptions = CircleOptions()
                .withLatLng(latLng)
                .withCircleColor(ColorUtils.colorToRgbaString(Color.BLUE))
                .withCircleStrokeColor(ColorUtils.colorToRgbaString(Color.BLACK))
                .withCircleStrokeWidth(1.0f)
                .withCircleRadius(12f)
                .withDraggable(false)
            mCircleManager!!.create(circleOptions)
        }
    }

    override fun onDroneReady(drone: System) {
        super.onDroneReady(drone)
        mTelemetryVm.mPositionLiveData.observe(
            viewLifecycleOwner,
            Observer { position ->
                updateVehiclePosition(LatLng(position.latitudeDeg, position.longitudeDeg))
            })
//        mWaypointViewModel.mWaypointLiveData.observe(
//            viewLifecycleOwner,
//            Observer { waypoints ->
//                updateMarkers(waypoints)
//            }
//        )
    }

    companion object {
        val TAG = "map fragment"
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
}