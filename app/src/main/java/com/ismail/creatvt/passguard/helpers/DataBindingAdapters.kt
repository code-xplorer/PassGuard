package com.ismail.creatvt.passguard.helpers

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_FORCED
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputLayout
import com.ismail.creatvt.passguard.helpers.ColorFactory.textColor
import com.ismail.creatvt.passguard.customviews.InfoView
import com.ismail.creatvt.passguard.customviews.InitialsDrawable
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.addpassword.ExtraViewModel
import com.ismail.creatvt.passguard.databinding.ExtraInfoInputLayoutBinding
import com.ismail.creatvt.passguard.databinding.ExtraInfoViewLayoutBinding
import com.ismail.creatvt.passguard.model.Website
import com.ismail.creatvt.passguard.viewpassword.ViewExtraViewModel
import java.util.*

@BindingAdapter("errorMessage")
fun TextInputLayout.setErrorMessage(errorMessage:String?) {
    Log.d("DataBindingAdapters", "Error Message: $errorMessage")
    error = errorMessage
}

@BindingAdapter("lifecycleOwner", "editExtras")
fun LinearLayoutCompat.setExtras(owner: LifecycleOwner?, extras:List<ExtraViewModel>?) {
    if(extras == null || owner == null) return
    val inflater = LayoutInflater.from(context)
    removeAllViews()
    extras.forEach {
        val extraInfoBinding = DataBindingUtil.inflate<ExtraInfoInputLayoutBinding>(inflater,
            R.layout.extra_info_input_layout, this, false)
        extraInfoBinding.viewModel = it
        extraInfoBinding.lifecycleOwner = owner
        addView(extraInfoBinding.root)
    }
}

@BindingAdapter("lifecycleOwner", "viewExtras")
fun LinearLayoutCompat.setViewExtras(owner: LifecycleOwner?, extras:List<ViewExtraViewModel>?) {
    if(extras == null || owner == null) return
    val inflater = LayoutInflater.from(context)
    removeAllViews()
    extras.forEach {
        val extraInfoBinding = DataBindingUtil.inflate<ExtraInfoViewLayoutBinding>(inflater,
            R.layout.extra_info_view_layout, this, false)
        extraInfoBinding.viewModel = it
        extraInfoBinding.lifecycleOwner = owner
        addView(extraInfoBinding.root)
    }
}

@BindingAdapter("websiteIcon")
fun ImageView.setWebsiteIcon(website: Website) {
    val icon = WebsiteFactory.getIcon(website.icon)
    val color = context.getColor(ColorFactory.getColorForName(website.color).colorRes)
    if(icon != null) {
        setBackgroundResource(R.drawable.website_icon_background)
        backgroundTintList = ColorStateList.valueOf(color)
        setPadding((background.intrinsicWidth * 0.25f).toInt())
        setImageResource(icon)
    } else {
        setPadding(0)
        Log.d("InitialsDrawableTag", "Setting initials for ${website.color}")
        val drawable = InitialsDrawable(website.websiteName, color, color.textColor).apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
        setImageDrawable(drawable)
    }
}

private fun ImageView.setAndStartAnimation(animation:AnimatedVectorDrawable) {
    setImageDrawable(animation)
    animation.start()
}

@BindingAdapter("forwardAnimation", "backwardAnimation", "android:onClick", requireAll = false)
fun ImageView.setAnimatedIconSwitch(forward:Drawable?, backward:Drawable?, onClickListener: OnClickListener?) {
    val forwardAnimation = forward as? AnimatedVectorDrawable
    val backwardAnimation = backward as? AnimatedVectorDrawable

    if(forwardAnimation == null || backwardAnimation == null) return

    setOnClickListener {
        if(tag == null || tag == "Forward") {
            setAndStartAnimation(forwardAnimation)
            tag = "Backward"
        } else {
            setAndStartAnimation(backwardAnimation)
            tag = "Forward"
        }
        onClickListener?.onClick(it)
    }
}

@BindingAdapter("keyboardEnabled")
fun EditText.setKeyboardEnabled(isKeyboardEnabled:Boolean) {
    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

    if(isKeyboardEnabled) {
        inputMethodManager.showSoftInput(this, SHOW_IMPLICIT)
    } else {
        setText("")
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

@BindingAdapter("selected")
fun AutoCompleteTextView.setSelected(selection:String) {
    setText(selection, false)
}

@InverseBindingAdapter(attribute = "selected")
fun AutoCompleteTextView.getSelected():String {
    return text.toString()
}

@BindingAdapter("selectedAttrChanged")
fun AutoCompleteTextView.setSelectAttrChangedListener(listener: InverseBindingListener) {
    setOnItemClickListener { _, _, _, _ ->
        listener.onChange()
    }
}

@BindingAdapter("onVisibilityRequest")
fun InfoView.setVisibilityRequestListener(listener: InfoView.TextVisibilityListener) {
    setVisibilityListener(listener)
}
