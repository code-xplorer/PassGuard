package com.ismail.creatvt.passguard.helpers

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
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
