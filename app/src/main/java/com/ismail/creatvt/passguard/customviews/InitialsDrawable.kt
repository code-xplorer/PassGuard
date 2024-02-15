package com.ismail.creatvt.passguard.customviews

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint


class InitialsDrawable(val text: String, val backgroundColor: Int, val textColor: Int) :
    Drawable() {

    val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
    }

    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(
            RectF(0f, 0f, bounds.width() * 1f, bounds.height() * 1f),
            bounds.width() / 2f,
            bounds.height() / 2f,
            backgroundPaint
        )
        textPaint.textSize = bounds.height() * 0.5f
        drawCenter(canvas, textPaint, text[0].toString().uppercase())
    }

    private val textBounds = Rect()

    private fun drawCenter(canvas: Canvas, paint: Paint, text: String) {
        canvas.getClipBounds(textBounds)
        val cHeight = textBounds.height()
        val cWidth = textBounds.width()
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, textBounds)
        val x = cWidth / 2f - textBounds.width() / 2f - textBounds.left
        val y = cHeight / 2f + textBounds.height() / 2f - textBounds.bottom
        canvas.drawText(text, x, y, paint)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

}
