package com.camerakit


import android.widget.FrameLayout
import android.view.ScaleGestureDetector
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.OnScaleGestureListener

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater

abstract class GestureLayout : FrameLayout {
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize()
    }

    private fun initialize() {
        mScaleGestureDetector = ScaleGestureDetector(context, mScaleGestureListener)
        mGestureDetector = GestureDetector(context, mGestureListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGestureDetector!!.onTouchEvent(event)
        mScaleGestureDetector!!.onTouchEvent(event)
        return true
    }

    protected abstract fun onTap(x: Float, y: Float)
    protected abstract fun onLongTap(x: Float, y: Float)
    protected abstract fun onDoubleTap(x: Float, y: Float)
    protected abstract fun onPinch(ds: Float, dsx: Float, dsy: Float)
    fun performTap(x: Float, y: Float) {
        onTap(x, y)
    }

    fun performLongTap(x: Float, y: Float) {
        onLongTap(x, y)
    }

    fun performDoubleTap(x: Float, y: Float) {
        onDoubleTap(x, y)
    }

    fun performPinch(dsx: Float, dsy: Float) {
        val ds = Math.sqrt((dsx * dsx + dsy * dsy).toDouble()).toFloat()
        onPinch(ds, dsx, dsy)
    }

    private val mGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            performTap(e.x / width.toFloat(), e.y / height.toFloat())
            return super.onSingleTapConfirmed(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            performDoubleTap(e.x / width.toFloat(), e.y / height.toFloat())
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            performLongTap(e.x / width.toFloat(), e.y / height.toFloat())
        }
    }
    private val mScaleGestureListener: OnScaleGestureListener = object : OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val dsx = detector.currentSpanX - detector.previousSpanX
            val dsy = detector.currentSpanY - detector.previousSpanY
            performPinch(dsx, dsy)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {}
    }
}