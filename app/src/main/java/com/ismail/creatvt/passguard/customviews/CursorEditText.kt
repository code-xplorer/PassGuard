package com.ismail.creatvt.passguard.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.ismail.creatvt.passguard.R

class CursorEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    override fun getTextCursorDrawable(): Drawable? {
        return ResourcesCompat.getDrawable(context.resources, R.drawable.text_cursor_drawable, context.theme)
    }
}