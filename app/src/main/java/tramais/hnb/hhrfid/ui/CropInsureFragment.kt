package tramais.hnb.hhrfid.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.gyf.immersionbar.ktx.immersionBar
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.ModuleBean
import tramais.hnb.hhrfid.bean.Roles
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.RecyclerItemClickListener
import tramais.hnb.hhrfid.lv.MainModuleAdapter
import tramais.hnb.hhrfid.ui.crop.CropBaoAnChoiceActivity
import tramais.hnb.hhrfid.ui.crop.CropInsureCheckPublic
import tramais.hnb.hhrfid.ui.crop.CropStandardActivity
import tramais.hnb.hhrfid.ui.crop.CropSyncPayment
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.Utils

class CropInsureFragment : BaseFragment() {
    private var crop_rv: RecyclerView? = null
    private var crop_rv1: RecyclerView? = null
    private var crop_rv2: RecyclerView? = null
    var roles: MutableList<Roles>? = ArrayList()
    private var view_: View? = null
    override fun findViewById(view: View?) {
        view?.let {
            crop_rv = it.findViewById(R.id.crop_rv)
            crop_rv1 = it.findViewById(R.id.crop_rv1)
            crop_rv2 = it.findViewById(R.id.crop_rv2)
            view_ = it.findViewById(R.id.view)


            val params: LinearLayout.LayoutParams = view_!!.layoutParams as LinearLayout.LayoutParams

            params.weight = LinearLayout.LayoutParams.MATCH_PARENT.toFloat()

            params.height = BarUtils.getStatusBarHeight()

            view_!!.layoutParams = params
        }
        //  activity?.let { StatusBarUtil.setStatusBarMode(it, true, R.color.white) }
    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_crop_insure
    }

    override fun initListener() {

    }

    override fun initData() {

    }


    private fun initCropData(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()
        if (haveRoles("承保", "承保验标"))
            ints.add(ModuleBean("承保验标", R.mipmap.animal_chengbao))
        else
            ints.add(ModuleBean("承保验标", R.mipmap.animal_chengbao_gray))


        if (haveRoles("承保", "新建投保单"))
            ints.add(ModuleBean("新建投保单", R.mipmap.a_new))
        else
            ints.add(ModuleBean("新建投保单", R.mipmap.a_new_gray))

        if (haveRoles("承保", "审核公示"))
            ints.add(ModuleBean("审核公示", R.mipmap.a_public))
        else
            ints.add(ModuleBean("审核公示", R.mipmap.a_public_gray))

        if (haveRoles("承保", "同步缴费"))
            ints.add(ModuleBean("同步缴费", R.mipmap.a_money))
        else
            ints.add(ModuleBean("同步缴费", R.mipmap.a_money_gray))

        return ints
    }

    private fun initCropData1(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()

        if (haveRoles("理赔", "任务调度"))
            ints.add(ModuleBean("任务调度", R.mipmap.a_diaodu))
        else
            ints.add(ModuleBean("任务调度", R.mipmap.a_diaodu_gray))

        if (haveRoles("理赔", "任务查勘"))
            ints.add(ModuleBean("任务查勘", R.mipmap.a_chakan))
        else
            ints.add(ModuleBean("任务查勘", R.mipmap.a_chakan_gray))


        if (haveRoles("理赔", "现场抽样"))
            ints.add(ModuleBean("现场抽样", R.mipmap.crop_chouyang))
        else
            ints.add(ModuleBean("现场抽样", R.mipmap.crop_chouyang_gray))

        /*   if (haveRoles("理赔", "理赔定损"))
               ints.add(ModuleBean("理赔定损", R.mipmap.crop_lipei))
           else
               ints.add(ModuleBean("理赔定损", R.mipmap.crop_lipei_gray))
   */
        if (haveRoles("理赔", "审核公示"))
            ints.add(ModuleBean("审核公示", R.mipmap.a_public))
        else
            ints.add(ModuleBean("审核公示", R.mipmap.a_public_gray))


        if (haveRoles("理赔", "提交核心"))
            ints.add(ModuleBean("提交核心", R.mipmap.a_sub_core))
        else
            ints.add(ModuleBean("提交核心", R.mipmap.a_sub_core_gray))
        return ints
    }

    private fun initCropData2(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()
        if (haveRoles("数据查询", "种植户查询"))
            ints.add(ModuleBean("种植户查询", R.mipmap.a_farmer_chaxun))
        else
            ints.add(ModuleBean("种植户查询", R.mipmap.a_farmer_chaxun_gray))

        return ints
    }

    companion object {
        @JvmStatic
        fun newInstance(): CropInsureFragment {
            val fragment = CropInsureFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    /* fun goToFarmList(module: String) {
         var intent = Intent(context, ActivityFarmList::class.java)
         intent.putExtra(Constants.MODULE_NAME, module)
         startActivity(intent)
     }*/

    fun goToCropCheck(module: String) {
        /*  var intent = Intent(context, CropCheck::class.java)
          intent.putExtra(Constants.MODULE_NAME, module)
          startActivity(intent)
  */
        var intent = Intent(context, ActivityCheck::class.java)
        intent.putExtra(Constants.MODULE_NAME, module)
        requireContext().startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        roles = MainActivity.roles
        crop_rv!!.layoutManager = GridLayoutManager(context, 3)
        val adapter = MainModuleAdapter(initCropData(), context)
        crop_rv!!.adapter = adapter
        adapter.setListener(object : RecyclerItemClickListener {
            override fun onClick(view: View?, position: Int, name: String?) {
                when (name) {

                    "承保验标" -> {
                        val fxzCode = PreferUtils.getString(requireContext(), Constants.FXZCode)
                        if (fxzCode.isNotEmpty() && fxzCode.endsWith("00")) {
                            "该账号,不可承保验标".showStr()
                            return
                        }
                        if (haveRoles("承保", "承保验标"))
                            Utils.goToNextUI(CropStandardActivity::class.java)
                        else
                            tips()
                    }


                    "新建投保单" ->

                        if (haveRoles("承保", "新建投保单")) {
                            val intent = Intent(context, CropBaoAnChoiceActivity::class.java)
                            intent.putExtra(Constants.SO_WHAT, "Fragment")
                            startActivity(intent)
                        } else
                            tips()


                    "审核公示" ->
                        if (haveRoles("承保", "审核公示"))
                            Utils.goToNextUI(CropInsureCheckPublic::class.java)
                        else
                            tips()

                    "同步缴费" ->
                        if (haveRoles("承保", "同步缴费"))
                            Utils.goToNextUI(CropSyncPayment::class.java)
                        else
                            tips()

                }
            }
        })

        crop_rv1!!.layoutManager = GridLayoutManager(context, 3)
        val adapter1 = MainModuleAdapter(initCropData1(), context)
        crop_rv1!!.adapter = adapter1
        adapter1.setListener(object : RecyclerItemClickListener {
            override fun onClick(view: View?, position: Int, name: String?) {
                when (name) {
                    "任务调度" -> {
                        if (haveRoles("理赔", "任务调度"))
                        /*Utils.goToNextUI(CropActivityFenPei::class.java)*/ {
                            var intent = Intent(context, ActivityFenPei::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "种植险")
                            context!!.startActivity(intent)
                        } else
                            tips()
                    }
                    "任务查勘" ->
                        if (haveRoles("理赔", "任务查勘"))
                        // goToCropCheck(name)
                        {
                            /*  var intent = Intent(context, ActivityCheck::class.java)
                              intent.putExtra(Constants.MODULE_NAME, "种植险-$name")
                              context!!.startActivity(intent)*/
                            goToCropCheck("种植险-$name")
                        } else
                            tips()

                    "现场抽样" ->
                        if (haveRoles("理赔", "现场抽样"))
                            goToCropCheck("种植险-$name")
                        else
                            tips()

                    "提交核心" ->
                        if (haveRoles("理赔", name))
                            goToCropCheck("种植险-$name")
                        else
                            tips()
                    "审核公示" -> {
                        if (haveRoles("理赔", name)) {
                            var intent = Intent(context, ActivityAnimalCBPublic::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "种植险")
                            context!!.startActivity(intent)
                        } else
                            tips()

                    }
                }
            }
        })

        crop_rv2!!.layoutManager = GridLayoutManager(context, 3)
        val adapter2 = MainModuleAdapter(initCropData2(), context)
        crop_rv2!!.adapter = adapter2
        adapter2.setListener(object : RecyclerItemClickListener {
            override fun onClick(view: View?, position: Int, name: String?) {
                when (name) {
                    "种植户查询" -> {
                        if (haveRoles("数据查询", name)) {
                            var intent = Intent(context, ActivityFarmList::class.java)
                            intent.putExtra(Constants.MODULE_NAME, name)
                            startActivity(intent)
                        } else
                            tips()
                    }
                }
            }
        })
    }

    override fun initImmersionBar() {
        immersionBar {
            //   LogUtils.e("come in")
            statusBarDarkFont(true, 0.2f)
                    .statusBarView(view_)
        }
    }

    fun haveRoles(FObjSubGroup: String, FObjectName: String): Boolean {
        return haveRoles(roles!!, "种植险", FObjSubGroup, FObjectName)
    }

    fun tips() {
        "暂无该模块权限".showStr()
    }
}