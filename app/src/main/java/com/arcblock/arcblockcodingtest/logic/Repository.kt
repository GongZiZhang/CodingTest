package com.arcblock.arcblockcodingtest.logic

import ArticListDataItem
import androidx.lifecycle.liveData
import com.arcblock.arcblockcodingtest.logic.network.CodingTestNetwork
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

/**
 *
 */
object Repository {

    /**
     * 获取文章列表数据
     * @param query String
     * @return LiveData<Result<ArticListdata>>
     */
    fun getArtickList(query: Long) = fire(Dispatchers.IO) {
        val articlistData: ArrayList<ArticListDataItem> = CodingTestNetwork.getArticList(query)
        if (articlistData.isNotEmpty()) {
            Result.success(articlistData)
        } else {
            Result.failure(RuntimeException("没有数据"))
        }

    }

    /**
     *利用协程统一进行try-catch处理
     * @param context CoroutineContext
     * @param block SuspendFunction0<Result<T>>
     * @return LiveData<Result<T>>
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

}