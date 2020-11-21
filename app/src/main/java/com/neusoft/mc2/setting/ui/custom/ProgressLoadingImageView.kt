package com.neusoft.mc2.setting.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.RotateAnimation

class ProgressLoadingImageView(context: Context?, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatImageView(
        context!!,
        attrs
    ) {
    @Volatile
    private var animRun = false // 动画是否正在运行

    var rotateAnimation: RotateAnimation? = null

    /**
     * 展示转动动画
     */
    fun showAnimation() {
        if (animRun) {
            stopAnimation()
        }
        if (rotateAnimation == null) {
            animRun = true
            rotateAnimation = RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotateAnimation!!.repeatCount = Animation.INFINITE
            rotateAnimation!!.duration = 800
            rotateAnimation!!.fillAfter = true
            startAnimation(rotateAnimation)
        } else if (!animRun) {
            animRun = true
            startAnimation(rotateAnimation)
        }
    }

    public fun stopAnimation() {
        if (rotateAnimation != null) {
            animRun = false
            rotateAnimation!!.cancel()
        }
    }
}