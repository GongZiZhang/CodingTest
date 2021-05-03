package com.arcblock.arcblockcodingtest

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import okhttp3.OkHttpClient

/**
 *项目自定义application
 */
class CodingTestApplication : Application(), ImageLoaderFactory {

    /**
     * 全局设置图片加载coil参数
     * @return ImageLoader
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .availableMemoryPercentage(0.25)//使用百分之25的应用可用内存
            .crossfade(true)
            .placeholder(R.drawable.image_loading_background)//加载位图
            .error(R.drawable.image_loading_background)//错误位图
            .allowRgb565(true)//使用565颜色值，降低使用内存
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(applicationContext))//磁盘缓存
                    .build()
            }
            .build()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}