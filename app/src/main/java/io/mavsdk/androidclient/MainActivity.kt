package io.mavsdk.androidclient

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import io.mavsdk.androidclient.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "main activity"
    }

    enum class MainWindowType {
        MAP, VIDEO
    }

    var mCurrentMainWindow: MainWindowType = MainWindowType.MAP

    private var mMainActivityBinding: MainActivityBinding? = null
    private var mVideoFrameLayout: View? = null
    private var mVideoView : SurfaceView ? = null
    private var mMapFrameLayout: View? = null
    private var mMainWindowHeight: Int = 0
    private var mMainWindowWidth: Int = 0
    private var mRightCornerWindowHeight: Int = 0
    private var mRightCornerWindowWidth: Int = 0

    fun initMainWindowSize(width: Int, height: Int) {
        mMainWindowWidth = width
        mMainWindowHeight = height
    }

    fun initRightCornerWindowSize(width: Int, height: Int) {
        mRightCornerWindowWidth = width
        mRightCornerWindowHeight = height
    }

    fun onMapLayoutClick(): Boolean {
        if (mCurrentMainWindow == MainWindowType.MAP) {
            return false
        } else {
            switchMapMainWindow()
            mCurrentMainWindow = MainWindowType.MAP
            return true
        }
    }

    fun onVideoLayoutClick(): Boolean {
        if (mCurrentMainWindow == MainWindowType.VIDEO) {
            return false
        } else {
            switchVideoMainWindow()
            mCurrentMainWindow = MainWindowType.VIDEO
            return true
        }
    }

    private fun switchMapMainWindow() {
        mMapFrameLayout!!.isEnabled = false
        mVideoFrameLayout!!.isEnabled = false
        AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(mMapFrameLayout, View.X, 0f)).apply {
                with(ObjectAnimator.ofFloat(mMapFrameLayout, View.Y, 0f))
                with(ObjectAnimator.ofFloat(mMapFrameLayout, View.SCALE_X, 1f))
                with(ObjectAnimator.ofFloat(mMapFrameLayout, View.SCALE_Y, 1f))
                with(
                    ObjectAnimator.ofFloat(
                        mVideoFrameLayout,
                        View.X,
                        (mMainWindowWidth - mRightCornerWindowWidth).toFloat()
                    )
                )
                with(
                    ObjectAnimator.ofFloat(
                        mVideoFrameLayout,
                        View.Y,
                        (mMainWindowHeight - mRightCornerWindowHeight).toFloat()
                    )
                )
                with(ObjectAnimator.ofFloat(mVideoFrameLayout, View.SCALE_X, 1f))
                with(ObjectAnimator.ofFloat(mVideoFrameLayout, View.SCALE_Y, 1f))

                with(ObjectAnimator.ofFloat(mMapFrameLayout, View.TRANSLATION_Z, -1.0f))
                with(ObjectAnimator.ofFloat(mVideoFrameLayout, View.TRANSLATION_Z, 1f))
            }
            duration = 1000
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mMapFrameLayout!!.isEnabled = true
                    mVideoFrameLayout!!.isEnabled = true
                    mVideoView!!.setZOrderOnTop(true)
                    mMapFrameLayout!!.visibility = View.GONE
                    mMainActivityBinding!!.root.removeView(mMapFrameLayout)
                    mMapFrameLayout!!.visibility = View.VISIBLE
                    mMainActivityBinding!!.root.addView(mMapFrameLayout)
                }
            })
            start()
        }
    }

    private fun switchVideoMainWindow() {
        mVideoFrameLayout!!.isEnabled = false
        mMapFrameLayout!!.isEnabled = false
        AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(mVideoFrameLayout, View.X, 750f)).apply {
                with(ObjectAnimator.ofFloat(mVideoFrameLayout, View.Y, 440f))
                with(
                    ObjectAnimator.ofFloat(
                        mVideoFrameLayout,
                        View.SCALE_X,
                        mMainWindowWidth / mRightCornerWindowWidth.toFloat()
                    )
                )
                with(
                    ObjectAnimator.ofFloat(
                        mVideoFrameLayout,
                        View.SCALE_Y,
                        mMainWindowHeight / mRightCornerWindowHeight.toFloat()
                    )
                )
                with(ObjectAnimator.ofFloat(mMapFrameLayout, View.X, 750f))
                with(ObjectAnimator.ofFloat(mMapFrameLayout, View.Y, 440f))
                with(
                    ObjectAnimator.ofFloat(
                        mMapFrameLayout,
                        View.SCALE_X,
                        mRightCornerWindowWidth / mMainWindowWidth.toFloat()
                    )
                )
                with(
                    ObjectAnimator.ofFloat(
                        mMapFrameLayout,
                        View.SCALE_Y,
                        mRightCornerWindowHeight / mMainWindowHeight.toFloat()
                    )
                )

                with(ObjectAnimator.ofFloat(mVideoFrameLayout, View.TRANSLATION_Z, -1.0f))
                with(ObjectAnimator.ofFloat(mMapFrameLayout, View.TRANSLATION_Z, 1f))
            }
            duration = 1000
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    mVideoFrameLayout!!.isEnabled = true
                    mMapFrameLayout!!.isEnabled = true
                    mVideoView!!.setZOrderOnTop(false)
                    mMapFrameLayout!!.visibility = View.GONE
                    mMainActivityBinding!!.root.removeView(mMapFrameLayout)
                    mMapFrameLayout!!.visibility = View.VISIBLE
                    mMainActivityBinding!!.root.addView(mMapFrameLayout)
                }
            })
            start()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivityBinding = MainActivityBinding.inflate(layoutInflater)
        setContentView(mMainActivityBinding!!.root)
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    public override fun onResume() {
        super.onResume()
        mVideoFrameLayout = mMainActivityBinding!!.videoFragment
        mVideoView = mVideoFrameLayout!!.findViewById(R.id.video_view)
        mMapFrameLayout = mMainActivityBinding!!.mapFragment
    }
}