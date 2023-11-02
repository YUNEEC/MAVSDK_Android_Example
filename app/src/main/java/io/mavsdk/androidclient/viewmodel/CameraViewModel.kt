package io.mavsdk.androidclient.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.mavsdk.MavsdkEventQueue
import io.mavsdk.System
import io.mavsdk.camera.Camera.*

class CameraViewModel(mDrone: System) : BaseFlowViewModel(mDrone) {
    val mVideoStreamInfoLiveData = MutableLiveData<VideoStreamInfo>()
    val mVideoModeLiveData = MutableLiveData<Mode>()
    val mVideoStatusLiveData = MutableLiveData<Status>()
    val mCaptureInfoLiveData = MutableLiveData<CaptureInfo>()

    init {
        MavsdkEventQueue.executor().execute {
            subscribeData<VideoStreamInfo>(mDrone.camera.videoStreamInfo, mVideoStreamInfoLiveData)
            subscribeData<Mode>(mDrone.camera.mode, mVideoModeLiveData)
            subscribeData<Status>(mDrone.camera.status, mVideoStatusLiveData)
            subscribeData<CaptureInfo>(mDrone.camera.captureInfo, mCaptureInfoLiveData)
        }
    }
    companion object {
        class ViewModelFactory (
            private val mDrone: System
        ) : ViewModelProvider.Factory {

            override fun <CameraViewModel : ViewModel> create(modelClass: Class<CameraViewModel>): CameraViewModel {
                return CameraViewModel(mDrone = mDrone) as CameraViewModel
            }
        }
    }
}