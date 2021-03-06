package tramais.hnb.hhrfid.ui

import android.content.Context
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.apkfuns.logutils.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.itextpdf.awt.AsianFontMapper
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.BadPdfFormatException
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.AnimalLiPeiPdfBean
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.bean.ResultBean
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.interfaces.GetResult
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.dialog.DialogCheckInfo
import tramais.hnb.hhrfid.util.*
import tramais.hnb.hhrfid.util.ShareUtils.shareWechatFriend
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ActivityAnimalCBPublic : BaseActivity() {
    var pageIndex = 1
    var pdf_address = ""
    private val mContext: Context = this@ActivityAnimalCBPublic
    private var etSearch: EditText? = null

    private var rv: RecyclerView? = null
    private var mRefreshLayout: SmartRefreshLayout? = null

    private var llTitle: LinearLayout? = null
    private var ivBack_: ImageView? = null
    private var ivHome: ImageView? = null
    private var title: TextView? = null
    private var rlPayType: LinearLayout? = null
    private var mIvArrow: ImageView? = null
    private var mTypeChoice: TextView? = null
    var fenPeis: MutableList<FenPei?> = ArrayList()
    private val handler = Handler { msg ->
        when (msg.what) {
            PDF_SAVE_START -> {
                LogUtils.e("come  in")
                showAvi()
            }
            PDF_SAVE_RESULT -> {
                hideAvi()
                shareWechatFriend(this@ActivityAnimalCBPublic, pdf_address)
            }
            3 -> {
                fenPeis = msg.obj as MutableList<FenPei?>
                setAdapter(
                        fenPeis, isLoadMore
                )
                // lvContent!!.adapter = CheckAdapter(context, fenPeis).also { }
                hideAvi()
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal_pay)
    }

    var module_name: String? = null
    override fun initView() {
        hideAllTitle()
        module_name = intent.getStringExtra(Constants.MODULE_NAME)
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
        title!!.text = "????????????"
        //  rlPayType!!.visibility = View.GONE
        // rv!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv!!.layoutManager = LinearLayoutManager(this)
        initAdapter()

    }

    override fun onResume() {
        super.onResume()

        if (NetUtil.checkNet(mContext)) {
            isLoadMore = false
            currentIndex = 1
            getBill(Utils.getEdit(etSearch), "7", currentIndex)
        } else {
            netTips()
        }
    }

    override fun initData() {
        initRefresh()
        etSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (NetUtil.checkNet(mContext)) {
                    if (delayRun != null && handler != null) handler.removeCallbacks(delayRun)
                    //??????600ms???????????????????????????????????????????????????run??????
                    if (handler != null && delayRun != null) handler.postDelayed(delayRun, 600)
                }
            }
        })
    }

    override fun initListner() {
        //  ivHome!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        ivBack_!!.setOnClickListener { v: View? -> Utils.goToNextUI(MainActivity::class.java) }
        mAdapter?.let {
            it.setOnItemClickListener { adapter, view, position ->
                val item = adapter.getItem(position) as FenPei

                DialogCheckInfo(this, "????????????", item).show()
                /*    val intent = Intent(this@ActivityAnimalCBPublic, ActivityUnderWriteDeal::class.java)
                    intent.putExtra(Constants.Ba_num, item.number)
                    intent.putExtra(Constants.Type, "from_public")
                    startActivity(intent)*/
            }
        }


        rlPayType!!.setOnClickListener {
            isLoadMore = false
            mIvArrow!!.animate().setDuration(100).rotation(90f).start()//???????????????????????? ???f5 ????????????  ???f7 ????????????  ???f9
            PopuUtils(this).initChoicePop(rlPayType, choice_type) { str: String? ->
                //?????????  ???f1????????????  ???f3 ????????????  ???f5
                mTypeChoice!!.text = str
                mIvArrow!!.animate().setDuration(100).rotation(0f).start()
                getBill(Utils.getEdit(etSearch), str!!, pageIndex)
            }
        }
    }

    private var choice_type: MutableList<String> = mutableListOf("??????", "??????", "????????????")

    companion object {
        private const val PDF_SAVE_START = 1 shl 19
        private const val PDF_SAVE_RESULT = 1 shl 20
    }

    private val delayRun = Runnable {
        isLoadMore = false
        currentIndex = 1
        getBill(Utils.getEdit(etSearch), "7", currentIndex)
    }

    var currentIndex = 1
    private var touBaoBeans: MutableList<FenPei>? = ArrayList()
    var total = 0
    private var isLoadMore = false
    private fun initRefresh() {
        mRefreshLayout!!.setOnLoadMoreListener {
            if (total == 20) {
                isLoadMore = true
                currentIndex += 1
                getBill(Utils.getEdit(etSearch), currnet_status, currentIndex)
                it.finishLoadMore()
            } else {
                isLoadMore = false
                it.finishLoadMoreWithNoMoreData()
            }
        }
    }

    var currnet_status: String = "7"
    private fun getBill(input: String, statu: String, pageIndex: Int) {

        var statu_: String? = null
        var url: String? = null
        //?????????1?????????3????????????5????????????7????????????9????????????
        if (module_name == "?????????") {
            url = Config.GetBaoAn
            statu_ = when (statu) {
                "??????" ->
                    "7"
                "????????????" ->
                    "f7"
                "??????" ->
                    "f5"
                else ->
                    "7"
            }

        } else {
            url = Config.GetLandBaoAn
            statu_ = when (statu) {
                "??????" ->
                    "9"
                "????????????" ->
                    "f9"
                "??????" ->
                    "f7"
                else ->
                    "9"
            }
        }
        currnet_status = statu_
        RequestUtil.getInstance(this)!!.getBill(url, statu_, input, 20, pageIndex, module_name.toString())
        { rtnCode: Int, message: String?, totalNums: Int, datas: JSONArray? ->
            total = totalNums
            if (datas != null && datas.size > 0)
                touBaoBeans = GsonUtil.instant!!.parseCommonUseArr(datas, FenPei::class.java)
            else {
                touBaoBeans!!.clear()
                showStr(message)
            }
            val msg = Message()
            msg.obj = touBaoBeans
            msg.what = 3
            handler.sendMessage(msg)
        }
    }

    var mAdapter: BaseQuickAdapter<FenPei?, BaseViewHolder>? = null
    fun initAdapter() {
        rv!!.adapter = object : BaseQuickAdapter<FenPei?, BaseViewHolder>(R.layout.item_public) {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun convert(holder: BaseViewHolder, item: FenPei?) {

                val tv_first = holder.getView<TextView>(R.id.tv_name)
                val tv_second = holder.getView<TextView>(R.id.tv_insure_sum)
                val tv_third = holder.getView<TextView>(R.id.tv_make_deal_time)
                val tv_fourth = holder.getView<TextView>(R.id.tv_public_time)
                val tv_five = holder.getView<TextView>(R.id.tv_address)
                val check = holder.getView<TextView>(R.id.tv_statu)
                val fStatus = item?.status
                holder.setText(R.id.tv_insure, item!!.fItemDetailList)
                check.text = fStatus
                if (module_name == "?????????") {
                    if (fStatus == "????????????") {
                        check.text = "??????"
                    } else if (fStatus == "??????") {
                        check.text = "????????????"
                    } else {
                        check.text = "????????????"
                    }
                } else {
                    if (fStatus == "??????") {
                        check.text = "??????"
                    } else if (fStatus == "??????") {
                        check.text = "????????????"
                    } else {
                        check.text = "????????????"
                    }
                }

                check.setOnClickListener { v: View? ->
                    if (!NetUtil.checkNet(mContext)) {
                        showStr("???????????????????????????")
                        return@setOnClickListener
                    }
                    /*??????*/
                    if (module_name == "?????????")
                        if (fStatus == "????????????") {
                            RequestUtil.getInstance(this@ActivityAnimalCBPublic)!!.setLiPeiStatus(item.number, "??????", object : GetResult {
                                override fun getResult(result: ResultBean?) {
                                    showStr(result!!.msg)
                                    if (result.code >= 0) {
                                        isLoadMore = false
                                        pageIndex = 1
                                        getBill(Utils.getEdit(etSearch), currnet_status, pageIndex)

                                        getAnimalLiPeiPdf(item.number)
                                    }
                                }
                            })

                        }

                    if (fStatus == "??????")
                        getAnimalLiPeiPdf(item.number)
                }
                tv_first.text = "????????????" + item?.farmerName + " - " + item?.farmerNumber
                tv_second.text = item?.number
                tv_third.text = item?.baoAnDate
                tv_fourth.text = item?.FShowTimeBegin
                tv_five.text = item?.riskAddress
            }
        }.also { mAdapter = it }
    }

    fun getAnimalLiPeiPdf(fNumber: String?) {
        /* RequestUtil.getInstance(mContext)!!.GetAnimalLipeiPDF(
                 fNumber
         ) { data ->

             if (data != null) {
                 handler.sendEmptyMessage(PDF_SAVE_START)
                 fillPdfTemplate(data)
             } else {
                 hideAvi()
                 showStr("????????????")
             }
         }*/
        RequestUtil.getInstance(mContext)!!.GetAnimalLipeiPDF(fNumber, object : GetCommon<AnimalLiPeiPdfBean> {
            override fun getCommon(t: AnimalLiPeiPdfBean) {
                if (t != null) {
                    handler.sendEmptyMessage(PDF_SAVE_START)
                    fillPdfTemplate(t)
                } else {
                    hideAvi()
                    showStr("????????????")
                }
            }
        })

    }

    fun fillPdfTemplate(animalPublicBean: AnimalLiPeiPdfBean) {
        FileUtil.copyAssetAndWrite(this, "???????????????????????????????????????.pdf")
        val absolutePath = File(cacheDir, "???????????????????????????????????????.pdf").absolutePath
        val sb = StringBuilder()
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        sb.append(externalStorageDirectory.absolutePath)
        sb.append(File.separator)
        sb.append("???????????????")
        val sb2 = sb.toString()
        val file = File(sb2)
        if (!file.exists()) {
            file.mkdirs()
        }
        pdf_address = sb2 + File.separator + animalPublicBean.fInsureNumber + "_" + TimeUtil.getTime("yyyyMMddHHmmsss") + ".pdf"
        Thread {
            try {
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
                //  val createFont = BaseFont.createFont(AsianFontMapper.ChineseSimplifiedFont, AsianFontMapper.ChineseSimplifiedEncoding_H, false)

                val fileOutputStream = FileOutputStream(pdf_address)
                val pdfReader = PdfReader(absolutePath)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val pdfStamper = PdfStamper(pdfReader, fileOutputStream)
                val acroFields = pdfStamper.acroFields
                val data = animalPublicBean.data
                val array_table: ArrayList<String?> = ArrayList()
                val array_head_all: ArrayList<String?> = ArrayList<String?>()
                array_table.clear()
                array_head_all.clear()
                val array_sign: ArrayList<String?> = ArrayList()
                array_sign.clear()
                if (data != null && data.isNotEmpty()) {
                    for (dataBean in data) {
                        array_table.add(dataBean.rn)
                        array_table.add(dataBean.fFarmerName ?: "")
                        array_table.add(dataBean.fLabelAddress ?: "")

                        array_table.add(dataBean.fEarNumber ?: "")
                        array_table.add(dataBean.fRiskDate ?: "")
                        array_table.add(dataBean.fReasonName ?: "")
                        array_table.add(dataBean.fAnimalAge ?: "")
                        array_table.add(dataBean.fAnimalWeight ?: "")
                        array_table.add(dataBean.fUnitAmount ?: "")
                        array_table.add(dataBean.fLossAmount ?: "")
                        array_sign.add(dataBean.fSignPicture ?: "")
                    }
                }
                //??????
                array_head_all.add(animalPublicBean.fLabelAddress)

                array_head_all.add(animalPublicBean.fItemName)

                array_head_all.add(animalPublicBean.fInsureNumber)
                array_head_all.add(animalPublicBean.fEmployeeName)
                array_head_all.add(animalPublicBean.fCreateTime)
                //????????????
                array_head_all.addAll(2, array_table)

                //?????????????????????
                val arrayList4: ArrayList<String> = ArrayList<String>()
                arrayList4.clear()
                for (i2 in 1..2) {
                    arrayList4.add("Text$i2")
                }

                val size = array_table.size + 3
                for (i3 in 3 until size) {
                    arrayList4.add("Text$i3")
                }
                for (i4 in 163..165) {
                    //  LogUtils.e("i4  $i4")
                    arrayList4.add("Text$i4")
                }
                val size2 = array_head_all.size
                for (i5 in 0 until size2) {
                    acroFields.setFieldProperty(arrayList4[i5], "textfont", baseFont, null as IntArray?)
                    acroFields.setField(arrayList4[i5], array_head_all[i5])
                }

                for (i5 in 0 until array_sign.size) {
                    val s = array_sign[i5]
                    if (!s.isNullOrBlank()) {
                        try {
                            var overContent = pdfStamper.getOverContent(acroFields.getFieldPositions("Image${i5 + 1}")[0].page)//????????????
                            var idFontImg = Image.getInstance(s)//????????????????????????
                            val signRect: Rectangle = acroFields.getFieldPositions("Image${i5 + 1}")[0].position
                            idFontImg.setAbsolutePosition(signRect.left, signRect.bottom)
                            idFontImg.scaleToFit(signRect)//??????????????????,?????????
                            // idFontImg.rotation =180
                            overContent.addImage(idFontImg)//??????????????????pdf
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
                showStr("????????????")
                e.printStackTrace()
                LogUtils.e("IOException 2  " + e.message, *arrayOfNulls(0))
                hideAvi()
            } catch (e2: BadPdfFormatException) {
                hideAvi()
                showStr("????????????")
                LogUtils.e("BadPdfFormatException 2  " + e2.message, *arrayOfNulls(0))
                e2.printStackTrace()
            } catch (e3: DocumentException) {
                hideAvi()
                showStr("????????????")
                LogUtils.e("DocumentException 2  " + e3.message, *arrayOfNulls(0))
                e3.printStackTrace()
            }
        }.start()
    }

    fun setAdapter(fenPei: MutableList<FenPei?>, isLoadMore: Boolean) {

        if (!isLoadMore)
            if (fenPei == null || fenPei.size == 0) {
                showStr("??????????????????")
                mAdapter!!.setList(null)
                return
            }
        if (isLoadMore) {

            if (fenPei != null && fenPei.isNotEmpty())
                mAdapter!!.addData(fenPei)
        } else
            mAdapter!!.setNewInstance(fenPei)
    }
}