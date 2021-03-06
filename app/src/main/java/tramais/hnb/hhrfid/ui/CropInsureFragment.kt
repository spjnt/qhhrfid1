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

        return ints
    }

    private fun initCropData1(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()

        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_diaodu))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_diaodu_gray))

        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_chakan))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_chakan_gray))


        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.crop_chouyang))
        else
            ints.add(ModuleBean("????????????", R.mipmap.crop_chouyang_gray))

        /*   if (haveRoles("??????", "????????????"))
               ints.add(ModuleBean("????????????", R.mipmap.crop_lipei))
           else
               ints.add(ModuleBean("????????????", R.mipmap.crop_lipei_gray))
   */
        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_public))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_public_gray))


        if (haveRoles("??????", "????????????"))
            ints.add(ModuleBean("????????????", R.mipmap.a_sub_core))
        else
            ints.add(ModuleBean("????????????", R.mipmap.a_sub_core_gray))
        return ints
    }

    private fun initCropData2(): MutableList<ModuleBean> {
        val ints: MutableList<ModuleBean> = ArrayList()
        if (haveRoles("????????????", "???????????????"))
            ints.add(ModuleBean("???????????????", R.mipmap.a_farmer_chaxun))
        else
            ints.add(ModuleBean("???????????????", R.mipmap.a_farmer_chaxun_gray))

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

                    "????????????" -> {
                        val fxzCode = PreferUtils.getString(requireContext(), Constants.FXZCode)
                        if (fxzCode.isNotEmpty() && fxzCode.endsWith("00")) {
                            "?????????,??????????????????".showStr()
                            return
                        }
                        if (haveRoles("??????", "????????????"))
                            Utils.goToNextUI(CropStandardActivity::class.java)
                        else
                            tips()
                    }


                    "???????????????" ->

                        if (haveRoles("??????", "???????????????")) {
                            val intent = Intent(context, CropBaoAnChoiceActivity::class.java)
                            intent.putExtra(Constants.SO_WHAT, "Fragment")
                            startActivity(intent)
                        } else
                            tips()


                    "????????????" ->
                        if (haveRoles("??????", "????????????"))
                            Utils.goToNextUI(CropInsureCheckPublic::class.java)
                        else
                            tips()

                    "????????????" ->
                        if (haveRoles("??????", "????????????"))
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
                    "????????????" -> {
                        if (haveRoles("??????", "????????????"))
                        /*Utils.goToNextUI(CropActivityFenPei::class.java)*/ {
                            var intent = Intent(context, ActivityFenPei::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "?????????")
                            context!!.startActivity(intent)
                        } else
                            tips()
                    }
                    "????????????" ->
                        if (haveRoles("??????", "????????????"))
                        // goToCropCheck(name)
                        {
                            /*  var intent = Intent(context, ActivityCheck::class.java)
                              intent.putExtra(Constants.MODULE_NAME, "?????????-$name")
                              context!!.startActivity(intent)*/
                            goToCropCheck("?????????-$name")
                        } else
                            tips()

                    "????????????" ->
                        if (haveRoles("??????", "????????????"))
                            goToCropCheck("?????????-$name")
                        else
                            tips()

                    "????????????" ->
                        if (haveRoles("??????", name))
                            goToCropCheck("?????????-$name")
                        else
                            tips()
                    "????????????" -> {
                        if (haveRoles("??????", name)) {
                            var intent = Intent(context, ActivityAnimalCBPublic::class.java)
                            intent.putExtra(Constants.MODULE_NAME, "?????????")
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
                    "???????????????" -> {
                        if (haveRoles("????????????", name)) {
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
        return haveRoles(roles!!, "?????????", FObjSubGroup, FObjectName)
    }

    fun tips() {
        "?????????????????????".showStr()
    }
}