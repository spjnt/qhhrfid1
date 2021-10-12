package tramais.hnb.hhrfid.ui

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
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.AnimalInsureBillList
import tramais.hnb.hhrfid.bean.AnimalPublicBean
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.litePalBean.AnimalInsureBillListCache
import tramais.hnb.hhrfid.lv.ViewHolder
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.ui.ActivityAnimalPay
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import tramais.hnb.hhrfid.util.ShareUtils.shareWechatFriend
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ActivityAnimalPay : BaseActivity() {
    var pageIndex = 1
    var pdf_address = ""
    var total = 0
    private var etSearch: EditText? = null

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
                shareWechatFriend(this@ActivityAnimalPay, pdf_address)
            }
        }
        false
    }
    private var llTitle: LinearLayout? = null
    private var ivBack_: ImageView? = null
    private var ivHome: ImageView? = null
    private var title: TextView? = null
    private var rlPayType: LinearLayout? = null
    private var mIvArrow: ImageView? = null
    private var mTypeChoice: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_pay)
        // initView();
    }

    private var choice_type: MutableList<String> = mutableListOf("全部", "同步", "缴费", "已缴费")
    override fun initView() {
        hideAllTitle()

        mRefreshLayout = findViewById(R.id.refresh)
        etSearch = findViewById(R.id.et_search)

        rv = findViewById(R.id.lv_content)
        llTitle = findViewById(R.id.ll_title)
        llTitle!!.visibility = View.GONE
        ivBack_ = findViewById(R.id.iv_back_public)
        ivHome = findViewById(R.id.setting_public)
        ivHome!!.visibility = View.GONE
        title = findViewById(R.id.root_title_public)
        rlPayType = findViewById(R.id.rl_pay_type)
        mIvArrow = findViewById(R.id.iv_arrow)
        mTypeChoice = findViewById(R.id.type_choice)
        title!!.text = "同步缴费"
        // rv!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv!!.layoutManager = LinearLayoutManager(this)
        initAdapter()

    }

    /*缴费状态：未同步 传f5 ，未缴费  传f7 ，已缴费  传f9 */
    var status: String = "9"
    override fun initData() {


        //  getlist(Utils.getEdit(etSearch), pageIndex)
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                isLoadMore = false
                pageIndex = 1
                getlist(status, s.toString(), pageIndex)
            }
        })
        initRefresh()
    }

    var landInsureBillLists: MutableList<AnimalInsureBillList> = ArrayList()
    private fun getlist(status: String, search: String, pageIndex: Int) {
        if (NetUtil.checkNet(this))
            getInstance(this)!!.getaAnimalInsureBillList(status, search, 20, pageIndex) { rtnCode, message, totalNums, datas ->
                total = totalNums
                if (datas != null && datas.size > 0)
                    landInsureBillLists = instant!!.parseCommonUseArr(datas, AnimalInsureBillList::class.java)!!
                else {
                    landInsureBillLists.clear()
                    showStr(message)
                }


                setAdapter(landInsureBillLists, isLoadMore)

            }
        else {

            netTips()


        }
        /*  else {
              var strArr: Array<String>? = null
              strArr = if (search == null || search.isEmpty()) {
                  arrayOf("statusCode =?", status)
              } else {
                  if (Utils.isNumeric(search)) {
                      arrayOf("statusCode =? and fNumber like ? ", status, "%$search%")
                  } else {
                      arrayOf("statusCode =? and fname  like ? ", status, "%$search%")
                  }

              }

              if (pageIndex == 1) {
                  LitePal.where(*strArr).limit(20).findAsync(AnimalInsureBillListCache::class.java).listen { list ->
                      total = list.size
                      cacheToLine(list)
                  }
              } else {

                  LitePal.where(*strArr).limit(20).offset(20).findAsync(AnimalInsureBillListCache::class.java).listen { list ->
                      total = list.size
                      cacheToLine(list)
                  }
              }
          }*/
    }

    fun cacheToLine(list: MutableList<AnimalInsureBillListCache>) {
        landInsureBillLists?.clear()
        if (list == null || list.isEmpty()) {
            return
        }
        for (item in list) {
            val billList = AnimalInsureBillList()
            billList.fFarmerNumber = item.fFarmerNumber
            billList.fNumber = item.fNumber
            billList.fCreator = item.fCreator
            billList.fLabelAddress = item.fLabelAddress
            billList.fCreateTime = item.fCreateTime
            billList.fUpdateTime = item.fUpdateTime
            billList.fStatus = item.fStatus
            billList.fItemName = item.fItemName
            billList.fRiskCode = item.fRiskCode
            billList.fproductCode = item.fproductCode
            billList.fname = item.fname
            billList.fShowTime = item.fShowTime
            landInsureBillLists.add(billList)
        }
        setAdapter(landInsureBillLists, isLoadMore)

    }

    private fun setAdapter(landInsureBillLists: MutableList<AnimalInsureBillList>, isLoadMore: Boolean) {
        if (!isLoadMore)
            if (landInsureBillLists == null && landInsureBillLists.size == 0) {
                showStr("暂无数据展示")
                mAdapter!!.setList(null)
                return
            }


        if (isLoadMore) {
            if (landInsureBillLists != null && landInsureBillLists.isNotEmpty())
                mAdapter!!.addData(landInsureBillLists)
        } else
            mAdapter!!.setNewInstance(landInsureBillLists)
    }
/*
    private fun getPdf(billno: String) {
        getInstance(this)!!.GetAnimalInsureGongShiPDF(billno) { bean: AnimalPublicBean? ->
            if (bean == null) {
                showAvi("暂无数据")
                return@GetAnimalInsureGongShiPDF
            }
            turnToPdf(bean)
        }
    }

    private fun turnToPdf(bean: AnimalPublicBean) {
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
                paragraph_title.add(Chunk(if (bean.fCompanyName == null) " " else bean.fCompanyName, chunkFont_big))
                paragraph_title.add(Phrase("分公司", font))
                paragraph_title.add(Phrase("养殖保险承保公示清单", font))
                document.add(paragraph_title)
                document.add(Paragraph(Phrase("投保方式(批次或年度)", font)))
                //地址
                val paragraph_address = Paragraph()
                paragraph_address.add(Chunk(if (bean.fAddress == null) " " else bean.fAddress, chunkFont))
                *//*   paragraph_address.add(new Phrase("县(市、区)", fontsmall));
                    paragraph_address.add(new Chunk("xxx", chunkFont));
                    paragraph_address.add(new Phrase("乡(镇)", fontsmall));
                    paragraph_address.add(new Chunk("xxx", chunkFont));
                    paragraph_address.add(new Phrase("存", fontsmall));*//*document.add(paragraph_address)
                //保险时间
                val paragraph_insure_time = Paragraph()
                paragraph_insure_time.add(Phrase("保险期间:  ", fontsmall))
                paragraph_insure_time.add(Phrase(if (bean.fBeginDate == null) " " else if (bean.fBeginDate + " - " + bean.fEndDate == null) " " else bean.fEndDate, fontsmall))
                document.add(paragraph_insure_time)
                document.add(Paragraph(" ", fontsmall))
                val table = PdfPTable(8)
                val data = bean.data
                table.addCell(Phrase("序号", fontsmall))
                table.addCell(Phrase("保户姓名", fontsmall))
                table.addCell(Phrase("标的种类", fontsmall))
                table.addCell(Phrase("养殖地点", fontsmall))
                table.addCell(Phrase("承保数量", fontsmall))
                table.addCell(Phrase("总保费", fontsmall))
                table.addCell(Phrase("农户自缴保费", fontsmall))
                table.addCell(Phrase("农户签名", fontsmall))
                var index = 1
                for (item in data) {
                    table.addCell(Phrase(index++.toString(), fontsmall))
                    table.addCell(Phrase(if (item.farmername == null) " " else item.farmername, fontsmall))
                    table.addCell(Phrase(if (item.fItemName == null) " " else item.fItemName, fontsmall))
                    table.addCell(Phrase(if (item.fLabelAddress == null) " " else item.fLabelAddress, fontsmall))
                    table.addCell(Phrase(if (item.farmer_cou == null) " " else item.farmer_cou, fontsmall))
                    table.addCell(Phrase(if (item.riskAmount == null) " " else item.riskAmount, fontsmall))
                    table.addCell(Phrase(if (item.farmerRiskAmount == null) " " else item.farmerRiskAmount, fontsmall))
                    if (!TextUtils.isEmpty(item.fSignPicture)) table.addCell(Image.getInstance(item.fSignPicture)) else table.addCell(Phrase(" ", fontsmall))
                }
                table.horizontalAlignment = Element.ALIGN_LEFT
                document.add(table)
                val paragraph_public_time = Paragraph()
                paragraph_public_time.add(Phrase("公示期:  ", fontsmall))
                paragraph_public_time.add(Phrase(if (bean.fShowTime == null) " " else if (bean.fShowTime + " - " + bean.fShowTimeEnd == null) " " else bean.fShowTimeEnd, fontsmall))
                document.add(paragraph_public_time)
                val paragraph_tips = Paragraph()
                paragraph_tips.add(Phrase("注：公示期内，对公示情况如有异议，请及时与人保财险 ", fontsmall))
                paragraph_tips.add(Phrase(if (bean.fCompanyName == null) " " else bean.fCompanyName, chunkFont))
                paragraph_tips.add(Phrase("支公司联系。", fontsmall))
                document.add(paragraph_tips)
                val paragraph_con_people = Paragraph()
                paragraph_con_people.add(Phrase("联系人：", fontsmall))
                paragraph_con_people.add(Phrase(if (bean.fEmployeeName == null) " " else bean.fEmployeeName, chunkFont))
                document.add(paragraph_con_people)
                val paragraph_con_tel = Paragraph()
                paragraph_con_tel.add(Phrase("联系电话：", fontsmall))
                paragraph_con_tel.add(Phrase(if (bean.fMobile == null) " " else bean.fMobile, chunkFont))
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
    }*/

    override fun initListner() {
        ivBack_!!.setOnClickListener { v: View? -> finish() }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->

                val animalInsureBillList = it.getItem(position) as AnimalInsureBillList
                if (animalInsureBillList != null) {
                    val intent = Intent(this@ActivityAnimalPay, ActivityUnderWriteDeal::class.java)
                    intent.putExtra(Constants.Ba_num, animalInsureBillList.fNumber)
                    intent.putExtra(Constants.Type, "from_pay")
                    startActivity(intent)
                }
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
                getlist(status, Utils.getEdit(etSearch), pageIndex)
                mIvArrow!!.animate().setDuration(100).rotation(0f).start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isLoadMore = false
        getlist(status, Utils.getEdit(etSearch), 1)
    }

    var mAdapter: BaseQuickAdapter<AnimalInsureBillList, BaseViewHolder>? = null
    fun initAdapter() {

        rv!!.adapter = object : BaseQuickAdapter<AnimalInsureBillList, BaseViewHolder>(R.layout.item_public) {
            override fun convert(helper: BaseViewHolder, item: AnimalInsureBillList) {
                val tv_first = helper.getView<TextView>(R.id.tv_name)
                val tv_second = helper.getView<TextView>(R.id.tv_insure_sum)
                val tv_third = helper.getView<TextView>(R.id.tv_make_deal_time)
                val tv_fourth = helper.getView<TextView>(R.id.tv_public_time)
                val tv_five = helper.getView<TextView>(R.id.tv_address)
                val check = helper.getView<TextView>(R.id.tv_statu)
                item?.let {
                    helper.setText(R.id.tv_insure, it.fItemName)
                    val fStatus = item.fStatus
                    if (!TextUtils.isEmpty(fStatus)) check.text = fStatus else check.text = "未知状态"
                    check.setOnClickListener { v: View? ->
                        if (!NetUtil.checkNet(this@ActivityAnimalPay)) {
                            showStr("请在联网环境下操作")
                            return@setOnClickListener
                        }
                        if (!TextUtils.isEmpty(fStatus)) if (fStatus != "同步") {
                            if (fStatus == "缴费" || fStatus == "已缴费") {
                                val intent = Intent(this@ActivityAnimalPay, ActivityAnimalPayDetail::class.java)
                                intent.putExtra(Constants.Ba_num, item.fNumber)
                                intent.putExtra(Constants.Statu, item.fStatus)
                                intent.putExtra(Constants.Type, "养殖险")
                                startActivity(intent)
                            } else {
                                getInstance(this@ActivityAnimalPay)!!.SetAnimalBillStatus(item.fNumber, item.fStatus) { rtnCode: Int, message: String? ->
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
                            val intent = Intent(this@ActivityAnimalPay, ActivityAnimalPayDetail::class.java)
                            intent.putExtra(Constants.Ba_num, item.fNumber)
                            intent.putExtra(Constants.Statu, item.fStatus)
                            intent.putExtra(Constants.Type, "养殖险")
                            startActivity(intent)
                        }

                    }
                    tv_first.text = "投保人：" + item.fname + " - " + item.fFarmerNumber
                    tv_second.text = item.fNumber
                    tv_fourth.text = item.fShowTime
                    tv_third.text = item.fUpdateTime
                    tv_five.text = item.fLabelAddress
                }
            }
        }.also { mAdapter = it }
    }

    private fun initRefresh() {


        mRefreshLayout!!.setOnLoadMoreListener {

            if (total == 20) {
                pageIndex += 1
                isLoadMore = true
                getlist(status, Utils.getEdit(etSearch), pageIndex++)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }


    }

    companion object {
        private const val PDF_SAVE_START = 1 shl 19
        private const val PDF_SAVE_RESULT = 1 shl 20
    }
}