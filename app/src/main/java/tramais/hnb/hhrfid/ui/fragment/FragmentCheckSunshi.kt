package tramais.hnb.hhrfid.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.style.PictureParameterStyle
import com.yanzhenjie.recyclerview.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.CheckDetail
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.bean.InsureLableBean
import tramais.hnb.hhrfid.bean.RiskReason
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.interfaces.ChoicePhoto
import tramais.hnb.hhrfid.interfaces.GetBool
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.interfaces.GetInsureLable
import tramais.hnb.hhrfid.lv.ExpandedAdapter
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivityFeedCheck
import tramais.hnb.hhrfid.ui.CameraOnlyActivity
import tramais.hnb.hhrfid.ui.dialog.DialogDelete
import tramais.hnb.hhrfid.ui.dialog.DialogEarTags
import tramais.hnb.hhrfid.ui.dialog.DialogEditCheck
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.ui.view.RecyleViewForScrollView
import tramais.hnb.hhrfid.util.*
import java.util.*
import kotlin.collections.ArrayList


class FragmentCheckSunshi : BaseFragment(), ChoicePhoto {
    var fenPei: FenPei? = null
    private lateinit var mInsureType: TextView
    private lateinit var mUnitCoverage: TextView
    private lateinit var mHuarmType: TextView
    private lateinit var mLossTotal: EditText
    private lateinit var mComRate: EditText
    private lateinit var mComTotal: EditText
    private lateinit var mComTotalBatch: TextView
    private lateinit var mComRateBatch: TextView
    private lateinit var mRecyleView: RecyleViewForScrollView
    private lateinit var mSave: Button
    private lateinit var mSure: TextView
    private lateinit var mEarTags: TextView
    var ear_tags: MutableList<String> = ArrayList()

    //    private lateinit var mAvi: AVLoadingIndicatorView
    override fun initImmersionBar() {

    }

    fun regist(arr: JSONArray) {
        val loss_total = Utils.getEdit(mLossTotal)
        val harm_type = Utils.getText(mHuarmType)

        RequestUtil.getInstance(context)!!.saveAnimalDingSun(
                fenPei!!.number, reason_code!![harm_type], harm_type, loss_total,
                "", riskAmount.toString(), "", "", companyNum, "", arr) { json ->
            hideAvi()
            json.getString("Msg").showStr()
        }

    }

    var reasons: MutableList<String>? = ArrayList()
    var reason_code: MutableMap<String, String>? = HashMap()
    private var riskReasons: MutableList<RiskReason.DataDTO>? = null
    private val insureType: Unit
        private get() {
            if (riskReasons != null) riskReasons!!.clear()
            if (reasons != null) reasons!!.clear()
            if (reason_code != null) reason_code!!.clear()
            if (NetUtil.checkNet(context)) {
                RequestUtil.getInstance(context)!!.getRiskReason("养殖险事故类型", object : GetCommon<RiskReason> {
                    override fun getCommon(t: RiskReason) {
                        riskReasons = t.data as MutableList<RiskReason.DataDTO>
                        if (riskReasons != null && riskReasons!!.size > 0)
                            for (item in riskReasons!!) {
                                val reasonName = item.reasonName
                                if (!TextUtils.isEmpty(reasonName) && !reasons!!.contains(reasonName))
                                    reasons!!.add(reasonName.toString())
                                reason_code!![reasonName.toString()] = item.reasonCode.toString()
                            }
                    }

                })
            }
        }


    var mAdapter: ExpandedAdapter? = null
    override fun findViewById(view: View?) {
        view?.let {
            mEarTags = it.findViewById(R.id.ear_tags)
            mSure = it.findViewById(R.id.tv_sure)
            mRecyleView = it.findViewById(R.id.recyle_view)
            mComTotalBatch = it.findViewById(R.id.com_total_batch)
            mComRateBatch = it.findViewById(R.id.com_rate_batch)
            mInsureType = it.findViewById(R.id.insure_type)
            mUnitCoverage = it.findViewById(R.id.unit_coverage)
            mHuarmType = it.findViewById(R.id.huarm_type)
            mLossTotal = it.findViewById(R.id.loss_total)
            mComRate = it.findViewById(R.id.com_rate)
            mComTotal = it.findViewById(R.id.com_total)
            mSave = it.findViewById(R.id.save)
            mAdapter = ExpandedAdapter(context)
//            mAvi = it.findViewById(R.id.avi)
            mRecyleView.layoutManager = GridLayoutManager(context, 3)
            //  mRecyleView!!.layoutManager = LinearLayoutManager(context)
            mRecyleView!!.setSwipeMenuCreator(addSwipeMenu())
            mRecyleView!!.setOnItemMenuClickListener(swipeClick())
            mRecyleView?.let {
                it.setOnItemClickListener { view, adapterPosition ->
                    // 根据原position判断该item是否是parent item
                    mAdapter?.let { expandedAdapter ->

                        // 换取parent position


                        val dto_data = expandedAdapter.getData()
                        //  if (parentPosition >= dto_data!!.size) return@let
                        if (expandedAdapter.isParentItem(adapterPosition)) {
                            val parentPosition = expandedAdapter.parentItemPosition(adapterPosition)
                            val liPeiAnimalPicData = dto_data?.get(parentPosition)!!.liPeiAnimalPicData
                            if (liPeiAnimalPicData == null || liPeiAnimalPicData.isNullOrEmpty()) {
                                "暂无照片数据".showStr()
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
                            val parentPosition = expandedAdapter.parentItemPosition(adapterPosition)
                            val childPosition = expandedAdapter.childItemPosition(adapterPosition)
                            val picUrl = expandedAdapter.getData()!![parentPosition]!!.liPeiAnimalPicData!![childPosition].picUrl
                            DialogImg(requireContext(), picUrl).show()
                        }

                    }

                }

            }
            mRecyleView!!.adapter = mAdapter
            getWhiteStyle()

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_check_sunshi
    }

/*    companion object {
        private const val SAVE_START = 1 shl 19
        private const val SAVE_RESULT = 1 shl 20
    }*/

    var dialog_title: MutableList<String> = mutableListOf("损失金额", "损失比例", "耳标号")
    var click_tag: String? = null
    var click_entry_id: String? = null
    var click_position = -1
    var current_pics: MutableList<String> = ArrayList()
    override fun initListener() {
        mEarTags.setOnClickListener {
            if (ear_tags.isNullOrEmpty()) {
                "暂无耳标信息".showStr()
                return@setOnClickListener
            }
            DialogEarTags(requireContext(), ear_tags) { tag ->
                mEarTags.text = tag
                val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
                val filter = mAdapter!!.getData()!!.filter { it!!.fEarNumber!! == tag }
                if (filter == null || filter.isEmpty()) {
                    var movie = CheckDetail.LiPeiAnimalDataDTO()
                    movie.fEarNumber = tag
                    movie.fLossAmount = null
                    movie.fRiskPre = null


                    data_change.add(movie)
                    if (mAdapter != null)
                        mAdapter!!.addData(data_change)
                    mLossTotal.setText(mAdapter!!.getData()?.size.toString() ?: "0")
                } else {
                    "该耳标号已添加".showStr()
                }

            }.show()

        }
        mSure.setOnClickListener {
            val lossTotal = Utils.getEdit(mLossTotal)
            if (lossTotal.isNullOrEmpty()) return@setOnClickListener
            val current_total = mAdapter?.getData()?.size ?: 0
            val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
            var end_index = lossTotal.toInt()
            if (end_index > current_total) {

                for (item in current_total until end_index) {
                    var movie = CheckDetail.LiPeiAnimalDataDTO()
                    movie.fEarNumber = null
                    movie.fLossAmount = null
                    movie.fRiskPre = null


                    data_change.add(movie)
                }

                if (mAdapter != null)
                    mAdapter!!.addData(data_change)
            }
            mLossTotal.setText(mAdapter!!.getData()?.size.toString() ?: "0")
        }


        mComTotalBatch.setOnClickListener {
            val com_total = Utils.getEdit(mComTotal)
            if (com_total.isEmpty()) {
                "请输入损失金额".showStr()
                return@setOnClickListener
            }
            val unitCoverage = Utils.getText(mUnitCoverage)
            if (!unitCoverage.isNullOrBlank()) {
                if (com_total.toDouble() > unitCoverage.toDouble()) {
                    "损失金额不得大于单位保额".showStr()
                    return@setOnClickListener
                }
            }
            if (mAdapter == null) {
                return@setOnClickListener
            }
            mAdapter?.let { adapter ->
                val data = adapter.getData()
                val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
                data_change?.clear()
                for (index in 0 until data!!.size) {
                    val item = data[index]
                    var movie = CheckDetail.LiPeiAnimalDataDTO()
                    item?.let {
                        movie.fEarNumber = it.fEarNumber
                        movie.fLossAmount = com_total
                        movie.fRiskPre = it.fRiskPre
                        movie.fEntryID = item.fEntryID
                        movie.liPeiAnimalPicData = item.liPeiAnimalPicData
                        adapter.upDateData(index, movie)

                    }
                }
                //adapter.setGroupList(data_change)
            }
        }
        mComRateBatch.setOnClickListener {
            if (mAdapter == null) {
                return@setOnClickListener
            }
            val com_rate = Utils.getEdit(mComRate)
            if (com_rate.isEmpty()) {
                "请输入损失比例".showStr()
                return@setOnClickListener
            }


            if (Utils.isNumeric(com_rate)) {
                if (com_rate!!.toDouble() > 100) {
                    "损失比例不得大于100".showStr()
                    return@setOnClickListener
                }
            }
            mAdapter?.let { adapter ->
                val data = adapter.getData()
                val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
                data_change?.clear()
                for (index in 0 until data!!.size) {
                    val item = data[index]
                    var movie = CheckDetail.LiPeiAnimalDataDTO()
                    item?.let {
                        movie.fEarNumber = it.fEarNumber
                        movie.fLossAmount = it.fLossAmount
                        movie.fRiskPre = com_rate
                        movie.fEntryID = item.fEntryID
                        movie.liPeiAnimalPicData = item.liPeiAnimalPicData
                        adapter.upDateData(index, movie)

                    }
                }
            }
        }
        mHuarmType.setOnClickListener {
            if (reasons == null || reasons!!.size == 0) {
                "暂无险种可选".showStr()
                return@setOnClickListener
            }
            PopuChoice(activity, mHuarmType, "请选择证件类型", reasons) { str: String ->
                mHuarmType.text = str
            }
        }
        mSave!!.setOnClickListener {


            val loss_total = Utils.getEdit(mLossTotal)
            if (loss_total.isNullOrEmpty()) {
                "请输入损失数量".showStr()
                return@setOnClickListener
            }
            val harm_type = Utils.getText(mHuarmType)
            if (harm_type.isNullOrEmpty()) {
                "请选择灾害类型".showStr()
                return@setOnClickListener
            }
            val data_quick = mAdapter?.getData()//理赔数据
            if (data_quick == null || data_quick.isEmpty()) {
                "暂无数据上传".showStr()
                return@setOnClickListener
            }

            for (item in data_quick) {
                if (item!!.fLossAmount.isNullOrEmpty() || item.fRiskPre.isNullOrEmpty()) {
                    "损失金额或损失比例不能为空".showStr()

                    break
                }
            }
            showAvi()
            LogUtils.e("time  show" + System.currentTimeMillis())

            Thread {
                y = 0
                upLoadOneByOne(data_quick, y)
            }.start()


        }
    }


    fun swipeClick(): OnItemMenuClickListener {
        return OnItemMenuClickListener { menuBridge, adapterPosition ->
            menuBridge.closeMenu()
            if (mAdapter == null) return@OnItemMenuClickListener
            val parentPosition = mAdapter!!.parentItemPosition(adapterPosition)
            //     val childPosition = mAdapter!!.childItemPosition(adapterPosition)
            when (menuBridge.position) {
                0 -> {
                    mAdapter?.let {
                        val data = it.getData()
                        data?.let {
                            val liPeiAnimalDataDTO = data[parentPosition]
                            val dialogEdit = DialogEditCheck(requireContext(), "修改参数", parentPosition, dialog_title, liPeiAnimalDataDTO) { map ->
                                var movie = CheckDetail.LiPeiAnimalDataDTO()

                                movie.fEntryID = data[parentPosition]?.fEntryID
                                if (map[dialog_title[0]] == "0.0" || map[dialog_title[0]].isNullOrEmpty()) {
                                    movie.fLossAmount = data[parentPosition]?.fLossAmount
                                } else {
                                    val s = map[dialog_title[0]]
                                    if (Utils.isNumeric(s)) {
                                        if (s!!.toDouble() > Utils.getText(mUnitCoverage).toDouble()) {
                                            "损失金额不得大于单位保额".showStr()
                                            return@DialogEditCheck
                                        }
                                    }
                                    movie.fLossAmount = map[dialog_title[0]]
                                }

                                if (map[dialog_title[1]] == "0.0" || map[dialog_title[1]].isNullOrEmpty())
                                    movie.fRiskPre = data[parentPosition]?.fRiskPre
                                else {
                                    val s = map[dialog_title[1]]
                                    if (Utils.isNumeric(s)) {
                                        if (s!!.toDouble() > 100) {
                                            "损失比例不得大于100".showStr()
                                            return@DialogEditCheck
                                        }
                                    }
                                    movie.fRiskPre = map[dialog_title[1]]
                                }

                                if (map[dialog_title[2]] == "0.0" || map[dialog_title[2]].isNullOrEmpty())
                                    movie.fEarNumber = data[adapterPosition]?.fEarNumber
                                else movie.fEarNumber = map[dialog_title[2]]
                                movie.liPeiAnimalPicData = liPeiAnimalDataDTO!!.liPeiAnimalPicData
                                mAdapter!!.upDateData(parentPosition, movie)
                            }
                            if (dialogEdit.isShowing) dialogEdit.dismiss()
                            if (!dialogEdit.isShowing) dialogEdit.show()
                        }
                    }
                }
                1 -> {

                    DialogDelete(requireContext(), "序号${parentPosition + 1}", object : GetBool {
                        override fun getBool(tf: Boolean) {
                            if (tf) {
                                if (mAdapter != null) {
                                    mAdapter!!.removeAt(parentPosition)
                                    mLossTotal.setText(mAdapter!!.getData()?.size.toString() ?: "0")
                                }
                            }
                        }
                    }).show()


                }
                2 -> {
                    current_pics.clear()

                    val liPeiAnimalDataDTO = mAdapter?.getData()?.get(parentPosition)
                    click_position = parentPosition
                    liPeiAnimalDataDTO?.let {

                        val liPeiAnimalPicData = it.liPeiAnimalPicData
                        liPeiAnimalPicData?.let { pic ->
                            for (item in pic) {
                                val picUrl = item.picUrl
                                if (picUrl!!.isNotEmpty() && !current_pics.contains(picUrl))
                                    current_pics.add(picUrl.toString())
                            }

                        }
                    }


                    PopuChoicePicture(activity, this).showAtLocation(view, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
                }
            }

        }
    }

    fun addSwipeMenu(): SwipeMenuCreator {
        return SwipeMenuCreator { leftMenu, rightMenu, position ->
            val width = resources.getDimensionPixelSize(R.dimen.size_60)

            val height = ViewGroup.LayoutParams.MATCH_PARENT

            var editItem = SwipeMenuItem(context)
            editItem.text = "编辑"
            editItem.setTextColorResource(R.color.white)
            editItem.setBackgroundColorResource(R.color.f08c792)
            editItem.width = width
            editItem.height = height
            rightMenu.addMenuItem(editItem)

            var deleteItem = SwipeMenuItem(context)
            deleteItem.text = "删除"
            deleteItem.setTextColorResource(R.color.white)
            deleteItem.setBackgroundColorResource(R.color.fb588f7)
            deleteItem.width = width
            deleteItem.height = height
            rightMenu.addMenuItem(deleteItem)


            var photoItem = SwipeMenuItem(context)
            photoItem.text = "拍照"
            photoItem.setTextColor(Color.WHITE)
            photoItem.setBackgroundColorResource(R.color.f1de5e2)
            photoItem.width = width
            photoItem.height = height

            rightMenu.addMenuItem(photoItem)

        }

    }

    var y = 0

    fun upLoadOneByOne(dto_all: MutableList<CheckDetail.LiPeiAnimalDataDTO?>?, index: Int) {
        var img_dto: MutableList<CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO>? = ArrayList()

        //照片
        val data_pics: MutableList<String?> = ArrayList()
        dto_all?.let {
            if (index > dto_all.size - 1) return@let
            val item = dto_all[index]
            item?.let { dto ->
                var dto_ = CheckDetail.LiPeiAnimalDataDTO()

                val liPeiAnimalPicData1 = dto.liPeiAnimalPicData
                if (liPeiAnimalPicData1 != null && liPeiAnimalPicData1.isNotEmpty()) {

                    data_pics.clear()
                    for (item_pic in liPeiAnimalPicData1) {
                        val picUrl = item_pic.picUrl
                        if (!data_pics.contains(picUrl)) {
                            data_pics.add(picUrl)

                        }
                    }
                    UpLoadFileUtil.upLoadFile(context, "查勘", index.toString(), data_pics) { list ->
                        //照片数据
                        for (pic in list) {
                            var img_dto_ = CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO()
                            if (pic!!.startsWith("http"))
                                img_dto_.picUrl = pic
                            else img_dto_.picUrl = Config.PHOTO_URL + pic
                            img_dto?.add(img_dto_)
                        }
                        dto.liPeiAnimalPicData = img_dto
                        mAdapter!!.upDateData(index, dto)
                        if (index == dto_all.size - 1) {
                            y = 0
                            val toJsonString = toJsonString(mAdapter!!.getData())
//                            val parseArray = JSON.parseArray(JSON.toJSONString(mAdapter!!.getData()))
                            if (toJsonString != null) {
                                regist(toJsonString)
//                                LogUtils.e("toJsonString  $toJsonString   ")
                            } else {
                                hideAvi()
                            }
                        } else {
                            y += 1
                            upLoadOneByOne(dto_all, y)
                        }
                    }
                } else {
                    dto_.liPeiAnimalPicData = null
                    mAdapter!!.upDateData(index, dto)
                    if (index == dto_all.size - 1) {
                        y = 0
                        val toJsonString = toJsonString(mAdapter!!.getData())
                        if (toJsonString != null) {
                            regist(toJsonString)
                        } else {
                            hideAvi()
                        }
                    } else {
                        y += 1
                        upLoadOneByOne(dto_all, y)
                    }

                }
            }

        }
    }

    var riskAmount = 0.0
    fun toJsonString(dots: MutableList<CheckDetail.LiPeiAnimalDataDTO?>?): JSONArray? {
        if (dots!!.isEmpty() || dots == null) return null
        var dto_arr = JSONArray()
        riskAmount = 0.0
        for (item in dots) {
            var pic_arr = JSONArray()
            var dot_json = JSONObject()
            val liPeiAnimalPicData = item!!.liPeiAnimalPicData
            liPeiAnimalPicData?.let {
                pic_arr.clear()
                for (item_ in it) {
                    val picUrl = item_.picUrl
                    if (!picUrl.isNullOrBlank()) {
                        var pic_json = JSONObject()
                        pic_json["PicUrl"] = picUrl
                        pic_arr.add(pic_json)
                    }

                }
            }
            var lossAmout = if (!item.fLossAmount.isNullOrBlank()) {
                item.fLossAmount!!.toDouble()
            } else {
                0.0
            }
            riskAmount += lossAmout
            dot_json["FEarNumber"] = item.fEarNumber ?: " "
            dot_json["FLossAmount"] = lossAmout.toString()
            dot_json["FEntryID"] = item.fEntryID ?: " "
            dot_json["FRiskPre"] = item.fRiskPre ?: " "
            dot_json["LiPeiAnimalPicData"] = pic_arr
            dto_arr.add(dot_json)
        }
        return dto_arr
    }

    override fun initData() {

        fenPei = ActivityFeedCheck.fenPei
        fenPei?.let {
            mUnitCoverage.text = it.fUnitAmount
            mInsureType.text = it.fItemDetailList
        }

        getDetail(fenPei!!.number)
        getTags(fenPei!!.insureNumber)
        insureType
    }


    fun boo2Int(isTrue: Boolean): Int {
        return if (isTrue)
            1
        else 0
    }

    var quck_list: MutableList<CheckDetail.LiPeiAnimalDataDTO?>? = ArrayList()
    fun getDetail(num: String?) {
        if (NetUtil.checkNet(context)) {
            RequestUtil.getInstance(context)!!.getChaKanDetail(num) { detail ->
                if (detail.code == 0) {
                    detail?.let {
                        quck_list?.clear()
                        val liPeiAnimalData = detail.liPeiAnimalData
                        liPeiAnimalData?.let { it1 -> quck_list?.addAll(it1) }
                        mAdapter!!.setGroupList(quck_list)
                        // mAdapter!!.notifyDataSetChanged()
                        requireActivity().runOnUiThread {
                            mHuarmType.text = it.fReasonName
                            mLossTotal.setText(quck_list?.size.toString() ?: "0")
                            mComTotal.setText(it.fRiskAmount)
                            mComRate.setText(it.fRiskPre)
                        }
                    }
                }
            }
        }
    }

    fun getTags(num: String?) {
        RequestUtil.getInstance(context)!!.GetInsureLabels(num, "", object : GetInsureLable {
            override fun insureLable(info: InsureLableBean?) {
                info?.let {
                    val data = it.data
                    data.let { lable ->
                        for (item in lable!!) {
                            val fLabelNumber = item.fLabelNumber
                            if (!fLabelNumber.isNullOrBlank() && !ear_tags.contains(fLabelNumber)) ear_tags.add(fLabelNumber)
                        }
                    }
                }
            }
        })
    }

    override fun actionCamera() {
        var intent = Intent(context, CameraOnlyActivity::class.java)
        var fenPei_ = FenPei()
        fenPei_.farmerName = fenPei!!.farmerName + "(" + "序号:${click_position + 1}" + ")"
        intent.putExtra("fenpei", fenPei_)
        intent.putExtra("photo_num", current_pics.size)
        startActivityForResult(intent, 124)
    }

    var fileUrlLists: MutableList<String>? = ArrayList()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 124 && resultCode == Activity.RESULT_OK) {

            val stringArrayListExtra = data!!.getStringArrayListExtra("imgs")
            if (stringArrayListExtra != null) {
                current_pics!!.addAll(stringArrayListExtra)
            }
            upDataPicAdapter(current_pics)
        }
    }

    override fun actionAlbum() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.MULTIPLE)
                .setPictureStyle(mPictureParameterStyle)
                .theme(R.style.picture_default_style)
                .maxSelectNum(9 - current_pics.size)
                .isCompress(true)
                .cutOutQuality(20)// 裁剪压缩质量 默认90 int
                .minimumCompressSize(90)// 小于100kb的图片不压缩
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onResult(result: List<LocalMedia>) {
                        if (result.isNullOrEmpty()) return
                        for (item_pic in result) {
                            current_pics.add(item_pic.compressPath)
                        }
                        upDataPicAdapter(current_pics)

                    }

                    override fun onCancel() {}
                })
    }

    fun upDataPicAdapter(result: List<String>) {
        if (click_position >= 0) {
            if (mAdapter == null) return
            val eratag = mAdapter!!.getData()?.get(click_position)
            eratag?.let { dto ->
                val liPeiAnimalPicData: MutableList<CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO> = ArrayList()
                liPeiAnimalPicData.clear()
                /*  for (item in result) {
                      var dto = CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO()
                      dto.picUrl = item.realPath
                      liPeiAnimalPicData.add(dto)
                  }*/
                if (result != null && result.isNotEmpty())
                    for (item_current in result) {
                        var dto = CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO()
                        dto.picUrl = item_current
                        liPeiAnimalPicData.add(dto)
                    }

                dto.liPeiAnimalPicData = liPeiAnimalPicData
                mAdapter!!.upDateData(click_position, eratag)
            }

        }
    }

    private var mPictureParameterStyle: PictureParameterStyle? = null
    private fun getWhiteStyle() {
        // 相册主题
        val requireContext = requireContext()
        mPictureParameterStyle = PictureParameterStyle()
        mPictureParameterStyle?.let { // 是否改变状态栏字体颜色(黑白切换)
            it.isChangeStatusBarFontColor = true
            // 是否开启右下角已完成(0/9)风格
            it.isOpenCompletedNumStyle = false
            // 是否开启类似QQ相册带数字选择风格
            it.isOpenCheckNumStyle = false
            // 相册状态栏背景色
            it.pictureStatusBarColor = ContextCompat.getColor(requireContext, R.color.white)
            // 相册列表标题栏背景色
            it.pictureTitleBarBackgroundColor = ContextCompat.getColor(requireContext, R.color.white)
            // 相册列表标题栏右侧上拉箭头
            it.pictureTitleUpResId = R.drawable.picture_icon_orange_arrow_up
            // 相册列表标题栏右侧下拉箭头
            it.pictureTitleDownResId = R.drawable.picture_icon_orange_arrow_down
            // 相册文件夹列表选中圆点
            it.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval
            // 相册返回箭头
            it.pictureLeftBackIcon = R.drawable.picture_icon_back_arrow
            // 标题栏字体颜色
            it.pictureTitleTextColor = ContextCompat.getColor(requireContext, R.color.black)
            // 相册右侧取消按钮字体颜色  废弃 改用.pictureRightDefaultTextColor和.pictureRightDefaultTextColor
            it.pictureRightDefaultTextColor = ContextCompat.getColor(requireContext, R.color.black)
            // 相册列表勾选图片样式
            it.pictureCheckedStyle = R.drawable.picture_checkbox_selector
            // 相册列表底部背景色
            it.pictureBottomBgColor = ContextCompat.getColor(requireContext, R.color.picture_color_fa)
            // 已选数量圆点背景样式
            it.pictureCheckNumBgStyle = R.drawable.picture_num_oval
            // 相册列表底下预览文字色值(预览按钮可点击时的色值)
            it.picturePreviewTextColor = ContextCompat.getColor(requireContext(), R.color.picture_color_fa632d)
            // 相册列表底下不可预览文字色值(预览按钮不可点击时的色值)
            it.pictureUnPreviewTextColor = ContextCompat.getColor(requireContext, R.color.picture_color_9b)
            // 相册列表已完成色值(已完成 可点击色值)
            it.pictureCompleteTextColor = ContextCompat.getColor(requireContext, R.color.picture_color_fa632d)
            // 相册列表未完成色值(请选择 不可点击色值)
            it.pictureUnCompleteTextColor = ContextCompat.getColor(requireContext, R.color.picture_color_9b)
            // 预览界面底部背景色
            // it.picturePreviewBottomBgColor = ContextCompat.getColor(requireContext, R.color.picture_color_white)
            // 原图按钮勾选样式  需设置.isOriginalImageControl(true); 才有效
            it.pictureOriginalControlStyle = R.drawable.picture_original_checkbox
            // 原图文字颜色 需设置.isOriginalImageControl(true); 才有效
            it.pictureOriginalFontColor = ContextCompat.getColor(requireContext, R.color.white)
            // 外部预览界面删除按钮样式
            it.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_black_delete
            // 外部预览界面是否显示删除按钮
            it.pictureExternalPreviewGonePreviewDelete = true
        }
    }
}