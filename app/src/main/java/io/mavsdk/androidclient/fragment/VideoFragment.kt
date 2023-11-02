package io.mavsdk.androidclient.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceHolder.Callback
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.mavsdk.System
import io.mavsdk.androidclient.MainActivity
import io.mavsdk.androidclient.databinding.VideoFragmentBinding
import io.mavsdk.androidclient.viewmodel.CameraViewModel
import io.mavsdk.camera.Camera
import tv.danmaku.ijk.media.player.IjkMediaPlayer


class VideoFragment : BaseDroneFragment() {
    companion object {
        const val TAG = "Video Fragment"
    }
    private var mVideoBinding: VideoFragmentBinding? = null
    private val mCameraVm: CameraViewModel by viewModels {
        CameraViewModel.Companion.ViewModelFactory(mDrone!!)
    }

    private var mIMediaPlayer: IjkMediaPlayer? = null
    @SuppressLint("UnsafeOptInUsageError")
    override fun onDroneReady(drone: System) {
        super.onDroneReady(drone)
        mCameraVm.mVideoStreamInfoLiveData.observe(viewLifecycleOwner, Observer { videoStreamInfo ->
           if (Camera.VideoStreamInfo.VideoStreamSpectrum.VISIBLE_LIGHT == videoStreamInfo.spectrum && mIMediaPlayer!!.dataSource == null) {
               mIMediaPlayer!!.setDataSource(
                   requireContext(),
                   Uri.parse(videoStreamInfo.settings.uri)
               )
               IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_INFO)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "threads", 1)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "nobuffer", 1)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "flush_packets", 1)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC,  "direct", 1)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC,  "discardcorrupt", 1)

               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "an", 1)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1)
               mIMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0)


               mIMediaPlayer!!.prepareAsync()
               mIMediaPlayer!!.start()
               mIMediaPlayer!!.isLooping
           }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mVideoBinding = VideoFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        mVideoBinding!!.root.post(Runnable { // for instance
            (activity as MainActivity).initRightCornerWindowSize(
                mVideoBinding!!.root.measuredWidth,
                mVideoBinding!!.root.measuredHeight
            )
        })
        mVideoBinding!!.videoView.setOnClickListener {
            Log.d(TAG, "video View click")
            (activity as MainActivity).onVideoLayoutClick()
        }
        mIMediaPlayer = IjkMediaPlayer()
        mVideoBinding!!.videoView.holder.addCallback(object : Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                mIMediaPlayer!!.setSurface(surfaceHolder.surface)
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            }

            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
            }
        })

        return mVideoBinding!!.root
    }

    override fun onDestroyView() {
        mIMediaPlayer!!.stop()
        mIMediaPlayer!!.release()
        super.onDestroyView()
    }
}