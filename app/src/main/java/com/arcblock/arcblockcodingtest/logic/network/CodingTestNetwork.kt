package com.arcblock.arcblockcodingtest.logic.network

import ArticListData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 网络数据请求
 */
object CodingTestNetwork {

    private val articService = ServiceCreator.create<ArticService>()

    suspend fun getArticList(query: Long) = articService.getArticList<ArticListData>().await()

    /**
     * 通过挂起函数封装，省去了繁琐的接口回调
     * @receiver Call<T>
     * @return T
     */
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(RuntimeException("response body is null"))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.d("TAG",t.message+":")
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}