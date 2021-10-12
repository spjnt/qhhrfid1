package tramais.hnb.hhrfid.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils.getStatusBarHeight
import com.gyf.immersionbar.ktx.immersionBar
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseFragment
import tramais.hnb.hhrfid.bean.ModuleBean
import tramais.hnb.hhrfid.bean.Roles
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.RecyclerItemClickListener
import tramais.hnb.hhrfid.util.Utils
import tramais.hnb.hhrfid.lv.MainModuleAdapter

class AnimalInsureFragment : BaseFragment() {
    private var rv: RecyclerView? = null
    private var rv_lipei: RecyclerView? = null
    private var rv_search: RecyclerView? = null
    private var view_: View? = null
    override fun findViewById(view: View?) {
        view?.let {
            rv = it.findViewById(R.id.animal_rv_chengbao)
            rv_lipei = it.findViewById(R.id.crop_rv_lipei)
            rv_search = it.findViewById(R.id.crop_search)
            view_ = it.findViewById(R.id.view)
            val params: LinearLayout.LayoutParams = view_!!.layoutParams as LinearLayout.LayoutParams
            params.weight = LinearLayout.LayoutParams.MATCH_PARENT.toFloat()
            params.height = getStatusBarHeight()
            view_!!.layoutParams = params
        }

    }

    override fun setLayoutId(): Int {
        return R.layout.fragment_animal_insure
    }

    override fun initListener() {
    }

    override fun initData() {
    }

    var roles: MutableList<Roles>? = ArrayList()
    override fun onResume() {
        super.onResume()
        roles = MainActivity.roles
        rv!!.layoutManager = GridLayoutManager(context, 3)
        val adapter = MainModuleAdapter(initModuleData(), context)
        rv!!.adapter = adapter
        adapter.setListener(object : RecyclerItemClickListener {
            override fun onClick(view: View?, position: Int, name: String?) {
                when (name) {

                    "养殖户登记" -> {
                        if (haveRoles("承保", "养殖户登记"))
                            goToFarmList(name)
                        else
                            tips()

                    }
                    "承保验标" -> {
                        if (haveRoles("承保", "承保验标"))
                            goToFarmList(name)
                        else
                            tips()

                    }
                    "新建投保单" -> {
                        if (haveRoles("承保", "新建投保单"))
                            Utils.goToNextUI(ActivityTouBaoList::class.java)
                        else
                            tips()
                    }
                    "审核公示" -> {
                        if (haveRoles("承保", "新建投保单"))
                            Utils.goToNextUI(ActivityAnimalLPPublic::class.java)
                        else
                            tips()

                    }
                    "任务分配" -> {
                    }
                    "智能点数" -> {
                        goToFarmList(name)
                       // Utils.goToNextUI(ActivityNumber::class.java)
                    }
                    "同步缴费" -> {
                        if (haveRoles("承保", "新建投保单"))
                            Utils.goToNextUI(ActivityAnimalPay::class.java)
                        else
                            tips()

                    }

                }
            }
        })

        rv_lipei!!.layoutManager = GridLayoutManager(context, 3)
        val adapter_lipei = MainModuleAdapter(initLiPeiData(), context)
        rv_lipei!!.adapter = adapter_lipei
        adapter_lipei.setListener(object : RecyclerItemClickListener {
            override fun onClick(view: View?, position: Int, name: String?) {
                when (name) {
                    "任务调度" -> {
                        if (haveRoles("理赔", "任务调度")) {
                            var intent = Intent(context, ActivityFenPei::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "养殖险")
                            context!!.startActivity(intent)
                        }
                        // Utils.goToNextUI(ActivityFenPei::class.java)
                        else
                            tips()
                    }
                    "任务查勘" -> {
                        if (haveRoles("理赔", "任务查勘"))
                        //Utils.goToNextUI(ActivityCheck::class.java)
                        {
                            var intent = Intent(context, ActivityCheck::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "养殖险-$name")
                            context!!.startActivity(intent)
                        } else
                            tips()
                    }
                    /* "任务定损" -> {
                         if (haveRoles("理赔", "任务定损"))
                             Utils.goToNextUI(ActivityDingSun::class.java)
                         else
                             tips()
                     }*/
                    "查勘公示" -> {//ActivityAnimalLiPeiPublic
                        if (haveRoles("理赔", "查勘公示")) {
                            var intent = Intent(context, ActivityAnimalCBPublic::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "养殖险")
                            context!!.startActivity(intent)
                        }
                        //Utils.goToNextUI(ActivityAnimalCBPublic::class.java)

                        //   Utils.goToNextUI(ActivityAnimalCBPublic::class.java)
                    }
                    "提交核心" -> {
                        if (haveRoles("理赔", "提交核心")) {
                            var intent = Intent(context, ActivityCheck::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "养殖险-$name")
                            context!!.startActivity(intent)
                        } else
                            tips()

                    }
                    "AI理赔" -> {

                         Utils.goToNextUI(ActivityWeightBody::class.java)
                    }
                    "理赔拍照" -> {
                        Utils.goToNextUI(ActivityGoToCamer::class.java)
                    }
                }
            }
        })


        rv_search!!.layoutManager = GridLayoutManager(context, 3)
        val adapter_serch = MainModuleAdapter(initCheckData(), context)
        rv_search!!.adapter = adapter_serch
        adapter_serch.setListener(object : RecyclerItemClickListener {
            override fun onClick(view: View?, position: Int, name: String?) {
                when (name) {


                    "养殖户查询" -> {
                        if (haveRoles("数据查询", name))
                            goToFarmList(name)
                        else
                            tips()
                      /*  if (haveRoles("数据查询", "养殖户查询"))
                            Utils.goToNextUI(ActtivitySearchByFarmerOrTel::class.java)
                        else
                            tips()
*/
                    }
                    "按耳标查询" -> {
                        if (haveRoles("数据查询", "按耳标查询"))
                            Utils.goToNextUI(ActivitySearchByEarTag::class.java)
                        else
                            tips()

                    }
                    "数据上传" -> {
                        if (haveRoles("数据查询", "数据上传"))
                            Utils.goToNextUI(ActivityFile::class.java)
                        else
                            tips()
                    }
                }
            }
        })
    }

    override fun initImmersionBar() {

        immersionBar {
            statusBarDarkFont(true, 0.2f)
                    .statusBarView(view_)
        }
    }

    fun tips() {
        "暂无该模块权限".showStr()
    }

    fun goToFarmList(module: String) {
        var intent = Intent(context, ActivityFarmList::class.java)
        intent.putExtra(Constants.MODULE_NAME, module)
        startActivity(intent)
    }

    private fun initModuleData(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()

        if (haveRoles("承保", "养殖户登记"))
            ints.add(ModuleBean("养殖户登记", R.mipmap.animal_farmer_regist))
        else
            ints.add(ModuleBean("养殖户登记", R.mipmap.animal_farmer_regist_gray))

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
          ints.add(ModuleBean("智能点数", R.mipmap.body_length_weight))
        return ints
    }

    private fun initLiPeiData(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()
        if (haveRoles("理赔", "任务调度"))
            ints.add(ModuleBean("任务调度", R.mipmap.a_diaodu))
        else
            ints.add(ModuleBean("任务调度", R.mipmap.a_money_gray))

        if (haveRoles("理赔", "任务查勘"))
            ints.add(ModuleBean("任务查勘", R.mipmap.a_chakan))
        else
            ints.add(ModuleBean("任务查勘", R.mipmap.a_chakan_gray))

        /* if (haveRoles("理赔", "任务定损"))
             ints.add(ModuleBean("任务定损", R.mipmap.a_dingsun))
         else
             ints.add(ModuleBean("任务定损", R.mipmap.a_dingsun_gray))
 */
        if (haveRoles("理赔", "查勘公示"))
            ints.add(ModuleBean("查勘公示", R.mipmap.a_public))
        else
            ints.add(ModuleBean("查勘公示", R.mipmap.a_public_gray))

        if (haveRoles("理赔", "提交核心"))
            ints.add(ModuleBean("提交核心", R.mipmap.a_sub_core))
        else
            ints.add(ModuleBean("提交核心", R.mipmap.a_sub_core_gray))

      /*  if (haveRoles("理赔", "AI理赔"))
            ints.add(ModuleBean("AI理赔", R.mipmap.ai_lipei))
        else
            ints.add(ModuleBean("AI理赔", R.mipmap.ai_lipei_gray))*/
        ints.add(ModuleBean("理赔拍照", R.mipmap.take_photo))
        return ints
    }

    private fun initCheckData(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()
        if (haveRoles("数据查询", "养殖户查询"))
            ints.add(ModuleBean("养殖户查询", R.mipmap.a_farmer_chaxun))
        else
            ints.add(ModuleBean("养殖户查询", R.mipmap.a_farmer_chaxun_gray))


        if (haveRoles("数据查询", "按耳标查询"))
            ints.add(ModuleBean("按耳标查询", R.mipmap.crop_chouyang))
        else
            ints.add(ModuleBean("按耳标查询", R.mipmap.crop_chouyang_gray))


        if (haveRoles("数据查询", "数据上传"))
            ints.add(ModuleBean("数据上传", R.mipmap.a_shangchuan))
        else
            ints.add(ModuleBean("数据上传", R.mipmap.a_shangchuan_gray))

      /*  if (haveRoles("数据查询", "理赔进度查询"))
            ints.add(ModuleBean("理赔进度查询", R.mipmap.progress))
        else
            ints.add(ModuleBean("理赔进度查询", R.mipmap.progress_gray))*/
        return ints
    }

    companion object {
        @JvmStatic
        fun newInstance(): AnimalInsureFragment {
            val fragment = AnimalInsureFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    fun haveRoles(FObjSubGroup: String, FObjectName: String): Boolean {
        return haveRoles(roles!!, "养殖险", FObjSubGroup, FObjectName)
    }


}