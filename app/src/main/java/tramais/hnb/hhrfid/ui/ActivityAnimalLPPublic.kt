package tramais.hnb.hhrfid.ui

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
import org.litepal.LitePal
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.AnimalInsureBillList
import tramais.hnb.hhrfid.bean.AnimalPublicBean
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.litePalBean.AnimalInsureBillListCache
import tramais.hnb.hhrfid.lv.ViewHolder
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.GsonUtil.Companion.instant
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ActivityAnimalLPPublic : BaseActivity() {
    var pageIndex = 1
    var pdf_address = ""
    var total = 0
    private var etSearch: EditText? = null

    private var rv: RecyclerView? = null
    private var mRefreshLayout: SmartRefreshLayout? = null
    private var rlPayType: LinearLayout? = null
    private var mIvArrow: ImageView? = null
    private var mTypeChoice: TextView? = null
    private var isLoadMore = false
    private val handler = Handler { msg ->
        when (msg.what) {
            PDF_SAVE_START -> showAvi()
            PDF_SAVE_RESULT -> {
                hideAvi()
                ShareUtils.shareWechatFriend(this@ActivityAnimalLPPublic, pdf_address)
            }
        }
        false
    }
    private var llTitle: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_pay)

    }

    var ivBack_: ImageView? = null
    private var ivHome_: ImageView? = null
    private var title: TextView? = null
    override fun initView() {
        hideAllTitle()
        rlPayType = findViewById(R.id.rl_pay_type)
        mIvArrow = findViewById(R.id.iv_arrow)
        mTypeChoice = findViewById(R.id.type_choice)
        mRefreshLayout = findViewById(R.id.refresh)
        etSearch = findViewById(R.id.et_search)
        rv = findViewById(R.id.lv_content)
        llTitle = findViewById(R.id.ll_title)
        llTitle!!.visibility = View.GONE
        ivBack_ = findViewById(R.id.iv_back_public)
        ivHome_ = findViewById(R.id.setting_public)
        title = findViewById(R.id.root_title_public)
        title!!.text = "审核公示"
        //  rv!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv!!.layoutManager = LinearLayoutManager(this)
        initAdapter()
    }

    private var choice_type: MutableList<String> = mutableListOf("全部", "提交初审", "公示", "清单分享")

    override fun initData() {
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                pageIndex = 1
                isLoadMore = false
                if (isResume)
                    getlist(status_, s.toString(), pageIndex)
            }
        })
        initRefresh()
    }

    var isResume: Boolean = false
    override fun onResume() {
        super.onResume()
        getlist(status_, Utils.getEdit(etSearch), pageIndex)
        isResume = true
    }

    var landInsureBillLists: MutableList<AnimalInsureBillList> = ArrayList()
    private fun getlist(status: String, search: String?, pageIndex: Int) {
        status_ = when (status) {
            "全部" -> "5"
            "提交初审" -> "f1"
            "公示" -> "f3"
            "清单分享" -> "f5"
            else -> "5"
        }
        if (NetUtil.checkNet(this)) {
            landInsureBillLists?.clear()
            RequestUtil.getInstance(this)!!.getaAnimalInsureBillList(status_, search, 20, pageIndex)
            { rtnCode, message, totalNums, datas ->
                total = totalNums
                if (datas != null && datas.size > 0)
                    landInsureBillLists = instant!!.parseCommonUseArr(datas, AnimalInsureBillList::class.java)!!
                else {
                    landInsureBillLists.clear()
                    showStr(message)
                }
                setAdapter(landInsureBillLists, isLoadMore)

            }
        }else{
            netTips()
        }/* else {
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
        /* if (list == null || list.isEmpty()) {
             showStr("暂无数据展示")
             mAdapter?.setList(null)
           //  rv!!.adapter = null
             return
         }*/
        landInsureBillLists?.clear()
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
            landInsureBillLists!!.add(billList)
        }
        setAdapter(landInsureBillLists, isLoadMore)

    }

    var mAdapter: BaseQuickAdapter<AnimalInsureBillList, BaseViewHolder>? = null
    fun initAdapter() {

        rv!!.adapter = object : BaseQuickAdapter<AnimalInsureBillList, BaseViewHolder>(R.layout.item_public) {
            override fun convert(holder: BaseViewHolder, item: AnimalInsureBillList) {

                val tv_first = holder.getView<TextView>(R.id.tv_name)
                val tv_second = holder.getView<TextView>(R.id.tv_insure_sum)
                val tv_third = holder.getView<TextView>(R.id.tv_make_deal_time)
                val tv_fourth = holder.getView<TextView>(R.id.tv_public_time)
                val tv_five = holder.getView<TextView>(R.id.tv_address)
                val check = holder.getView<TextView>(R.id.tv_statu)
                val fStatus = item?.fStatus
                holder.setText(R.id.tv_insure, item.fItemName)
                if (fStatus == "同步") {
                    check.text = "清单分享"
                } else {
                    check.text = fStatus
                }
                check.setOnClickListener { v: View? ->
                    if (!NetUtil.checkNet(this@ActivityAnimalLPPublic)) {
                        showStr("请在联网环境下操作")
                        return@setOnClickListener
                    }
                    if (!TextUtils.isEmpty(fStatus)) if (fStatus != "同步") {
                        RequestUtil.getInstance(this@ActivityAnimalLPPublic)!!.SetAnimalBillStatus(item?.fNumber, fStatus) { rtnCode: Int, message: String? ->
                            showStr(message)
                            if (rtnCode == 0) {
                                if (fStatus == "公示") getPdf(item!!.fNumber.toString())
                                isLoadMore = false
                                getlist(status_, Utils.getEdit(etSearch), 1)

                            } else {
                                showStr(message)
                            }
                        }
                    } else {
                        getPdf(item!!.fNumber.toString())
                    }

                }
                tv_first.text = "投保人：" + item?.fname + " - " + item?.fFarmerNumber
                tv_second.text = item?.fNumber
                tv_third.text = item?.fUpdateTime
                tv_fourth.text = item?.fShowTime
                tv_five.text = item?.fLabelAddress
            }
        }.also { mAdapter = it }
    }


    private fun setAdapter(landInsureBillLists: MutableList<AnimalInsureBillList>, isLoadMore: Boolean) {
        if (!isLoadMore)
            if (landInsureBillLists == null || landInsureBillLists.size == 0) {
                showStr("暂无数据展示")
                mAdapter!!.setList(null)
                return
            }
        if (isLoadMore) {
            if (landInsureBillLists != null && landInsureBillLists.isNotEmpty())
                mAdapter!!.addData(landInsureBillLists)
        } else {
            mAdapter!!.setList(landInsureBillLists)


        }

    }

    private fun getPdf(billno: String) {
        RequestUtil.getInstance(this)!!.GetAnimalInsureGongShiPDF(billno,object :GetCommon<AnimalPublicBean>{
            override fun getCommon(t: AnimalPublicBean) {
                if (t.code >= 0) {
                    showAvi()
                    fillPdfTemplate(t)
                } else showStr(t.msg)
            }
        })
    }

    var status_: String = "5"
    override fun initListner() {
        ivHome_!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        ivBack_!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        mAdapter?.let { adapter ->

            adapter.setOnItemClickListener { adapter, view, position ->
                val item = adapter.getItem(position) as AnimalInsureBillList
                val intent = Intent(this@ActivityAnimalLPPublic, ActivityUnderWriteDeal::class.java)
                intent.putExtra(Constants.Ba_num, item.fNumber)
                intent.putExtra(Constants.Type, "from_public")
                startActivity(intent)
            }


        }
        /*      rv!!.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                  val landInsureBillList = landInsureBillLists_[position]
                  if (landInsureBillList != null) {
                      val intent = Intent(this@ActivityAnimalLPPublic, ActivityUnderWriteDeal::class.java)
                      intent.putExtra(Constants.Ba_num, landInsureBillList.fNumber)
                      intent.putExtra(Constants.Type, "from_public")
                      startActivity(intent)
                  }
              }
      */
        rlPayType!!.setOnClickListener {
            isLoadMore = false
            mIvArrow!!.animate().setDuration(100).rotation(90f).start()//缴费状态：未同步 传f5 ，未缴费  传f7 ，已缴费  传f9
            PopuUtils(this).initChoicePop(rlPayType, choice_type) { str: String? ->
//未初审  传f1，未公示  传f3 ，已公示  传f5
                mTypeChoice!!.text = str

                mIvArrow!!.animate().setDuration(100).rotation(0f).start()
                getlist(str!!, Utils.getEdit(etSearch), pageIndex)
            }
        }
    }

    private fun initRefresh() {

        mRefreshLayout!!.setOnLoadMoreListener {

            if (total == 20) {
                pageIndex += 1
                isLoadMore = true
                getlist(status_, Utils.getEdit(etSearch), pageIndex++)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }

    }

    fun fillPdfTemplate(animalPublicBean: AnimalPublicBean) {
        FileUtil.copyAssetAndWrite(this, "养殖业保险承保公示清单.pdf")
        val absolutePath = File(cacheDir, "养殖业保险承保公示清单.pdf").absolutePath
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
        pdf_address = sb2 + File.separator + animalPublicBean.fnumber + "_" + TimeUtil.getTime("yyyyMMddHHmmsss") + ".pdf"
        Thread {
            try {
                val createFont = BaseFont.createFont(AsianFontMapper.ChineseSimplifiedFont, AsianFontMapper.ChineseSimplifiedEncoding_H, false)
                handler.sendEmptyMessage(PDF_SAVE_START)
                val fileOutputStream = FileOutputStream(pdf_address)
                val pdfReader = PdfReader(absolutePath)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val pdfStamper = PdfStamper(pdfReader, fileOutputStream)
                val acroFields = pdfStamper.acroFields
                val data = animalPublicBean.data
                val array_table: ArrayList<String> = ArrayList()
                val array_head_all: ArrayList<String> = ArrayList()
                array_table.clear()
                array_head_all.clear()
                val array_sign: ArrayList<String> = ArrayList()
                array_sign.clear()
                if (data != null && data.size > 0) {
                    var i = 1
                    for (dataBean in data) {
                        array_table.add((i++).toString())
                        array_table.add(dataBean.farmername)
                        array_table.add(dataBean.fItemName)
                        array_table.add(dataBean.fLabelAddress)
                        array_table.add(dataBean.farmer_cou)
                        array_table.add(dataBean.riskAmount)
                        array_table.add(dataBean.farmerRiskAmount)
                        array_sign.add(dataBean?.fSignPicture ?: "")

                    }
                }
                //头部
                array_head_all.add(animalPublicBean.fCompanyName)
                array_head_all.add("年度")
                array_head_all.add(animalPublicBean.fAddress)
                array_head_all.add(animalPublicBean.fBeginDate)
                array_head_all.add(animalPublicBean.fEndDate)
                array_head_all.add(animalPublicBean.fUnitAmount)
                array_head_all.add(animalPublicBean.fUnitRiskAmount)
                //尾部
                array_head_all.add(animalPublicBean.fShowTime)
                array_head_all.add(animalPublicBean.fShowTimeEnd)
                array_head_all.add(animalPublicBean.fCompanyName)
                array_head_all.add(animalPublicBean.fEmployeeName)
                array_head_all.add(animalPublicBean.fMobile)
                //中间表格
                array_head_all.addAll(7, array_table)

                //编辑表格得填充
                val arrayList4: ArrayList<String> = ArrayList<String>()
                arrayList4.clear()
                for (i2 in 1..7) {
                    arrayList4.add("Text$i2")
                }
                var i3 = 8
                val size = array_table.size + 7
                if (8 <= size) {
                    while (true) {
                        arrayList4.add("Text$i3")
                        if (i3 == size) {
                            break
                        }
                        i3++
                    }
                }
                for (i4 in 88..92) {
                    arrayList4.add("Text$i4")
                }
                val size2 = array_head_all.size
                for (i5 in 0 until size2) {
                    if (i5 < array_head_all.size) {
                        acroFields.setFieldProperty(arrayList4[i5], "textfont", createFont, null as IntArray?)
                        acroFields.setField(arrayList4[i5], array_head_all[i5])
                    }
                }

                for (i5 in 0 until array_sign.size) {
                    val s = array_sign[i5]
                    if (!s.isNullOrBlank()) {
                        try {
                            var overContent = pdfStamper.getOverContent(acroFields.getFieldPositions("Image${i5 + 1}")[0].page)//追加一页
                            var idFontImg = Image.getInstance(s)//获取图片封装对象
                            val signRect: Rectangle = acroFields.getFieldPositions("Image${i5 + 1}")[0].position
                            idFontImg.setAbsolutePosition(signRect.left, signRect.bottom)
                            idFontImg.scaleToFit(signRect)//设置图片位置,及缩放
                            // idFontImg.rotation =180
                            overContent.addImage(idFontImg)//将图片添加到pdf
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }

                }
                pdfStamper.setFormFlattening(true)
                pdfStamper.close()
                pdfReader.close()
                handler.sendEmptyMessage(PDF_SAVE_RESULT)
            } catch (e: IOException) {
                showStr("分享失败")
                e.printStackTrace()
                LogUtils.e("IOException 2  " + e.message, *arrayOfNulls(0))
                hideAvi()
            } catch (e2: BadPdfFormatException) {
                showStr("分享失败")
                hideAvi()
                LogUtils.e("BadPdfFormatException 2  " + e2.message, *arrayOfNulls(0))
                e2.printStackTrace()
            } catch (e3: DocumentException) {
                showStr("分享失败")
                hideAvi()
                LogUtils.e("DocumentException 2  " + e3.message, *arrayOfNulls(0))
                e3.printStackTrace()
            }
        }.start()
    }

    companion object {
        private const val PDF_SAVE_START = 1 shl 19
        private const val PDF_SAVE_RESULT = 1 shl 20
    }
}