package tramais.hnb.hhrfid.ui

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.grandtech.corelibrary.widget.picker.common.util.ScreenUtils
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.util.PackageUtils

class ActivityVersion : BaseActivity() {
    private var version: TextView? = null
    private var appContent: WebView? = null
    private var logoImg: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version)
    }

    override fun initView() {
        logoImg = findViewById(R.id.logo_img)
        setTitleText(R.string.app_name)
        version = findViewById(R.id.version)
        appContent = findViewById(R.id.version_web)
        logoImg!!.bringToFront()
        version!!.text = "版本号  " + PackageUtils.getVersionName(this)
        val options = RequestOptions.bitmapTransform(RoundedCorners(20)) //图片圆角为30

        Glide.with(this).load(R.mipmap.new_logo) //图片地址
                .apply(options)
                .into(logoImg!!)
    }

    override fun initData() {
        //声明WebSettings子类
        val webSettings = appContent!!.settings


        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可


        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件

        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        appContent!!.loadUrl(Config.AppDescription)
        appContent!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val javascript = "javascript:function ResizeImages() {" +
                        "var myimg,oldwidth;" +
                        "var maxwidth = document.body.clientWidth;" +
                        "for(i=0;i <document.images.length;i++){" +
                        "myimg = document.images[i];" +
                        "if(myimg.width > maxwidth){" +
                        "oldwidth = myimg.width;" +
                        "myimg.width = maxwidth;" +
                        "}" +
                        "}" +
                        "}"
                val width = ScreenUtils.widthPixels(this@ActivityVersion).toString()
                view.loadUrl(javascript)
                view.loadUrl("javascript:ResizeImages();")
            }
        }
    }

    override fun initListner() {

    }
}