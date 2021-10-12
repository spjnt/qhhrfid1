package tramais.hnb.hhrfid.ui.crop

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.itextpdf.awt.AsianFontMapper
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.GongShiPdfBean
import tramais.hnb.hhrfid.bean.LandInsureBillList
import tramais.hnb.hhrfid.interfaces.GongShiPdfBeanIn
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.ui.MainActivity
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.ShareUtils.shareWechatFriend
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CropInsureCheckPublic : BaseActivity() {
    var pageIndex = 1
    var status = "5"
    var total = 0
    var pdf_address = ""
    private var submitForCheck: TextView? = null
    private var insurePublic: TextView? = null
    private var etSearch: EditText? = null

    //private var btnSearch: Button? = null
    private var rv: RecyclerView? = null
    private var mRefreshLayout: SmartRefreshLayout? = null

    /**
     * 在上拉刷新的时候，判断，是否处于上拉刷新，如果是的话，就禁止在一次刷新，保障在数据加载完成之前
     * 避免重复和多次加载
     */
    private var isLoadMore = false
    private val handler = Handler { msg ->
        when (msg.what) {
            PDF_SAVE_START -> showAvi()
            PDF_SAVE_RESULT -> {
                hideAvi()
                shareWechatFriend(this@CropInsureCheckPublic, pdf_address)
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_pay)
    }

    private var rlPayType: LinearLayout? = null
    private var mIvArrow: ImageView? = null
    private var mTypeChoice: TextView? = null
    private var ivBack_: ImageView? = null
    private var ivHome_: ImageView? = null
    private var title: TextView? = null
    override fun initView() {
        hideAllTitle()
        mRefreshLayout = findViewById(R.id.refresh)
        submitForCheck = findViewById<View>(R.id.submit_for_check) as TextView
        insurePublic = findViewById<View>(R.id.insure_public) as TextView
        etSearch = findViewById<View>(R.id.et_search) as EditText
        rv = findViewById(R.id.lv_content)
        ivBack_ = findViewById(R.id.iv_back_public)
        ivHome_ = findViewById(R.id.setting_public)
        ivHome_!!.visibility = View.VISIBLE
        title = findViewById(R.id.root_title_public)
        title!!.text = "审核公示"
        rlPayType = findViewById(R.id.rl_pay_type)
        mIvArrow = findViewById(R.id.iv_arrow)
        mTypeChoice = findViewById(R.id.type_choice)

        // rv!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv!!.layoutManager = LinearLayoutManager(this)
        initAdapter()
    }

    private var choice_type: MutableList<String> = ArrayList()
    override fun initData() {
        choice_type.add("全部")
        choice_type.add("提交初审")
        choice_type.add("公示")
        choice_type.add("清单分享")
        submitForCheck!!.setBackgroundColor(resources.getColor(R.color.new_theme))
        insurePublic!!.setBackgroundColor(resources.getColor(R.color.ff999))

        //
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                isLoadMore = false
                getlist(status, s.toString(), pageIndex)
            }
        })
        initRefresh()
    }

    override fun onResume() {
        super.onResume()
        isLoadMore = false
        getlist(status, Utils.getEdit(etSearch), 1)
    }

    private fun getPdf(billno: String?) {
        getInstance(this)!!.GetGongShiPDFDetail(billno) { bean ->
            if (bean == null) {
                showAvi("暂无数据")
                return@GetGongShiPDFDetail
            }
            FillPdfTemplate(bean)
        }
    }

    var landInsureBillLists: MutableList<LandInsureBillList>? = ArrayList()
    private fun getlist(status: String, search: String, pageIndex: Int) {
        getInstance(this)!!.getLandInsureBillList(status, search, 20, pageIndex) { rtnCode, message, totalNums, datas ->
            total = totalNums
            if (datas != null && datas.size > 0) {

                landInsureBillLists = instant!!.parseCommonUseArr(datas, LandInsureBillList::class.java)
            } else {
                landInsureBillLists?.clear()
                showStr(message)
            }


            setAdapter(landInsureBillLists)

        }
    }

    var mAdapter: BaseQuickAdapter<LandInsureBillList, BaseViewHolder>? = null
    fun initAdapter() {

        rv!!.adapter = object : BaseQuickAdapter<LandInsureBillList, BaseViewHolder>(R.layout.item_public) {
            override fun convert(helper: BaseViewHolder, item: LandInsureBillList) {
                val tv_first = helper.getView<TextView>(R.id.tv_name)
                val tv_second = helper.getView<TextView>(R.id.tv_insure_sum)
                val tv_third = helper.getView<TextView>(R.id.tv_make_deal_time)
                val tv_fourth = helper.getView<TextView>(R.id.tv_public_time)
                val tv_five = helper.getView<TextView>(R.id.tv_address)
                val check = helper.getView<TextView>(R.id.tv_statu)
                helper.setText(R.id.tv_insure, item.fItemName)
                item?.let {
                    val fStatus = item.fStatus
                    if (fStatus == "同步") {
                        check.text = "清单分享"
                    } else {
                        check.text = fStatus
                    }
                    check.setOnClickListener { v: View? ->
                        if (!TextUtils.isEmpty(fStatus)) if (fStatus != "同步") {
                            getInstance(this@CropInsureCheckPublic)!!.setBillStatus(item.fNumber, item.fStatus) { rtnCode: Int, message: String? ->
                                showStr(message)
                                if (rtnCode == 0) {
                                    if (fStatus == "公示") getPdf(item.fNumber)
                                    isLoadMore = false
                                    getlist(status, Utils.getEdit(etSearch), 1)
                                }
                            }
                        } else {
                            getPdf(item.fNumber)
                        }
                    }
                    tv_first.text = "投保人：" + item.fname + " - " + item.fFarmerNumber
                    tv_second.text = item.fNumber
                    tv_fourth.text = item.fShowTime
                    tv_third.text = item.fUpdateTime
                    tv_five.text = item.fLandAddress
                }


            }
        }.also { mAdapter = it }


    }

    private fun setAdapter(landInsureBillLists: MutableList<LandInsureBillList>?) {
        if (!isLoadMore)
            if (landInsureBillLists == null || landInsureBillLists.size == 0) {
                showStr("暂无数据展示")
                mAdapter!!.setList(null)
                return
            }
//        LogUtils.e("isLoadMore $isLoadMore  ${landInsureBillLists.size}")

        if (isLoadMore) {
            if (landInsureBillLists != null && landInsureBillLists.isNotEmpty())
                landInsureBillLists?.let { mAdapter!!.addData(it) }
        } else
            mAdapter!!.setNewInstance(landInsureBillLists)
    }

    override fun initListner() {
        ivHome_!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        ivBack_!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        mAdapter?.let {

            it.setOnItemClickListener { adapter, view, position ->

                val landInsureBillList = it.getItem(position) as LandInsureBillList
                val intent = Intent(this@CropInsureCheckPublic, CropMakeDealActivity::class.java)
                intent.putExtra("underWrites", landInsureBillList)
                intent.putExtra("type", "list")
                startActivity(intent)

            }
        }

        rlPayType!!.setOnClickListener {
            isLoadMore = false
            mIvArrow!!.animate().setDuration(100).rotation(90f).start()//缴费状态：未同步 传f5 ，未缴费  传f7 ，已缴费  传f9
            PopuUtils(this).initChoicePop(rlPayType, choice_type) { str: String? ->
//未初审  传f1，未公示  传f3 ，已公示  传f5
                mTypeChoice!!.text = str
                status = when (str) {
                    "全部" -> "5"
                    "提交初审" -> "f1"
                    "公示" -> "f3"
                    "清单分享" -> "f5"
                    else -> "5"
                }
                mIvArrow!!.animate().setDuration(100).rotation(0f).start()
                getlist(status, Utils.getEdit(etSearch), pageIndex)
            }
        }
    }

    private fun initRefresh() {


        mRefreshLayout!!.setOnLoadMoreListener {

            if (total == 20) {
                pageIndex += 1
                isLoadMore = true
                getlist(status, Utils.getEdit(etSearch), pageIndex)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }

    }

    fun FillPdfTemplate(gongShiPdfBean: GongShiPdfBean) {
        FileUtil.copyAssetAndWrite(this, "种植业保险承保公示清单.pdf")
        val absolutePath = File(cacheDir, "种植业保险承保公示清单.pdf").absolutePath
        val sb = StringBuilder()
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        sb.append(externalStorageDirectory.absolutePath)
        sb.append(File.separator)
        sb.append("投保确认单")
        val sb2 = sb.toString()
        val file = File(sb2)
        if (!file.exists()) {
            file.mkdirs()
        }
        pdf_address = sb2 + File.separator + gongShiPdfBean.fnumber + "_" + TimeUtil.getTime("yyyyMMddHHmmsss") + ".pdf"
        Thread {
            handler.sendEmptyMessage(PDF_SAVE_START)
            var baseFont: BaseFont? = null
            try {
                baseFont = BaseFont.createFont(AsianFontMapper.ChineseSimplifiedFont, AsianFontMapper.ChineseSimplifiedEncoding_H, false)
            } catch (e: DocumentException) {
                hideAvi()
                e.printStackTrace()
            } catch (e2: IOException) {
                hideAvi()
                e2.printStackTrace()
            }
            Font(baseFont, 12.0f, 0)
            try {
                val fileOutputStream = FileOutputStream(pdf_address)
                val pdfReader = PdfReader(absolutePath)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val pdfStamper = PdfStamper(pdfReader, fileOutputStream)
                val acroFields = pdfStamper.acroFields
                val data = gongShiPdfBean.data
                val arrayList: ArrayList<String?> = ArrayList()
                val arrayList2: ArrayList<String?> = ArrayList()
                val sign_list: ArrayList<String?> = ArrayList()
                arrayList.clear()
                arrayList2.clear()
                if (data != null && data.isNotEmpty()) {
                    var i = 1
                    for (dataBean in data) {
                        arrayList.add(i.toString() + "")
                        arrayList.add(dataBean.farmername)
                        arrayList.add(dataBean.fLandCategory)
                        arrayList.add(dataBean.fLandAddress)
                        arrayList.add(dataBean.ffarmernumber)
                        arrayList.add(dataBean.farmermobile)
                        arrayList.add(dataBean.fSquare)
                        arrayList.add(dataBean.dwbe)
                        arrayList.add(dataBean.bf)
                        arrayList.add(dataBean.grbf)
                        sign_list.add(dataBean.fSignPicture)
                        i++
                    }
                }
                arrayList2.add(gongShiPdfBean.fcompanyname)
                arrayList2.add(gongShiPdfBean.fProductName)
                arrayList2.add(gongShiPdfBean.fLandAddress)
                arrayList2.add(gongShiPdfBean.fBeginDate)
                arrayList2.add(gongShiPdfBean.fEndDate)
                arrayList2.add(gongShiPdfBean.showtimebegin)
                arrayList2.add(gongShiPdfBean.showtimeend)
                arrayList2.add(gongShiPdfBean.fcompanyname)
                arrayList2.add(gongShiPdfBean.femployeename)
                arrayList2.add(gongShiPdfBean.fMobile)
                //前五个为头部数据   中间列表数据   最后尾部数据
                arrayList2.addAll(5, arrayList)

                /*  自定义属性数据*/
                val arrayList3: ArrayList<String> = ArrayList()
                arrayList3.clear()
                /*头部数据  1----5*/
                for (i2 in 1..5) {
                    arrayList3.add("Text$i2")
                }
                /*列表数据  1----5*/
                val size = arrayList.size + 6
                for (i3 in 6 until size) {
                    arrayList3.add("Text$i3")
                }
                /*尾部数据  106------110*/
                for (i4 in 106..110) {
                    arrayList3.add("Text$i4")
                }
                val size2 = arrayList2.size
                for (i5 in 0 until size2) {
                    if (i5 < arrayList2.size) {
                        acroFields.setFieldProperty(arrayList3[i5] as String, "textfont", baseFont, null as IntArray?)
                        acroFields.setField(arrayList3[i5] as String, arrayList2[i5] as String)
                    }
                }


                for (i5 in 0 until sign_list.size) {
                    var overContent = pdfStamper.getOverContent(acroFields.getFieldPositions("Image${i5 + 1}")[0].page)//追加一页
                    if (!sign_list[i5].isNullOrBlank()) {
                        try {
                            var idFontImg = Image.getInstance(sign_list[i5])//获取图片封装对象
                            val signRect: Rectangle = acroFields.getFieldPositions("Image${i5 + 1}")[0].position
                            idFontImg.setAbsolutePosition(signRect.left, signRect.bottom)
                            idFontImg.scaleToFit(signRect)//设置图片位置,及缩放
                            // idFontImg.rotation =180
                            overContent.addImage(idFontImg)//将图片添加到pdf}
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }


                    }
                }
                pdfStamper.setFormFlattening(true)
                pdfStamper.close()
//                val document = Document()
//                val pdfCopy = PdfCopy(document, fileOutputStream)
//                document.open()
//                pdfCopy.addPage(pdfCopy.getImportedPage(PdfReader(byteArrayOutputStream.toByteArray()), 1))
                pdfReader.close()
                handler.sendEmptyMessage(1048576)
            } catch (e3: IOException) {
                hideAvi()
                showStr("分享失败")
                LogUtils.e("IOException 2  " + e3.message, *arrayOfNulls(0))
                e3.printStackTrace()
            } catch (e4: BadPdfFormatException) {
                hideAvi()
                showStr("分享失败")
                LogUtils.e("BadPdfFormatException 2  " + e4.message, *arrayOfNulls(0))
                e4.printStackTrace()
            } catch (e5: DocumentException) {
                hideAvi()
                showStr("分享失败")
                LogUtils.e("DocumentException 2  " + e5.message, *arrayOfNulls(0))
                e5.printStackTrace()
            }
        }.start()
    }

    companion object {
        private const val PDF_SAVE_START = 1 shl 19
        private const val PDF_SAVE_RESULT = 1 shl 20
    }
}