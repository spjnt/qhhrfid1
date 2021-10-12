package tramais.hnb.hhrfid.ui.crop

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.bean.ChaKanLandsBean
import tramais.hnb.hhrfid.bean.ResultBean
import tramais.hnb.hhrfid.bean.SunShiListBean
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetChaKanLandsBean
import tramais.hnb.hhrfid.interfaces.GetMutableMap
import tramais.hnb.hhrfid.interfaces.GetResult
import tramais.hnb.hhrfid.lv.ExpandedCropAdapter
import tramais.hnb.hhrfid.net.RequestUtil.Companion.getInstance
import tramais.hnb.hhrfid.ui.dialog.DialogEditSunShi
import tramais.hnb.hhrfid.ui.view.RecyleViewForScrollView
import tramais.hnb.hhrfid.util.Utils
import tramais.hnb.hhrfid.util.Utils.getEdit
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class CheckSunShiList : BaseActivity() {
    private var mEtShouzai: EditText? = null
    private var mEtShouzaiMore: TextView? = null

    private var mEtSunShi: EditText? = null
    private var mEtSunShiMore: TextView? = null
    private var mLv: RecyleViewForScrollView? = null
    private var btn_save: Button? = null
    private var ba_num: String? = null
    private var fid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sun_shi_list)
    }

    override fun initView() {
        mEtShouzaiMore = findViewById(R.id.tv_shouzai_more)
        mEtShouzai = findViewById(R.id.et_shouzai)
        mEtSunShiMore = findViewById(R.id.tv_sunshi_more)
        mEtSunShi = findViewById(R.id.et_sun_shi)

        mLv = findViewById(R.id.recyle_view)
        btn_save = findViewById(R.id.save)
        setTitleText("损失程度")
        mAdapter = ExpandedCropAdapter(this)
        mLv!!.layoutManager = LinearLayoutManager(this)

        mLv?.let {
            it.setOnItemClickListener { view, adapterPosition ->
                // 根据原position判断该item是否是parent item
                mAdapter?.let { expandedAdapter ->

                    // 换取parent position


                    val dto_data = expandedAdapter.getData()
                    //  if (parentPosition >= dto_data!!.size) return@let
                    if (expandedAdapter.isParentItem(adapterPosition)) {
                        val parentPosition = expandedAdapter.parentItemPosition(adapterPosition)
                        val liPeiAnimalPicData = dto_data?.get(parentPosition)!!.data2
                        if (liPeiAnimalPicData == null || liPeiAnimalPicData.isNullOrEmpty()) {
                            showStr("暂无数据")
                            return@let
                        }
                        // 换取parent position
                        //   val parentPosition = expandedAdapter.parentItemPosition(adapterPosition)

                        // 判断parent是否打开了二级菜单
                        if (expandedAdapter.isExpanded(parentPosition)) {
                            if (dto_data != null) {
                                dto_data!![parentPosition]!!.isExpanded = false
                                expandedAdapter.notifyParentChanged(parentPosition)

                                // 关闭该parent下的二级菜单
                                expandedAdapter.collapseParent(parentPosition)
                            }

                        } else {
                            if (dto_data != null) {
                                dto_data!![parentPosition]!!.isExpanded = true
                                expandedAdapter.notifyParentChanged(parentPosition)
                                expandedAdapter.expandParent(parentPosition)

                            }
                        }
                    } else {
                        // 换取parent position
                        val parentPosition = mAdapter!!.parentItemPosition(adapterPosition)
                        // 换取child position
                        val childPosition = mAdapter!!.childItemPosition(adapterPosition)
                        val data = dto_data?.get(parentPosition)!!.data2?.get(childPosition)
                        var titles: MutableList<String> = mutableListOf("承保面积", "受灾面积", "损失面积")
                        val dialogEditSunShi = data?.let { it1 ->
                            DialogEditSunShi(this, titles, it1, object : GetMutableMap {
                                override fun getList(values: MutableMap<String, String>) {
                                    if (values != null && values.isNotEmpty()) {
                                        data!!.fSquare = values[titles[0]]
                                        data.fLossQty =values[titles[2]]
                                        data.fRiskQty =values[titles[1]]

                                        expandedAdapter.upDateData(parentPosition, childPosition, data)

                                    }
                                }

                            })
                            /*   DialogEditSunShi(this, titles, it1) { values: MutableMap<String,String> ->
                                   if (values != null && values.size > 0) {
                                       data!!.fSquare = values[0]
                                       data.fLossQty = values[1]
                                       data.fRiskQty = values[2]

                                       expandedAdapter.upDateData(parentPosition, childPosition, data)

                                   }
                               }*/
                        }
                        if (!dialogEditSunShi!!.isShowing) dialogEditSunShi.show()
                    }
                }

            }

        }
        mLv!!.adapter = mAdapter
    }

    var mAdapter: ExpandedCropAdapter? = null
    var maxFid: String? = null
    override fun initData() {
        fid = intent.getStringExtra(Constants.fid)
        ba_num = intent.getStringExtra(Constants.Ba_num)
        maxFid = intent.getStringExtra(Constants.MaxFid)
        getInstance(this)!!.getChaKanLands(ba_num, fid, object : GetChaKanLandsBean {
            override fun getLands(bean: ChaKanLandsBean?) {
                mAdapter!!.setGroupList(bean!!.data1 as MutableList<ChaKanLandsBean.Data1DTO?>)
            }
        })

    }

    override fun initListner() {
        mEtShouzaiMore!!.setOnClickListener {

            val edit = getEdit(mEtShouzai)
            if (TextUtils.isEmpty(edit)) {
                showStr("请输入受灾比例")
                return@setOnClickListener
            }
            if (edit.toDouble() > 100) {
                showStr("受灾比例不得大于100")
                return@setOnClickListener
            }
            upDataPer(edit, "")
        }
        mEtSunShiMore!!.setOnClickListener { v: View? ->

            val edit = getEdit(mEtSunShi)
            if (TextUtils.isEmpty(edit)) {
                showStr("请输入损失比例")
                return@setOnClickListener
            }
            if (edit.toDouble() > 100) {
                showStr("损失比例不得大于100")
                return@setOnClickListener
            }
            upDataPer("", edit)

        }
        btn_save!!.setOnClickListener { v: View? ->
            if (mAdapter == null) {
                return@setOnClickListener
            }
            val data = mAdapter!!.getData()
            if (data == null || data.isEmpty()) {
                return@setOnClickListener

            }
            getInstance(this)!!.SaveChanKanLand(ba_num, maxFid, fid, getData(data), object : GetResult {
                override fun getResult(result: ResultBean?) {
                    showStr(result!!.msg)
                    val intent = Intent()
                    intent.putExtra(Constants.risk, formatDouble(total_risk))
                    intent.putExtra(Constants.loss,  formatDouble(total_loss))
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
        }
    }

    var total_loss = 0.0
    var total_risk = 0.0
    private fun getData(datasFinal: MutableList<ChaKanLandsBean.Data1DTO?>?): String {
        val buffer = StringBuffer()
        total_loss = 0.0
        total_risk = 0.0
        for (item in datasFinal!!) {
            for (item_dto in item!!.data2!!) {
                if (item_dto.fIsChecked) {
                    total_loss += if (item_dto.fLossQty.isNullOrEmpty()) {
                        0.0
                    } else {
                        item_dto!!.fLossQty!!.toDouble()
                    }
                    total_risk += if (item_dto.fRiskQty.isNullOrEmpty()) {
                        0.0
                    } else {
                        item_dto!!.fRiskQty!!.toDouble()
                    }
                }

                buffer.append(item!!.fNumber + "|")
                buffer.append(item!!.fName + "|")
                buffer.append(item_dto.fLandNumber + "|")
                buffer.append(item_dto.fLandName + "|")
                buffer.append(item_dto.fSquare + "|")
                buffer.append(item_dto.fRiskQty + "|")
                buffer.append(item_dto.fLossQty + "|")
                buffer.append(item_dto.fIsChecked)
                buffer.append("|~")

            }

        }
        return buffer.toString()
    }

    /*
    * risk 受灾
    * loss 损失
    * */
    fun upDataPer(risk: String?, loss: String?) {
        if (mAdapter == null) return
        val data = mAdapter!!.getData()
        var bean_list: MutableList<ChaKanLandsBean.Data1DTO?> = ArrayList()
        bean_list.clear()
        for (item in data!!) {
            var bean = ChaKanLandsBean.Data1DTO()
            item?.let {
                bean.fNumber = item.fNumber
                bean.fName = item.fName
                bean.fInsureNumber = item.fInsureNumber
                var total_squre = 0.0
                var total_risk = 0.0
                var total_loss = 0.0
                var bean_dto_list: MutableList<ChaKanLandsBean.Data1DTO.Data2DTO> = ArrayList()
                bean_dto_list.clear()
                for (item_dto in item.data2!!) {
                    var bean_dto = ChaKanLandsBean.Data1DTO.Data2DTO()
                    val fSquare = item_dto.fSquare
                    bean_dto.fSquare = fSquare
                    /*承保面积*/
                    total_squre += if (fSquare.isNullOrEmpty()) {
                        0.0
                    } else {
                        if (Utils.isNumeric(fSquare))
                            fSquare.toDouble()
                        else 0.0
                    }
                    /*根据损失比例  计算损失面积*/
                    if (!loss.isNullOrEmpty()) {
                        bean_dto.fLossQty = if (fSquare.isNullOrEmpty()) {
                            "0.0"
                        } else {
                            val d = loss!!.toDouble() / 100
                            formatDouble(fSquare.toDouble() * d)

                        }
                    } else {
                        bean_dto.fLossQty = item_dto.fLossQty ?: "0.0"
                    }
                    /*损失总面积*/
                    total_loss += if (bean_dto.fLossQty.isNullOrEmpty()) {
                        0.0
                    } else {
                        bean_dto.fLossQty!!.toDouble()
                    }
                    /*根据受灾比例受灾面积*/
                    if (!risk.isNullOrEmpty()) {
                        bean_dto.fRiskQty = if (fSquare.isNullOrEmpty()) {
                            "0.0"
                        } else {
                            val d = risk!!.toDouble() / 100
                            formatDouble(fSquare.toDouble() * d)

                        }
                    } else {
                        bean_dto.fRiskQty = item_dto.fRiskQty ?: "0.0"
                    }

                    /*受灾总面积*/
                    total_risk += if (bean_dto.fRiskQty.isNullOrEmpty()) {
                        0.0
                    } else {
                        bean_dto.fRiskQty!!.toDouble()
                    }
                    bean_dto.fIsChecked = item_dto.fIsChecked
                    bean_dto.fLandName = item_dto.fLandName
                    bean_dto.fGISPicture = item_dto.fGISPicture
                    bean_dto.fLandNumber = item_dto.fLandNumber
                    bean_dto_list.add(bean_dto)
                }

                bean.data2 = bean_dto_list
                bean.fFarmerQuantity = formatDouble(total_squre)
                bean.fFarmerLossQty = formatDouble(total_loss)
                bean.fFarmerRiskQty = formatDouble(total_risk)
                bean_list.add(bean)

            }

        }
        mAdapter!!.setGroupList(bean_list)
    }

    fun formatDouble(d: Double): String {
        val df = DecimalFormat("#####0.00")
        return df.format(d)
    }
}