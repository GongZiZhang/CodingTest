package com.arcblock.arcblockcodingtest.ext

import android.content.Context
import android.widget.Toast
import com.arcblock.arcblockcodingtest.CodingTestApplication


/**
 * 类型转换
 * @receiver Any
 * @return T
 */
inline fun <T> Any.toConversion(): T {
    return this as T
}


/**
 * 弹吐司扩展方法
 * @receiver String
 * @param context Context
 * @param duration Int
 */
fun String.showToast(
    context: Context = CodingTestApplication.context,
    duration: Int = Toast.LENGTH_LONG,
) {
    Toast.makeText(context, this, duration).show()
}

fun Int.showToast(
    context: Context = CodingTestApplication.context,
    duration: Int = Toast.LENGTH_LONG,
) {
    Toast.makeText(context, this, duration).show()
}