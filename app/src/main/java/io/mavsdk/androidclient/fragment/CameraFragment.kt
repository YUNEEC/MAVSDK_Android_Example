package io.mavsdk.androidclient.fragment

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.mavsdk.System
import io.mavsdk.androidclient.R
import io.mavsdk.androidclient.databinding.CameraFragmentBinding
import io.mavsdk.androidclient.utils.CameraUtil
import io.mavsdk.androidclient.viewmodel.BaseFlowViewModel
import io.mavsdk.androidclient.viewmodel.CameraViewModel
import io.mavsdk.camera.Camera
import io.reactivex.disposables.Disposable

class CameraFragment : BaseDroneFragment() {
    private var mCameraFragmentBinding: CameraFragmentBinding? = null
    private val mCameraVm : CameraViewModel by viewModels {
        CameraViewModel.Companion.ViewModelFactory(mDrone!!)
    }
    private var mDisposable:Disposable?= null
    private var mRecordingAnimator: ValueAnimator = ValueAnimator.ofArgb(-0xcccccd, -0xbbcca)

    private fun initAnimator() {
        mRecordingAnimator.duration = 650
        mRecordingAnimator.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            val curValue = valueAnimator.animatedValue as Int
            mCameraFragmentBinding!!.ivRecordIndicator.setColor(curValue)
        })
        mRecordingAnimator.repeatCount = ValueAnimator.INFINITE
        mRecordingAnimator.repeatMode = ValueAnimator.REVERSE
        mRecordingAnimator.start()
        mRecordingAnimator.pause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mCameraFragmentBinding = CameraFragmentBinding.inflate(inflater, container, false)
        mCameraFragmentBinding!!.ibSwitchCamMode.setOnClickListener(View.OnClickListener {
            if (mDrone != null) {
                if (Camera.VideoStreamInfo.VideoStreamStatus.IN_PROGRESS == mCameraVm.mVideoStreamInfoLiveData.value?.status) {
                    Toast.makeText(
                        requireContext(),
                        R.string.can_not_switch_mode,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    mCameraFragmentBinding!!.ibSwitchCamMode.isEnabled = false
                    when (mCameraVm.mVideoModeLiveData.value) {
                        Camera.Mode.PHOTO -> mDrone?.camera?.setMode(Camera.Mode.VIDEO)?.doOnComplete({
                            mCameraFragmentBinding!!.ibSwitchCamMode.isEnabled = true
                        })?.doOnError({
                            Toast.makeText(requireContext(), R.string.switch_mode_fail, Toast.LENGTH_SHORT).show()
                            mCameraFragmentBinding!!.ibSwitchCamMode.isEnabled = true
                        })
                        Camera.Mode.VIDEO -> mDrone?.camera?.setMode(Camera.Mode.PHOTO)?.doOnComplete({
                            mCameraFragmentBinding!!.ibSwitchCamMode.isEnabled = true
                        })?.doOnError({
                            Toast.makeText(requireContext(), R.string.switch_mode_fail, Toast.LENGTH_SHORT).show()
                            mCameraFragmentBinding!!.ibSwitchCamMode.isEnabled = true
                        })
                        else -> {
                            Log.d(TAG, "Not got init mode for camera")
                        }
                    }
                }
            }
        })
        mCameraFragmentBinding!!.ibCapture.setOnClickListener {
            if (mDrone != null) {
                mCameraFragmentBinding!!.ibCapture.isEnabled = false
                when (mCameraVm.mVideoModeLiveData.value) {
                    Camera.Mode.PHOTO -> {
                        mDrone?.camera?.takePhoto()?.doOnComplete({
                            mCameraFragmentBinding!!.ibCapture.isEnabled = true
                        })?.doOnError({
                            Toast.makeText(requireContext(), R.string.take_photo_fail, Toast.LENGTH_SHORT).show()
                            mCameraFragmentBinding!!.ibCapture.isEnabled = true
                        })
                    }
                    Camera.Mode.VIDEO -> {
                        if (Camera.VideoStreamInfo.VideoStreamStatus.IN_PROGRESS == mCameraVm.mVideoStreamInfoLiveData.value?.status) {
                            mDrone?.camera?.startVideo()?.doOnComplete({
                                mCameraFragmentBinding!!.ibCapture.isEnabled = true
                            })?.doOnError({
                                Toast.makeText(requireContext(), R.string.start_record_fail, Toast.LENGTH_SHORT).show()
                                mCameraFragmentBinding!!.ibCapture.isEnabled = true
                            })
                        } else {
                            mDrone?.camera?.stopVideo()?.doOnComplete({
                                mCameraFragmentBinding!!.ibCapture.isEnabled = true
                            })?.doOnError({
                                Toast.makeText(requireContext(), R.string.stop_record_fail, Toast.LENGTH_SHORT).show()
                                mCameraFragmentBinding!!.ibCapture.isEnabled = true
                            })
                        }
                    }
                    else -> {
                        mCameraFragmentBinding!!.ibCapture.isEnabled = true
                    }
                }
            }
        }
        initAnimator()
        return mCameraFragmentBinding!!.root
    }

    override fun onDroneReady(drone: System) {
        super.onDroneReady(drone)
        mCameraVm.mVideoModeLiveData.observe(viewLifecycleOwner, Observer { mode ->
            when (mCameraVm.mVideoModeLiveData.value) {
                Camera.Mode.PHOTO -> {
                    mCameraFragmentBinding!!.ibSwitchCamMode.setBackgroundResource(R.drawable.btn_switch_capture_mode_selector)
                    mCameraFragmentBinding!!.ibCapture.setBackgroundResource(R.drawable.btn_capture_selector)
                }
                Camera.Mode.VIDEO -> {
                    mCameraFragmentBinding!!.ibSwitchCamMode.setBackgroundResource(R.drawable.btn_switch_video_mode_selector)
                    mCameraFragmentBinding!!.ibCapture.setBackgroundResource(R.drawable.btn_record_selector)
                }
                else -> mCameraFragmentBinding!!.ibSwitchCamMode.setBackgroundResource(R.mipmap.ic_switch_video_disable)
            }
        })

        mCameraVm.mVideoStatusLiveData.observe(
            viewLifecycleOwner,
            Observer { status ->
                if (status.videoOn) {
                    if (mCameraFragmentBinding!!.recordingContainer.visibility != View.VISIBLE) {
                        mCameraFragmentBinding!!.recordingContainer.visibility = View.VISIBLE
                        mCameraFragmentBinding!!.ibCapture.setBackgroundResource(R.mipmap.ic_recording_normal)
                        mRecordingAnimator.resume()
                    }
                    mCameraFragmentBinding!!.tvRecordTime.text = CameraUtil.getFormatRecordTime(
                        status.recordingTimeS.toLong()
                    )

                } else {
                    if (mCameraFragmentBinding!!.recordingContainer.visibility == View.VISIBLE) {
                        mCameraFragmentBinding!!.recordingContainer.visibility = View.INVISIBLE
                        mCameraFragmentBinding!!.ibCapture.setBackgroundResource(R.drawable.btn_record_selector)
                        mRecordingAnimator.pause()
                    }
                }
                mCameraFragmentBinding!!.cameraSdCapacity.text = String.format(
                    getString(R.string.sd_capacity),
                    status.availableStorageMib/1024.0f,
                    status.totalStorageMib/1024.0f
                )
            }
        )
    }

    companion object {
        const val TAG = "camera"
        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }
}