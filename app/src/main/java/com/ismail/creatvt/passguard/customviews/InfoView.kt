package com.ismail.creatvt.passguard.customviews

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.google.android.material.card.MaterialCardView
import com.ismail.creatvt.passguard.R

@BindingMethods(
    BindingMethod(
        attribute = "android:label",
        method = "setLabel",
        type = InfoView::class
    ),
    BindingMethod(
        attribute = "android:text",
        method = "setText",
        type = InfoView::class
    ),
    BindingMethod(
        attribute = "confidential",
        method = "setConfidential",
        type = InfoView::class
    ),
    BindingMethod(
        attribute = "textVisible",
        method = "setTextVisible",
        type = InfoView::class
    )
)
class InfoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val labelView: TextView
    private val textView: TextView
    private val eyeImage: ImageView

    private var listener: TextVisibilityListener? = null
    private var isTextVisible = true

    init {
        inflate(context, R.layout.info_view_layout, this)

        labelView = findViewById(R.id.info_label)
        textView = findViewById(R.id.info_text)
        eyeImage = findViewById(R.id.info_eye)

        eyeImage.setOnClickListener {
            listener?.onVisibilityRequest(!isTextVisible)
        }
    }

    fun setVisibilityListener(listener: TextVisibilityListener) {
        this.listener = listener
    }

    interface TextVisibilityListener {
        fun onVisibilityRequest(visible: Boolean)
    }

    fun setLabel(label: String) {
        labelView.text = label
    }

    fun setText(text: String?) {
        textView.text = text
    }

    fun setConfidential(confidential: Boolean) {
        eyeImage.visibility = if (confidential) View.VISIBLE else View.GONE
        if (confidential) {
            setTextVisible(confidential)
        } else {
            setTextVisible(true)
        }
    }

    fun setTextVisible(visible: Boolean) {
        isTextVisible = visible
        eyeImage.setImageResource(
            if (visible) R.drawable.eye_masked else R.drawable.eye_visible
        )

        textView.transformationMethod = if (visible) {
            null
        } else {
            PasswordTransformationMethod()
        }
    }
}