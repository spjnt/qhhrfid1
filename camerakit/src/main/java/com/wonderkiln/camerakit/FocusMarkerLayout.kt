package com.wonderkiln.camerakit

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.camerakit.R

class FocusMarkerLayout @JvmOverloads constructor(
    @NonNull context: Context?,
    @Nullable attrs: AttributeSet? = null
) : FrameLayout(context!!, attrs) {
    private val mFocusMarkerContainer: FrameLayout
    private val mFill: ImageView
    fun focus(mx: Float, my: Float) {
        var mx = mx
        var my = my
        mx *= getWidth().toFloat()
        my *= getHeight().toFloat()
        val x = (mx - mFocusMarkerContainer.getWidth() / 2) as Int
        val y = (my - mFocusMarkerContainer.getWidth() / 2) as Int
        mFocusMarkerContainer.setTranslationX(x.toFloat())
        mFocusMarkerContainer.setTranslationY(y.toFloat())
        mFocusMarkerContainer.animate().setListener(null).cancel()
        mFill.animate().setListener(null).cancel()
        mFill.scaleX = 0f
        mFill.scaleY = 0f
        mFill.alpha = 1f
        mFocusMarkerContainer.setScaleX(1.36f)
        mFocusMarkerContainer.setScaleY(1.36f)
        mFocusMarkerContainer.setAlpha(1f)
        mFocusMarkerContainer.animate().scaleX(1f).scaleY(1f).setStartDelay(0).setDuration(330)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mFocusMarkerContainer.animate().alpha(0f).setStartDelay(750).setDuration(800)
                        .setListener(null).start()
                }
            }).start()
        mFill.animate().scaleX(1f).scaleY(1f).setDuration(330)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    mFill.animate().alpha(0f).setDuration(800).setListener(null).start()
                }
            }).start()
    }

    init {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_focus_marker, this)
        mFocusMarkerContainer = findViewById<FrameLayout>(R.id.focusMarkerContainer)
        mFill = findViewById<ImageView>(R.id.fill)
        mFocusMarkerContainer.setAlpha(0f)
    }
}