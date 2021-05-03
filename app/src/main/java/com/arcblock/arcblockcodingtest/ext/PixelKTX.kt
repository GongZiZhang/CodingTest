package com.arcblock.arcblockcodingtest.ext

import android.content.res.Resources
import android.util.TypedValue

/**
 * 把dp值转成px值
 */
inline val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

/**
 * 把dp值转成px值
 */
inline val Int.dp
    get() = this.toFloat().dp.toInt()