package io.mavsdk.androidclient.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class IndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mPaint: Paint? = null
    private fun init(context: Context) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.style = Paint.Style.FILL
    }

    fun setColor(colorInt: Int) {
        mColorInt = colorInt
        invalidate()
    }

    private var mColorInt = Color.rgb(0x33, 0X33, 0x33)

    init {
        init(context)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint!!.color = mColorInt
        val drawWidth = (width / 2).toFloat()
        val drawHeight = (height / 2).toFloat()
        canvas.drawCircle(drawWidth, drawHeight, drawWidth, mPaint!!)
    }
}