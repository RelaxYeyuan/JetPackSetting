package com.neusoft.mc2.setting.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.neusoft.mc2.setting.R

class CustomeScrollBar(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var mPaint: Paint? = null
    private var userNoticeCursorWidth = 0f
    private var mPaintBg: Paint? = null
    private fun initRes() {
        mPaintBg = Paint()
        mPaintBg!!.color = context.getColor(R.color.transparent)
        mPaintBg!!.style = Paint.Style.FILL
        mPaintBg!!.isAntiAlias = true
        visibility = INVISIBLE
        mPaint = Paint()
        mPaint!!.color = context.getColor(R.color.scroll_view_slider)
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.isAntiAlias = true
        userNoticeCursorWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        @SuppressLint("DrawAllocation") val rectF = RectF(
            userNoticeCursorWidth / 4,
            0F,
            userNoticeCursorWidth / 4 * 2,
            height.toFloat()
        )
        canvas.drawRect(rectF, mPaintBg)
        val height = height - 68
        @SuppressLint("DrawAllocation") val rect3 = RectF(
            userNoticeCursorWidth / 4,
            height * cursorMoveY,
            userNoticeCursorWidth / 4 * 2,
            height * cursorMoveY + 68
        )
        canvas.drawRect(rect3, mPaint)
    }

    private var cursorMoveY = 0f
    fun SetOffSet(cursorMoveY: Float) {
        this.cursorMoveY = cursorMoveY
        invalidate()
    }

    companion object {
        fun dp2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

    init {
        initRes()
    }
}