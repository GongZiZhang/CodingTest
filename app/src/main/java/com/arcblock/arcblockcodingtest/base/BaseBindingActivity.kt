package com.arcblock.arcblockcodingtest.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.arcblock.arcblockcodingtest.R
import com.arcblock.arcblockcodingtest.ext.dp
import com.arcblock.arcblockcodingtest.ext.toConversion
import com.dylanc.viewbinding.base.inflateBindingWithGeneric

/**
 *activity基类，主要进行了对viewbinding的基础封装，方便子类省去繁琐的viewbind绑定
 * @param VB : ViewBinding
 * @property binding VB
 */
abstract class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity() {

    lateinit var binding: VB
    private lateinit var mLoadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewbinding绑定
        binding = inflateBindingWithGeneric(layoutInflater)
        setContentView(binding.root)
        //设置状态栏字体颜色为黑色
        ViewCompat.getWindowInsetsController(binding.root)?.isAppearanceLightStatusBars = true
        if (savedInstanceState == null) {
            initEvent()
        }
    }

    /**
     * loading弹窗
     */
    fun showLoading() {
        if (!::mLoadingDialog.isInitialized) {
            AlertDialog.Builder(this, R.style.transparentDialog).apply {
                val view: View = LayoutInflater.from(this@BaseBindingActivity)
                    .inflate(R.layout.dialog_loading_layout, null, false)
                setView(view)
                mLoadingDialog = create().apply { show() }
            }
        } else {
            mLoadingDialog.show()
        }


    }

    /**
     * loading弹窗取消
     */
    fun dismissLoading() {
        if (::mLoadingDialog.isInitialized) {//是否已经初始化
            mLoadingDialog.dismiss()
        }
    }

    /**
     * 初始化界面数据
     */
    abstract fun initEvent()

    open fun reload(){}

    override fun onDestroy() {
        dismissLoading()
        super.onDestroy()
    }

    /**
     * 扩展livedata方法，主要对请求错误进行处理
     * @receiver LiveData<Result<T>>
     * @param block Function1<Result<T>?, Unit>
     * @param isShowErrorView Boolean  是否展示错误界面
     */
    protected fun <T : Any> LiveData<Result<T>>.observeKt(
        isShowErrorView: Boolean = false,
        block: (Result<T>) -> Unit,
    ) {
        observe(this@BaseBindingActivity, Observer { data ->
            if (!data.isSuccess && isShowErrorView) {//展示错误界面
                errorEvent(data.exceptionOrNull())
            }
            block(data)
            dismissLoading()
        })
    }

    /**
     * 展示错误界面
     * @param t Throwable?
     */
    private fun errorEvent(t: Throwable?) {
        //实际开发中，这里会根据后台具体错误码来区分处理，这里先统一展示错误信息
        val errorTV: TextView = LayoutInflater.from(this@BaseBindingActivity)
            .inflate(R.layout.error_layout, null, false) as TextView
        binding.root.toConversion<ViewGroup>().addView(errorTV)
        errorTV.text = t?.message
        errorTV.layoutParams = errorTV.layoutParams?.apply {
            height = windowManager.defaultDisplay.height
            width = windowManager.defaultDisplay.width
        }
        errorTV.setOnClickListener {
            binding.root.toConversion<ViewGroup>().removeView(errorTV)
            reload()
        }
    }

}