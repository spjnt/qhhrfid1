package tramais.hnb.hhrfid.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.apkfuns.logutils.LogUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.yanzhenjie.recyclerview.OnItemMenuClickListener
import com.yanzhenjie.recyclerview.SwipeMenuCreator
import com.yanzhenjie.recyclerview.SwipeMenuItem
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.CheckDetail
import tramais.hnb.hhrfid.bean.FenPei
import tramais.hnb.hhrfid.bean.InsureLableBean
import tramais.hnb.hhrfid.bean.RiskReason
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.interfaces.ChoicePhoto
import tramais.hnb.hhrfid.interfaces.GetBool
import tramais.hnb.hhrfid.interfaces.GetCommonWithError
import tramais.hnb.hhrfid.interfaces.GetInsureLable
import tramais.hnb.hhrfid.lv.ExpandedAdapter
import tramais.hnb.hhrfid.net.RequestUtil
import tramais.hnb.hhrfid.ui.ActivityFeedCheck
import tramais.hnb.hhrfid.ui.CameraOnlyActivity
import tramais.hnb.hhrfid.ui.MainActivity
import tramais.hnb.hhrfid.ui.dialog.DialogDelete
import tramais.hnb.hhrfid.ui.dialog.DialogEarTags
import tramais.hnb.hhrfid.ui.dialog.DialogEditCheck
import tramais.hnb.hhrfid.ui.dialog.DialogImg
import tramais.hnb.hhrfid.ui.popu.PopuChoice
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.ui.view.RecyleViewForScrollView
import tramais.hnb.hhrfid.util.GlideEngine
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.UpLoadFileUtil
import tramais.hnb.hhrfid.util.Utils


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
    private lateinit var mEarTags: EditText
    private lateinit var mBtnAddChoice: TextView
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
            val integer = json.getInteger("Code")
            if (integer == 1) {
                Utils.goToNextUI(MainActivity::class.java)
            }
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
            if (NetUtil.checkNet(requireContext())) {
                RequestUtil.getInstance(context)!!.getRiskReason("?????????????????????", object : GetCommonWithError<RiskReason> {
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

                    override fun getError() {

                    }

                })
            }
        }


    var mAdapter: ExpandedAdapter? = null
    override fun findViewById(view: View?) {
        view?.let {
            mBtnAddChoice = it.findViewById(R.id.tv_add_choice)
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
                    // ?????????position?????????item?????????parent item
                    mAdapter?.let { expandedAdapter ->

                        // ??????parent position


                        val dto_data = expandedAdapter.getData()
                        //  if (parentPosition >= dto_data!!.size) return@let
                        if (expandedAdapter.isParentItem(adapterPosition)) {
                            val parentPosition = expandedAdapter.parentItemPosition(adapterPosition)
                            val liPeiAnimalPicData = dto_data?.get(parentPosition)!!.liPeiAnimalPicData
                            if (liPeiAnimalPicData == null || liPeiAnimalPicData.isNullOrEmpty()) {
                                "??????????????????".showStr()
                                return@let
                            }
                            // ??????parent position
                            //   val parentPosition = expandedAdapter.parentItemPosition(adapterPosition)

                            // ??????parent???????????????????????????
                            if (expandedAdapter.isExpanded(parentPosition)) {
                                if (dto_data != null) {
                                    dto_data!![parentPosition]!!.isExpanded = false
                                    expandedAdapter.notifyParentChanged(parentPosition)

                                    // ?????????parent??????????????????
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
                it.setOnItemLongClickListener { view, adapterPosition ->
                    mAdapter?.let { ad ->
                        val data = ad.getData()
                        if (!ad.isParentItem(adapterPosition)) {
                            val par_ = ad.parentItemPosition(adapterPosition)
                            val childe_ = ad.childItemPosition(adapterPosition)
                            val get = data?.get(par_)?.liPeiAnimalPicData
                            get!!.removeAt(childe_)
                            ad.notifyDataSetChanged()
                        }
                    }
                }

            }
            mRecyleView!!.adapter = mAdapter
            //getWhiteStyle()

        }
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_check_sunshi
    }

    /*    companion object {
            private const val SAVE_START = 1 shl 19
            private const val SAVE_RESULT = 1 shl 20
        }*/
    fun addTagToAdapter(tag: String?) {

        val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
        val filter = mAdapter!!.getData()!!.filter { it!!.fEarNumber!! == tag }
        if (filter.isEmpty()) {
            val movie = CheckDetail.LiPeiAnimalDataDTO()
            movie.fEarNumber = tag
            movie.fLossAmount = null
            movie.fRiskPre = null
            data_change.add(movie)
            if (mAdapter != null)
                requireActivity().runOnUiThread { mAdapter!!.addData(data_change) }
            mLossTotal.setText(mAdapter!!.getData()?.size.toString() ?: "0")
        } else {
            "?????????????????????".showStr()
        }
    }

    var dialog_title: MutableList<String> = mutableListOf("????????????", "????????????", "?????????")
    var click_tag: String? = null
    var click_entry_id: String? = null
    var click_position = -1
    var current_pics: MutableList<String> = ArrayList()
    override fun initListener() {
        mEarTags.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    mBtnAddChoice.text = "????????????"
                } else {
                    mBtnAddChoice.text = "????????????"
                }
            }
        })
        mBtnAddChoice.setOnClickListener {
            val btn = mBtnAddChoice.text.toString()
            if (btn == "????????????") {
                val tag = mEarTags.text.toString()
                if (tag.isEmpty()) {
                    "??????????????????".showStr()
                    return@setOnClickListener
                }
                addTagToAdapter(tag)
            } else if (btn == "????????????") {
                if (ear_tags.isNullOrEmpty()) {
                    "??????????????????".showStr()
                    return@setOnClickListener
                }

                DialogEarTags(requireContext(), ear_tags) { tag ->
                    mEarTags.setText(tag)
                    addTagToAdapter(tag)

                }.show()
            }
        }

        mSure.setOnClickListener {
            val lossTotal = Utils.getEdit(mLossTotal)
            if (lossTotal.isEmpty()) return@setOnClickListener
            val current_total = mAdapter?.getData()?.size ?: 0
            val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
            val end_index = lossTotal.toInt()
            if (end_index > current_total) {

                for (item in current_total until end_index) {
                    val movie = CheckDetail.LiPeiAnimalDataDTO()
                    movie.fEarNumber = null
                    movie.fLossAmount = null
                    movie.fRiskPre = null
                    data_change.add(movie)
                }

                if (mAdapter != null)
                    requireActivity().runOnUiThread {
                        mAdapter!!.addData(data_change)
                    }
            }
            mLossTotal.setText(mAdapter!!.getData()?.size.toString() ?: "0")
        }


        mComTotalBatch.setOnClickListener {
            val com_total = Utils.getEdit(mComTotal)
            if (com_total.isEmpty()) {
                "?????????????????????".showStr()
                return@setOnClickListener
            }
            val unitCoverage = Utils.getText(mUnitCoverage)
            if (unitCoverage.isNotBlank()) {
                if (com_total.toDouble() > unitCoverage.toDouble()) {
                    "????????????????????????????????????".showStr()
                    return@setOnClickListener
                }
            }
            if (mAdapter == null) {
                return@setOnClickListener
            }
            mAdapter?.let { adapter ->
                val data = adapter.getData()
                val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
                data_change.clear()
                for (index in 0 until data!!.size) {
                    val item = data[index]
                    val movie = CheckDetail.LiPeiAnimalDataDTO()
                    item?.let {
                        movie.fEarNumber = it.fEarNumber
                        movie.fLossAmount = com_total
                        movie.fRiskPre = it.fRiskPre
                        movie.fEntryID = item.fEntryID
                        movie.liPeiAnimalPicData = item.liPeiAnimalPicData
                        requireActivity().runOnUiThread { adapter.upDateData(index, movie) }
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
                "?????????????????????".showStr()
                return@setOnClickListener
            }
            if (Utils.isNumeric(com_rate)) {
                if (com_rate.toDouble() > 100) {
                    "????????????????????????100".showStr()
                    return@setOnClickListener
                }
            }
            mAdapter?.let { adapter ->
                val data = adapter.getData()
                val data_change: MutableList<CheckDetail.LiPeiAnimalDataDTO?> = ArrayList()
                data_change.clear()
                for (index in 0 until data!!.size) {
                    val item = data[index]
                    val movie = CheckDetail.LiPeiAnimalDataDTO()
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
                "??????????????????".showStr()
                return@setOnClickListener
            }
            PopuChoice(activity, mHuarmType, "?????????????????????", reasons) { str: String ->
                mHuarmType.text = str
            }
        }
        mSave.setOnClickListener {
            val loss_total = Utils.getEdit(mLossTotal)
            if (loss_total.isEmpty()) {
                "?????????????????????".showStr()
                return@setOnClickListener
            }
            val harm_type = Utils.getText(mHuarmType)
            if (harm_type.isEmpty()) {
                "?????????????????????".showStr()
                return@setOnClickListener
            }
            val data_quick = mAdapter?.getData()//????????????
            if (data_quick == null || data_quick.isEmpty()) {
                "??????????????????".showStr()
                return@setOnClickListener
            }
            for (item in data_quick) {
                if (item!!.fLossAmount.isNullOrEmpty() || item.fRiskPre.isNullOrEmpty()) {
                    "???????????????????????????????????????".showStr()
                    return@setOnClickListener
                }
            }
            showAvi()
            //  LogUtils.e("time  show" + System.currentTimeMillis())

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
                            val dialogEdit = DialogEditCheck(requireContext(), "????????????", parentPosition, dialog_title, liPeiAnimalDataDTO) { map ->
                                val movie = CheckDetail.LiPeiAnimalDataDTO()
                                //   LogUtils.e("map  $map")
                                movie.fEntryID = data[parentPosition]?.fEntryID
                                if (map[dialog_title[0]] == "0.0" || map[dialog_title[0]].isNullOrEmpty()) {
                                    movie.fLossAmount = data[parentPosition]?.fLossAmount
                                } else {
                                    val s = map[dialog_title[0]]
                                    if (Utils.isNumeric(s)) {
                                        if (s!!.toDouble() > Utils.getText(mUnitCoverage).toDouble()) {
                                            "????????????????????????????????????".showStr()
                                            return@DialogEditCheck
                                        }
                                    }
                                    movie.fLossAmount = s
                                }

                                if (map[dialog_title[1]] == "0.0" || map[dialog_title[1]].isNullOrEmpty())
                                    movie.fRiskPre = data[parentPosition]?.fRiskPre
                                else {
                                    val s = map[dialog_title[1]]
                                    if (Utils.isNumeric(s)) {
                                        if (s!!.toDouble() > 100) {
                                            "????????????????????????100".showStr()
                                            return@DialogEditCheck
                                        }
                                    }
                                    movie.fRiskPre = s
                                }

                                if (map[dialog_title[2]] == "0.0" || map[dialog_title[2]].isNullOrEmpty())
                                    movie.fEarNumber = data[adapterPosition]?.fEarNumber
                                else movie.fEarNumber = map[dialog_title[2]]
                                movie.liPeiAnimalPicData = liPeiAnimalDataDTO!!.liPeiAnimalPicData
                                requireActivity().runOnUiThread {
                                    mAdapter!!.upDateData(parentPosition, movie)
                                }

                            }
                            if (dialogEdit.isShowing) dialogEdit.dismiss()
                            if (!dialogEdit.isShowing) dialogEdit.show()
                        }
                    }
                }
                1 -> {

                    DialogDelete(requireContext(), "??????${parentPosition + 1}", object : GetBool {
                        override fun getBool(tf: Boolean) {
                            if (tf) {
                                if (mAdapter != null) {
                                    requireActivity().runOnUiThread {
                                        mAdapter!!.removeAt(parentPosition)
                                    }
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
            val editItem = SwipeMenuItem(context)
            editItem.text = "??????"
            editItem.setTextColorResource(R.color.white)
            editItem.setBackgroundColorResource(R.color.f08c792)
            editItem.width = width
            editItem.height = height
            rightMenu.addMenuItem(editItem)

            val deleteItem = SwipeMenuItem(context)
            deleteItem.text = "??????"
            deleteItem.setTextColorResource(R.color.white)
            deleteItem.setBackgroundColorResource(R.color.fb588f7)
            deleteItem.width = width
            deleteItem.height = height
            rightMenu.addMenuItem(deleteItem)


            val photoItem = SwipeMenuItem(context)
            photoItem.text = "??????"
            photoItem.setTextColor(Color.WHITE)
            photoItem.setBackgroundColorResource(R.color.f1de5e2)
            photoItem.width = width
            photoItem.height = height

            rightMenu.addMenuItem(photoItem)

        }

    }

    var y = 0

    fun upLoadOneByOne(dto_all: MutableList<CheckDetail.LiPeiAnimalDataDTO?>?, index: Int) {
        val img_dto: MutableList<CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO>? = ArrayList()

        //??????
        val data_pics: MutableList<String?> = ArrayList()
        dto_all?.let {
            if (index > dto_all.size - 1) return@let
            val item = dto_all[index]
            item?.let { dto ->
                val dto_ = CheckDetail.LiPeiAnimalDataDTO()

                val liPeiAnimalPicData1 = dto.liPeiAnimalPicData
                if (liPeiAnimalPicData1 != null && liPeiAnimalPicData1.isNotEmpty()) {

                    data_pics.clear()
                    for (item_pic in liPeiAnimalPicData1) {
                        val picUrl = item_pic.picUrl
                        if (!data_pics.contains(picUrl)) {
                            data_pics.add(picUrl)

                        }
                    }
                    UpLoadFileUtil.upLoadFile(context, "??????", index.toString(), data_pics) { list ->
                        //????????????
                        for (pic in list) {
                            val img_dto_ = CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO()
                            if (pic!!.startsWith("http"))
                                img_dto_.picUrl = pic
                            else img_dto_.picUrl = Config.PHOTO_URL + pic
                            img_dto?.add(img_dto_)
                        }
                        dto.liPeiAnimalPicData = img_dto
                        requireActivity().runOnUiThread { mAdapter!!.upDateData(index, dto) }

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
                    requireActivity().runOnUiThread { mAdapter!!.upDateData(index, dto) }

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
        if (dots!!.isEmpty()) return null
        val dto_arr = JSONArray()
        riskAmount = 0.0
        for (item in dots) {
            val pic_arr = JSONArray()
            val dot_json = JSONObject()
            val liPeiAnimalPicData = item!!.liPeiAnimalPicData
            liPeiAnimalPicData?.let {
                pic_arr.clear()
                for (item_ in it) {
                    val picUrl = item_.picUrl
                    if (!picUrl.isNullOrBlank()) {
                        val pic_json = JSONObject()
                        pic_json["PicUrl"] = picUrl
                        pic_arr.add(pic_json)
                    }

                }
            }
            val lossAmout = if (!item.fLossAmount.isNullOrBlank()) {
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

    var quck_list: MutableList<CheckDetail.LiPeiAnimalDataDTO?>? = ArrayList()
    fun getDetail(num: String?) {
        if (NetUtil.checkNet(requireContext())) {
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
        val intent = Intent(context, CameraOnlyActivity::class.java)
        val fenPei_ = FenPei()
        fenPei_.farmerName = fenPei!!.farmerName
        fenPei_.fRemark = "sunshi"

        intent.putExtra("fenpei", fenPei_)
        intent.putExtra("photo_num", current_pics.size)
        startActivityForResult(intent, 124)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 124 && resultCode == Activity.RESULT_OK) {

            val stringArrayListExtra = data!!.getStringArrayListExtra("imgs")
            LogUtils.e("stringArrayListExtra  $stringArrayListExtra")
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
                // .setPictureStyle(mPictureParameterStyle)
                //  .theme(R.style.picture_default_style)
                .maxSelectNum(9 - current_pics.size)
                .isCompress(true)
                .cutOutQuality(20)// ?????????????????? ??????90 int
                .minimumCompressSize(90)// ??????100kb??????????????????
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

                if (result.isNotEmpty())
                    for (item_current in result) {
                        val dto = CheckDetail.LiPeiAnimalDataDTO.LiPeiAnimalPicDataDTO()
                        dto.picUrl = item_current
                        liPeiAnimalPicData.add(dto)
                    }

                dto.liPeiAnimalPicData = liPeiAnimalPicData
                mAdapter!!.upDateData(click_position, eratag)
            }

        }
    }

    /*   private var mPictureParameterStyle: PictureParameterStyle? = null
       private fun getWhiteStyle() {
           // ????????????
           val requireContext = requireContext()
           mPictureParameterStyle = PictureParameterStyle()
           mPictureParameterStyle?.let { // ?????????????????????????????????(????????????)
               it.isChangeStatusBarFontColor = true
               // ??????????????????????????????(0/9)??????
               it.isOpenCompletedNumStyle = false
               // ??????????????????QQ???????????????????????????
               it.isOpenCheckNumStyle = false
               // ????????????????????????
               it.pictureStatusBarColor = ContextCompat.getColor(requireContext, R.color.white)
               // ??????????????????????????????
               it.pictureTitleBarBackgroundColor = ContextCompat.getColor(requireContext, R.color.white)
               // ???????????????????????????????????????
               it.pictureTitleUpResId = R.drawable.picture_icon_orange_arrow_up
               // ???????????????????????????????????????
               it.pictureTitleDownResId = R.drawable.picture_icon_orange_arrow_down
               // ?????????????????????????????????
               it.pictureFolderCheckedDotStyle = R.drawable.picture_orange_oval
               // ??????????????????
               it.pictureLeftBackIcon = R.drawable.picture_icon_back_arrow
               // ?????????????????????
               it.pictureTitleTextColor = ContextCompat.getColor(requireContext, R.color.black)
               // ????????????????????????????????????  ?????? ??????.pictureRightDefaultTextColor???.pictureRightDefaultTextColor
               it.pictureRightDefaultTextColor = ContextCompat.getColor(requireContext, R.color.black)
               // ??????????????????????????????
               it.pictureCheckedStyle = R.drawable.picture_checkbox_selector
               // ???????????????????????????
               it.pictureBottomBgColor = ContextCompat.getColor(requireContext, R.color.picture_color_fa)
               // ??????????????????????????????
               it.pictureCheckNumBgStyle = R.drawable.picture_num_oval
               // ????????????????????????????????????(?????????????????????????????????)
               it.picturePreviewTextColor = ContextCompat.getColor(requireContext(), R.color.picture_color_fa632d)
               // ??????????????????????????????????????????(????????????????????????????????????)
               it.pictureUnPreviewTextColor = ContextCompat.getColor(requireContext, R.color.picture_color_9b)
               // ???????????????????????????(????????? ???????????????)
               it.pictureCompleteTextColor = ContextCompat.getColor(requireContext, R.color.picture_color_fa632d)
               // ???????????????????????????(????????? ??????????????????)
               it.pictureUnCompleteTextColor = ContextCompat.getColor(requireContext, R.color.picture_color_9b)
               // ???????????????????????????
               // it.picturePreviewBottomBgColor = ContextCompat.getColor(requireContext, R.color.picture_color_white)
               // ????????????????????????  ?????????.isOriginalImageControl(true); ?????????
               it.pictureOriginalControlStyle = R.drawable.picture_original_checkbox
               // ?????????????????? ?????????.isOriginalImageControl(true); ?????????
               it.pictureOriginalFontColor = ContextCompat.getColor(requireContext, R.color.white)
               // ????????????????????????????????????
               it.pictureExternalPreviewDeleteStyle = R.drawable.picture_icon_black_delete
               // ??????????????????????????????????????????
               it.pictureExternalPreviewGonePreviewDelete = true
           }
       }*/
}