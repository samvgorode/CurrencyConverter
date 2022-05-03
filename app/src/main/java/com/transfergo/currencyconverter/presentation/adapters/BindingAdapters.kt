package com.transfergo.currencyconverter.presentation.adapters

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.transfergo.currencyconverter.R

@BindingAdapter("currencyDrawableStart")
fun setDrawableStart(textView: TextView, drawableStart: Int) {
    val drawable = ContextCompat.getDrawable(textView.context, drawableStart)
    val drawableEnd = ContextCompat.getDrawable(textView.context, R.drawable.ic_arrow_down_24)
    textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, drawableEnd, null)
}