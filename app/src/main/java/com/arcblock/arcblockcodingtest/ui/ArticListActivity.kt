package com.arcblock.arcblockcodingtest.ui

import ArticListDataItem
import android.content.Intent
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.arcblock.arcblockcodingtest.R
import com.arcblock.arcblockcodingtest.base.BaseBindingActivity
import com.arcblock.arcblockcodingtest.databinding.ActivityArticlistBinding
import com.arcblock.arcblockcodingtest.base.contant.ContantData.ARG1
import com.arcblock.arcblockcodingtest.ext.showToast
import com.arcblock.arcblockcodingtest.ext.toConversion
import com.arcblock.arcblockcodingtest.ui.adapter.ArticListItemAdapter
import kotlin.system.exitProcess

/**
 * 文章列表界面
 * @property mViewModel ArticListViewModel
 */
class ArticListActivity : BaseBindingActivity<ActivityArticlistBinding>() {

    val mViewModel by viewModels<ArticListViewModel>()

    private val mArticListItemAdapter by lazy {
        ArticListItemAdapter(R.layout.item_articlist_layout, mViewModel.articList)
    }

    override fun initEvent() {
        showLoading()
        initRecyclerView()//初始化recyclerview列表
        initData()//处理数据请求回调等
        request()
    }


    /**
     * 处理数据请求回调等
     */
    private fun initData() {
        mViewModel.articLiveData.observeKt(isShowErrorView = true) { it ->
            it.getOrNull()?.let {
                mViewModel.articList.addAll(it)
                mArticListItemAdapter.notifyDataSetChanged()
            }
            if (binding.articListSrl.isRefreshing) {
                binding.articListSrl.isRefreshing = false
            }
        }
    }

    fun request(){
        showLoading()
        mViewModel.requestArticListData(System.currentTimeMillis())
    }
    override fun reload(){
        request()
    }

    /**
     * 初始化recyclerview列表
     */
    private fun initRecyclerView() {
        binding.apply {
            articListSrl.setOnRefreshListener {
                //下拉刷新
                mViewModel.articList.clear()
                mViewModel.requestArticListData(System.currentTimeMillis())
            }
            articListRv.layoutManager = LinearLayoutManager(this@ArticListActivity)
            articListRv.adapter = mArticListItemAdapter
            mArticListItemAdapter.setOnItemClickListener { adapter, view, position ->
                //跳转到详情h5页面
                startActivity(Intent(this@ArticListActivity, WebViewActivity::class.java).apply {
                    putExtra(
                        ARG1,
                        adapter.data[position]?.toConversion<ArticListDataItem>()?.frontmatter?.path
                    )
                })
            }
        }
    }

    // 再点一次退出程序时间设置
    private val WAIT_TIME = 2000L
    private var TOUCH_TIME = 0L

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            finish()
            exitProcess(0)
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            "再按一次退出".showToast()
        }
        return true
    }

}