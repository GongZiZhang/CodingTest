package com.arcblock.arcblockcodingtest.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arcblock.arcblockcodingtest.R
import com.arcblock.arcblockcodingtest.base.BaseBindingActivity
import com.arcblock.arcblockcodingtest.databinding.ActivityWebViewBinding
import com.arcblock.arcblockcodingtest.logic.network.ServiceCreator
import com.arcblock.arcblockcodingtest.base.contant.ContantData.ARG1
import java.net.URISyntaxException


/**
 * 网页浏览界面
 */
class WebViewActivity : BaseBindingActivity<ActivityWebViewBinding>() {

    private val mUrl by lazy {
        ServiceCreator.BASE_URL + intent.getStringExtra(ARG1)
    }

    /**
     * 实现Video标签全屏播放
     */
    private var fullScreenView: View? = null


    override fun initEvent() {
        showLoading()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        initToolBar()
        initWebView()
        binding.apply {
            webview.loadUrl(mUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webview.apply {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                javaScriptCanOpenWindowsAutomatically = true//支持通过JS打开新窗口
                loadsImagesAutomatically = true//开始先不加载图片
                allowFileAccess = true//允许访问文件
                loadWithOverviewMode = true// 缩放至屏幕的大小
                useWideViewPort = true// 将图片调整到适合webview大小
                mediaPlaybackRequiresUserGesture = false//自动播放视频
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//混合加载模式
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    //获取网页标题
                    binding.webviewToolbar.title = view?.title
                    dismissLoading()
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    binding.webview.settings.cacheMode = WebSettings.LOAD_NO_CACHE // 不加载缓存内容
                    if (view?.let {
                            shouldOverrideUrlLoadingByApp(
                                it,
                                request?.url.toString()
                            )
                        } == true) {
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    showLoading()
                }
            }
            webChromeClient = object : WebChromeClient() {
                //外理h5视频全屏问题
                override fun onShowCustomView(view: View, callback: CustomViewCallback?) {
                    // 此处的 view 就是全屏的视频播放界面，需要把它添加到我们的界面上
                    binding.webviewCl.addView(
                        view
                    )
                    // 去除状态栏和导航按钮
                    fullScreenView = view
                    setScreen()
                }

                override fun onHideCustomView() {
                    // 退出全屏播放，我们要把之前添加到界面上的视频播放界面移除
                    binding.webviewCl.removeView(fullScreenView)
                    setScreen()
                    fullScreenView = null
                }
            }
        }
    }


    /**
     * 根据url的scheme处理跳转第三方app的业务
     * @param view WebView
     * @param url String
     * @return Boolean
     */
    private fun shouldOverrideUrlLoadingByApp(view: WebView, url: String): Boolean {
        if (url.startsWith("http") || url.startsWith("https") || url.startsWith("ftp")) {
            //不处理http, https, ftp的请求
            return false
        }
        val intent: Intent = try {
            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        } catch (e: URISyntaxException) {
            return false
        }
        intent.component = null
        try {
            this.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            return true
        }
        return true
    }

    /**
     * 初始化标题栏
     */
    private fun initToolBar() {
        binding.apply {
            webviewToolbar.setNavigationOnClickListener { finish() }
            // 设置Menu
            webviewToolbar.inflateMenu(R.menu.toolbar_menu)
            webviewToolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
                when (it.itemId) {
                    //刷新
                    R.id.refresh -> webview.reload()
                    //清空缓存
                    R.id.clearCache -> webview.clearCache(false)
                    //用浏览器打开
                    R.id.openBrowser -> {
                        startActivity(Intent().apply {
                            action = Intent.ACTION_VIEW
                            data = Uri.parse(mUrl)
                        })
                    }
                }
                return@OnMenuItemClickListener true
            })
        }
    }

    /**
     * 横竖屏切换
     */
    private fun setScreen() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            ViewCompat.getWindowInsetsController(binding.root)
                ?.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            ViewCompat.getWindowInsetsController(binding.root)
                ?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    override fun onBackPressed() {
        // 全屏状态点击返回退出全屏
        if (fullScreenView != null) {
            binding.webviewCl.removeView(fullScreenView)
            fullScreenView = null
        } else {
            super.onBackPressed()
        }
    }

    /**
     * 处理返回手势，用户通过非标题栏返回键返回时，不退出界面，只返回上一次网页
     * @param keyCode Int
     * @param event KeyEvent
     * @return Boolean
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.webview.canGoBack()) {
            binding.webview.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK//返回上一页不重新加载
            if (fullScreenView == null) {
                binding.webview.goBack()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 清除webview
     */
    override fun onDestroy() {
        binding.webview.removeAllViews()
        binding.webview.destroy()
        super.onDestroy()
    }
}