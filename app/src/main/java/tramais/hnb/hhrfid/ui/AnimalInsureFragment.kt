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
import tramais.hnb.hhrfid.lv.MainModuleAdapter
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.Utils

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

                    "???????????????" -> {
                        if (haveRoles("??????", "???????????????"))
                            goToFarmList(name)
                        else
                            tips()

                    }
                    "????????????" -> {
                        if (haveRoles("??????", "????????????"))
                            goToFarmList(name)
                        else
                            tips()

                    }
                    "???????????????" -> {
                        if (haveRoles("??????", "???????????????"))
                            Utils.goToNextUI(ActivityTouBaoList::class.java)
                        else
                            tips()
                    }
                    "????????????" -> {
                        if (haveRoles("??????", "???????????????"))
                            Utils.goToNextUI(ActivityAnimalLPPublic::class.java)
                        else
                            tips()

                    }
                    "????????????" -> {
                    }
                    "????????????" -> {
                        goToFarmList(name)
                        // Utils.goToNextUI(ActivityNumber::class.java)
                    }
                    "????????????" -> {
                        if (haveRoles("??????", "???????????????"))
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
                    "????????????" -> {
                        if (haveRoles("??????", "????????????")) {
                            val intent = Intent(context, ActivityFenPei::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "?????????")
                            context!!.startActivity(intent)
                        }
                        // Utils.goToNextUI(ActivityFenPei::class.java)
                        else
                            tips()
                    }
                    "????????????" -> {
                        if (haveRoles("??????", "????????????"))
                        //Utils.goToNextUI(ActivityCheck::class.java)
                        {
                            val intent = Intent(context, ActivityCheck::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "?????????-$name")
                            context!!.startActivity(intent)
                        } else
                            tips()
                    }
                    /* "????????????" -> {
                         if (haveRoles("??????", "????????????"))
                             Utils.goToNextUI(ActivityDingSun::class.java)
                         else
                             tips()
                     }*/
                    "????????????" -> {//ActivityAnimalLiPeiPublic
                        if (haveRoles("??????", "????????????")) {
                            val intent = Intent(context, ActivityAnimalCBPublic::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "?????????")
                            context!!.startActivity(intent)
                        }
                        //Utils.goToNextUI(ActivityAnimalCBPublic::class.java)

                        //   Utils.goToNextUI(ActivityAnimalCBPublic::class.java)
                    }
                    "????????????" -> {
                        if (haveRoles("??????", "????????????")) {
                            val intent = Intent(context, ActivityCheck::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "?????????-$name")
                            context!!.startActivity(intent)
                        } else
                            tips()

                    }
                    "AI??????" -> {

                        Utils.goToNextUI(ActivityWeightBody::class.java)
                    }
                    "????????????" -> {
                        val fxzCode = PreferUtils.getString(requireContext(), Constants.FXZCode)
                        if (fxzCode.isNotEmpty() && fxzCode.endsWith("00")) {
                            "?????????,??????????????????".showStr()
                            return
                        }
                        Utils.goToNextUI(ActivityGoToCamera::class.java)
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
                    "???????????????" -> {
                        if (haveRoles("????????????", name))
                            goToFarmList(name)
                        else
                            tips()
                        /*  if (haveRoles("????????????", "???????????????"))
                              Utils.goToNextUI(ActtivitySearchByFarmerOrTel::class.java)
                          else
                              tips()
  */
                    }
                    "???????????????" -> {
                        if (haveRoles("????????????", "???????????????"))
                            Utils.goToNextUI(ActivitySearchByEarTag::class.java)
                        else
                            tips()

                    }
                    "????????????" -> {
                        if (haveRoles("????????????", "????????????"))
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
        "?????????????????????".showStr()
    }

    fun goToFarmList(module: String) {
        val intent = Intent(context, ActivityFarmList::class.java)
        intent.putExtra(Constants.MODULE_NAME, module)
        startActivity(intent)
    }

    private fun initModuleData(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()

        if (haveRoles("??????", "???????????????"))
            ints.add(ModuleBean("???????????????", R.mipmap.animal_farmer_regist))
        else
            ints.add(ModuleBean("???????????????", R.mipmap.animal_farmer_regist_gray))

        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.animal_chengbao))
        else
            ints.add(ModuleBean("????????????", R.mipmap.animal_chengbao_gray))

        if (haveRoles("??????", "???????????????"))
            ints.add(ModuleBean("???????????????", R.mipmap.a_new))
        else
            ints.add(ModuleBean("???????????????", R.mipmap.a_new_gray))

        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_public))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_public_gray))

        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_money))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_money_gray))

        ints.add(ModuleBean("????????????", R.mipmap.body_length_weight))
        return ints
    }

    private fun initLiPeiData(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()
        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_diaodu))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_money_gray))

        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_chakan))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_chakan_gray))

        /* if (haveRoles("??????", "????????????"))
             ints.add(ModuleBean("????????????", R.mipmap.a_dingsun))
         else
             ints.add(ModuleBean("????????????", R.mipmap.a_dingsun_gray))
 */
        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_public))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_public_gray))

        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_sub_core))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_sub_core_gray))

        /*  if (haveRoles("??????", "AI??????"))
              ints.add(ModuleBean("AI??????", R.mipmap.ai_lipei))
          else
              ints.add(ModuleBean("AI??????", R.mipmap.ai_lipei_gray))*/
        ints.add(ModuleBean("????????????", R.mipmap.take_photo))
        return ints
    }

    private fun initCheckData(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()
        if (haveRoles("????????????", "???????????????"))
            ints.add(ModuleBean("???????????????", R.mipmap.a_farmer_chaxun))
        else
            ints.add(ModuleBean("???????????????", R.mipmap.a_farmer_chaxun_gray))


        if (haveRoles("????????????", "???????????????"))
            ints.add(ModuleBean("???????????????", R.mipmap.crop_chouyang))
        else
            ints.add(ModuleBean("???????????????", R.mipmap.crop_chouyang_gray))


        if (haveRoles("????????????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_shangchuan))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_shangchuan_gray))

        /*  if (haveRoles("????????????", "??????????????????"))
              ints.add(ModuleBean("??????????????????", R.mipmap.progress))
          else
              ints.add(ModuleBean("??????????????????", R.mipmap.progress_gray))*/
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
        return haveRoles(roles!!, "?????????", FObjSubGroup, FObjectName)
    }


}