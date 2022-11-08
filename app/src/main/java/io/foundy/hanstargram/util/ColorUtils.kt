package io.foundy.hanstargram.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
fun Context.themeColor(@AttrRes attrRes: Int): Int = TypedValue()
    .apply { theme.resolveAttribute(attrRes, this, true) }
    .data