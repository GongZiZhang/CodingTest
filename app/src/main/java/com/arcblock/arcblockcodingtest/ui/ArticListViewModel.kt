package com.arcblock.arcblockcodingtest.ui

import ArticListDataItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.arcblock.arcblockcodingtest.logic.Repository

/**
 * 文章viewmodel类
 */
class ArticListViewModel : ViewModel() {

    //文章列表数据
    var articList = mutableListOf<ArticListDataItem>()

    private val queryData = MutableLiveData<Long>()

    /**
     * 观察请求数据变动并进行数据请求
     */
    val articLiveData = Transformations.switchMap(queryData) { location ->
        queryData.value?.let { Repository.getArtickList(location) }
    }

    /**
     * 因为没有具体的请求参数，所以这里以当前时间戳为判断依据，方便进行数据请求操作
     * @param query String  时间戳
     */
    fun requestArticListData(query: Long) {
        queryData.value = query
    }
}