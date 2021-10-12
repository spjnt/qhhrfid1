package tramais.hnb.hhrfid.ui.crop

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apkfuns.logutils.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.GongShiPdfBean
import tramais.hnb.hhrfid.bean.LandInsureBillList
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GongShiPdfBeanIn
import tramais.hnb.hhrfid.lv.ViewHolder
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.ui.ActivityAnimalPayDetail
import tramais.hnb.hhrfid.ui.crop.CropMakeDealActivity
import tramais.hnb.hhrfid.ui.crop.CropSyncPayment
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PopuUtils
import tramais.hnb.hhrfid.util.ShareUtils.shareWechatFriend
import tramais.hnb.hhrfid.util.TimeUtil
import tramais.hnb.hhrfid.util.Utils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class CropSyncPayment : BaseActivity() {
    var pageIndex = 1
    var status = "9"
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
    private var isLoadMore = true
    private val handler = Handler { msg ->
        when (msg.what) {
            PDF_SAVE_START -> showAvi()
            PDF_SAVE_RESULT -> {
                hideAvi()
                shareWechatFriend(this@CropSyncPayment, pdf_address)
            }
        }
        false
    }
    private var ivBack_: ImageView? = null
    private var ivHome: ImageView? = null
    private var title: TextView? = null

    private var rlPayType: LinearLayout? = null
    private var mIvArrow: ImageView? = null
    private var mTypeChoice: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_pay)
        //initView();
    }

    override fun initView() {
        hideAllTitle()
        // setTitleText("同步缴费");
        mRefreshLayout = findViewById(R.id.refresh)
        submitForCheck = findViewById<View>(R.id.submit_for_check) as TextView
        insurePublic = findViewById<View>(R.id.insure_public) as TextView
        etSearch = findViewById<View>(R.id.et_search) as EditText
        // btnSearch = findViewById<View>(R.id.btn_search) as Button
        rv = findViewById(R.id.lv_content)
        ivBack_ = findViewById(R.id.iv_back_public)
        ivHome = findViewById(R.id.setting_public)
        ivHome!!.visibility = View.GONE
        title = findViewById(R.id.root_title_public)
        title!!.text = "同步缴费"

        rlPayType = findViewById(R.id.rl_pay_type)
        mIvArrow = findViewById(R.id.iv_arrow)
        mTypeChoice = findViewById(R.id.type_choice)

        // rv!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv!!.layoutManager = LinearLayoutManager(this)
        initAdapter()
    }

    private var choice_type: MutableList<String> = mutableListOf("全部", "同步", "缴费", "已缴费")
    override fun initData() {
        submitForCheck!!.setBackgroundColor(resources.getColor(R.color.new_theme))
        insurePublic!!.setBackgroundColor(resources.getColor(R.color.ff999))


        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                isLoadMore = false
                getlist(status, s.toString(), 1)
            }
        })
        initRefresh()
    }

    private fun getPdf(billno: String) {
        getInstance(this)!!.GetGongShiPDFDetail(billno, GongShiPdfBeanIn { bean ->
            if (bean == null) {
                showAvi("暂无数据")
                return@GongShiPdfBeanIn
            }
            turnToPdf(bean)
        })
    }

    var landInsureBillLists: MutableList<LandInsureBillList>? = ArrayList()
    private fun getlist(status: String, search: String, pageIndex: Int) {
        if (!NetUtil.checkNet(this)) {
            showStr("请在联网环境下进行")
            return
        }
        getInstance(this)!!.getLandInsureBillList(status, search, 20, pageIndex) { rtnCode, message, totalNums, datas ->
            total = totalNums
            if (datas != null && datas.size > 0)
                landInsureBillLists = instant!!.parseCommonUseArr(datas, LandInsureBillList::class.java)

            setAdapter(landInsureBillLists)

        }
    }

    private fun setAdapter(landInsureBillLists: MutableList<LandInsureBillList>?) {
        if (landInsureBillLists == null || landInsureBillLists.size == 0) {
            showStr("暂无数据展示")
            mAdapter!!.setList(null)
            return
        }

//        LogUtils.e("isLoadMore $isLoadMore  ${landInsureBillLists.size}")
        if (isLoadMore)
            landInsureBillLists?.let { mAdapter!!.addData(it) }
        else
            mAdapter!!.setNewInstance(landInsureBillLists)
    }

    override fun onResume() {
        super.onResume()
        isLoadMore = false
        getlist(status, Utils.getEdit(etSearch), 1)
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
                    val fStatus = it.fStatus
                    check.text = fStatus
                    if (!NetUtil.checkNet(this@CropSyncPayment)) {
                        showStr("请在联网环境下操作")
                        return@let
                    }
                    check.setOnClickListener { v: View? ->
                        if (!TextUtils.isEmpty(fStatus))
                            if (fStatus != "同步") {
                                if (fStatus == "缴费" || fStatus == "已缴费") {
                                    val intent = Intent(this@CropSyncPayment, ActivityAnimalPayDetail::class.java)
                                    intent.putExtra(Constants.Ba_num, item.fNumber)
                                    intent.putExtra(Constants.Statu, item.fStatus)
                                    intent.putExtra(Constants.Type, "种植险")
                                    startActivity(intent)
                                } else {
                                    getInstance(this@CropSyncPayment)!!.SetAnimalBillStatus(item.fNumber, item.fStatus) { rtnCode: Int, message: String? ->
                                        showStr(message)
                                        if (rtnCode == 0) {
                                            // if (fStatus.equals("公示"))
                                            //getPdf(item.getFNumber());
                                            isLoadMore = false
                                            getlist(status, Utils.getEdit(etSearch), 1)
                                        }
                                    }
                                }
                            } else {
                                val intent = Intent(this@CropSyncPayment, ActivityAnimalPayDetail::class.java)
                                intent.putExtra(Constants.Ba_num, item.fNumber)
                                intent.putExtra(Constants.Statu, item.fStatus)
                                intent.putExtra(Constants.Type, "种植险")
                                startActivity(intent)
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

    override fun initListner() {
        ivBack_!!.setOnClickListener { v: View? -> finish() }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val landInsureBillList = it.getItem(position) as LandInsureBillList
                val intent = Intent(this@CropSyncPayment, CropMakeDealActivity::class.java)
                intent.putExtra("underWrites", landInsureBillList)
                intent.putExtra("type", "list")
                startActivity(intent)
            }
        }
        rlPayType!!.setOnClickListener {
            isLoadMore = false
            mIvArrow!!.animate().setDuration(100).rotation(90f).start()
            PopuUtils(this).initChoicePop(rlPayType, choice_type) { str: String? ->

                mTypeChoice!!.text = str
                status = when (str) {
                    "全部" -> "9"
                    "同步" -> "f5"
                    "缴费" -> "f7"
                    "已缴费" -> "f9"
                    else -> "9"
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

    private fun turnToPdf(bean: GongShiPdfBean) {
        val ADDRESS = Environment.getExternalStorageDirectory().absolutePath + File.separator + "投保确认单"
        val file = File(ADDRESS)
        if (!file.exists()) file.mkdirs()
        pdf_address = (ADDRESS + File.separator + bean.fnumber + "_"
                + TimeUtil.getTime(Constants.yyyyMMddHHmmss) + ".pdf")
        val rectangle = Rectangle(PageSize.A4)
        rectangle.backgroundColor = BaseColor.WHITE
        rectangle.border = Rectangle.BOX
        rectangle.borderColor = BaseColor.LIGHT_GRAY
        rectangle.borderWidth = 5f
        val document = Document(rectangle)
        Thread {
            try {
                handler.sendEmptyMessage(PDF_SAVE_START)
                PdfWriter.getInstance(document, FileOutputStream(pdf_address))
                document.addTitle("标题")
                document.addAuthor("作者")
                document.addCreationDate()
                document.addCreator("创建者")
                document.open()
                //图片
//                    Image image = Image.getInstance("Config.PDF_LOGO_URL");
//                    image.setIndentationLeft(10);
                document.add(Image.getInstance(Config.PDF_LOGO_URL))

                //头部字体  String name, String encoding, boolean embedded
                val baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED)
                val font: Font = Font(baseFont, 18f, Font.BOLD)
                val fontsmall: Font = Font(baseFont, 12f, Font.NORMAL)
                val chunkFont_big = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED, 18f, Font.UNDERLINE)
                val chunkFont = FontFactory.getFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED, 12f, Font.UNDERLINE)

                //头
                val paragraph_title = Paragraph()
                paragraph_title.add(Phrase("中国人民财产保险股份有限公司", font))
                paragraph_title.add(Chunk(bean.fcompanyname, chunkFont_big))
                paragraph_title.add(Chunk(bean.fProductName, chunkFont_big))
                paragraph_title.add(Phrase("种植保险承保公示清单", font))
                document.add(paragraph_title)
                //地址
                val paragraph_address = Paragraph()
                paragraph_address.add(Chunk(bean.fLandAddress, chunkFont))
                /*   paragraph_address.add(new Phrase("县(市、区)", fontsmall));
                    paragraph_address.add(new Chunk("xxx", chunkFont));
                    paragraph_address.add(new Phrase("乡(镇)", fontsmall));
                    paragraph_address.add(new Chunk("xxx", chunkFont));
                    paragraph_address.add(new Phrase("存", fontsmall));*/document.add(paragraph_address)
                //保险时间
                val paragraph_insure_time = Paragraph()
                paragraph_insure_time.add(Phrase("保险期间:  ", fontsmall))
                paragraph_insure_time.add(Phrase(bean.fBeginDate + " - " + bean.fEndDate, fontsmall))
                document.add(paragraph_insure_time)
                document.add(Paragraph(" ", fontsmall))
                val table = PdfPTable(8)
                val data = bean.data
                table.addCell(Phrase("序号", fontsmall))
                table.addCell(Phrase("被保险人姓名", fontsmall))
                table.addCell(Phrase("保险标的项目", fontsmall))
                table.addCell(Phrase("种植地点", fontsmall))
                table.addCell(Phrase("保险数量(亩)", fontsmall))
                table.addCell(Phrase("每亩保险金额(元)", fontsmall))
                table.addCell(Phrase("总保险费(元)）", fontsmall))
                table.addCell(Phrase("农户自交保险费(元)", fontsmall))
                table.addCell(Phrase("农户签名", fontsmall))
                var index = 1
                for (item in data!!) {
                    table.addCell(Phrase(index++.toString(), fontsmall))
                    table.addCell(Phrase(item.farmername, fontsmall))
                    table.addCell(Phrase(item.fLandCategory, fontsmall))
                    table.addCell(Phrase(item.fLandAddress, fontsmall))
                    table.addCell(Phrase(item.fSquare, fontsmall))
                    table.addCell(Phrase(item.dwbe, fontsmall))
                    table.addCell(Phrase(item.bf, fontsmall))
                    table.addCell(Phrase(item.grbf, fontsmall))
                    table.addCell(Image.getInstance(item.fSignPicture))
                }
                table.horizontalAlignment = Element.ALIGN_LEFT
                document.add(table)
                val paragraph_public_time = Paragraph()
                paragraph_public_time.add(Phrase("公示期:  ", fontsmall))
                paragraph_public_time.add(Phrase(bean.showtimebegin + " - " + bean.showtimeend, fontsmall))
                document.add(paragraph_public_time)
                val paragraph_tips = Paragraph()
                paragraph_tips.add(Phrase("注：公示期内，对公示情况如有异议，请及时与人保财险 ", fontsmall))
                paragraph_tips.add(Phrase(bean.fcompanyname, chunkFont))
                paragraph_tips.add(Phrase("联系。", fontsmall))
                document.add(paragraph_tips)
                val paragraph_con_people = Paragraph()
                paragraph_con_people.add(Phrase("联系人：", fontsmall))
                paragraph_con_people.add(Phrase(bean.femployeename, chunkFont))
                document.add(paragraph_con_people)
                val paragraph_con_tel = Paragraph()
                paragraph_con_tel.add(Phrase("联系电话：", fontsmall))
                paragraph_con_tel.add(Phrase(bean.fMobile, chunkFont))
                document.add(paragraph_con_tel)
                document.add(Paragraph(" ", fontsmall))
                document.add(Paragraph("承保公司盖章：", font))
                document.add(Paragraph(" ", fontsmall))
                document.add(Paragraph(" ", fontsmall))
                document.add(Paragraph(" ", fontsmall))
                document.add(Paragraph(" ", fontsmall))
                document.close()
                handler.sendEmptyMessage(PDF_SAVE_RESULT)
            } catch (e: DocumentException) {
                Log.e("DocumentException", e.message.toString())
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                Log.e("FileNot", e.message.toString())
                e.printStackTrace()
            } catch (e: IOException) {
                Log.e("IOException", e.message.toString())
                e.printStackTrace()
            }
        }.start()
    }

    companion object {
        private const val PDF_SAVE_START = 1 shl 19
        private const val PDF_SAVE_RESULT = 1 shl 20
    }
}