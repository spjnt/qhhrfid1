package tramais.hnb.hhrfid.ui

import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import com.joanzapata.pdfview.PDFView
import com.joanzapata.pdfview.listener.OnDrawListener
import com.joanzapata.pdfview.listener.OnLoadCompleteListener
import com.joanzapata.pdfview.listener.OnPageChangeListener
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.constant.Constants

class PdfActivity : BaseActivity(), OnPageChangeListener, OnLoadCompleteListener, OnDrawListener {
    private var pdfView: PDFView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
    }

    override fun initView() {
        setTitleText("农险条款")
        pdfView = findViewById<View>(R.id.pdf) as PDFView
    }

    override fun initData() {
        val type = intent.getStringExtra(Constants.Type)
        if (type == "种植") displayFromAssets("种植险保险条款.pdf") else displayFromAssets("养殖保险条款.pdf")
    }

    override fun initListner() {}
    private fun displayFromAssets(assetFileName: String) {
        pdfView!!.fromAsset(assetFileName) //设置pdf文件地址
                .defaultPage(1) //设置默认显示第1页
                .onPageChange(this) //设置翻页监听
                .onLoad(this) //设置加载监听
                .onDraw(this) //绘图监听
                .showMinimap(false) //pdf放大的时候，是否在屏幕的右上角生成小地图
                .swipeVertical(false) //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true) //是否允许翻页，默认是允许翻页
                //   .pages() //把 5 过滤掉
                .load()
    }

    override fun onLayerDrawn(canvas: Canvas, pageWidth: Float, pageHeight: Float, displayedPage: Int) {}

    /**
     * 翻页回调
     *
     * @param page
     * @param pageCount
     */
    override fun onPageChanged(page: Int, pageCount: Int) {
        showStr("当前第 " + page +
                " 页，共 " + pageCount + " 页")
    }

    /**
     * 加载完成回调
     *
     * @param nbPages 总共的页数
     */
    override fun loadComplete(nbPages: Int) {
        showStr("加载完成$nbPages")
    }
}