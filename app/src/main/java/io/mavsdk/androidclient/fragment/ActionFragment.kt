package io.mavsdk.androidclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.mavsdk.System
import io.mavsdk.androidclient.App
import io.mavsdk.androidclient.databinding.ActionFragmentBinding
import io.mavsdk.telemetry.Telemetry.FlightMode

/**
 * A simple [Fragment] subclass.
 * Use the [ActionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActionFragment : Fragment() {
    private var mActionBinding: ActionFragmentBinding? = null
    private var mFlightMode : FlightMode = FlightMode.UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mActionBinding = ActionFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return mActionBinding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mActionBinding = null
    }

    override fun onStart() {
        super.onStart()
        val app: App = activity?.application as App
        app.mDroneLiveData.observe(viewLifecycleOwner, Observer<System> { drone ->
//            drone.telemetry.flightMode.subscribe { flightMode ->
//                activity?.runOnUiThread {
//                    this.mFlightMode = flightMode
//                    when (this.mFlightMode) {
//                        FlightMode.READY ->  {
//                            mActionBinding!!.actionTakeoff.isEnabled = true
//                            mActionBinding!!.actionMission.isEnabled = true
//                            mActionBinding!!.actionLanding.isEnabled = false
//                            mActionBinding!!.actionRtl.isEnabled = false
//                        }
//                        FlightMode.MISSION, FlightMode.MANUAL -> {
//
//                        } else {
//
//                        }
//                    }
//                }
//            }
            activity?.runOnUiThread {
                mActionBinding!!.actionLanding.setOnClickListener {
                    drone.action.land().subscribe()
                }
                mActionBinding!!.actionLanding.setOnClickListener {
                    drone.action.land().subscribe()
                }
                mActionBinding!!.actionLanding.setOnClickListener {
                    drone.action.land().subscribe()
                }
                mActionBinding!!.actionLanding.setOnClickListener {
                    drone.action.land().subscribe()
                }
            }
        })
    }

    companion object {
        fun newInstance(): ActionFragment {
            return ActionFragment()
        }
    }
}