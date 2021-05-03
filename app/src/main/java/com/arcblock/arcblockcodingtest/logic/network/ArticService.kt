package com.arcblock.arcblockcodingtest.logic.network

import ArticListData
import retrofit2.Call
import retrofit2.http.GET

/**
 * 文章相关数据网络请求接口Service
 */
interface ArticService {

    /**
     * 文章列表请求
     * @return Call<ArticListData>
     */
    @GET("blog/posts.json")
    fun <T> getArticList(): Call<ArticListData>

}