package tramais.hnb.hhrfid.base

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.components.ImmersionFragment
import tramais.hnb.hhrfid.bean.Roles
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.ui.dialog.DialogAvi
import tramais.hnb.hhrfid.util.NetUtil
import tramais.hnb.hhrfid.util.PreferUtils

abstract class BaseFragment : ImmersionFragment() /*Fragment()*/ {
    private var dialogAvi: DialogAvi? = null
    private var mActivity: Activity? = null
    abstract fun findViewById(view: View?)

    abstract fun setLayoutId(): Int
    abstract fun initListener()
    abstract fun initData()

    // androidx.fragment.app.Fragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    // androidx.fragment.app.Fragment
    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        return layoutInflater.inflate(setLayoutId(), viewGroup, false)
    }

    // androidx.fragment.app.Fragment
    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        findViewById(view)
        initData()
        initListener()
    }

    fun String?.showStr() {
        if (this?.isNotEmpty() == true) {
            requireActivity().runOnUiThread { Toast.makeText(context, this, Toast.LENGTH_SHORT).show() }
        }
    }

    fun haveRoles(roles: List<Roles>?, type: String, FObjSubGroup: String, FObjectName: String): Boolean {
        return roles!!.isNotEmpty() && roles!!.filter { it.fObjGroup.equals(type) && it.fObjSubGroup.equals(FObjSubGroup) && it.fObjectName.equals(FObjectName) }.isNotEmpty()
    }

    fun ifC72(): Boolean {
         return Build.MODEL.equals("HC720");
    }
    fun ifHC720s(): Boolean {
        return Build.MODEL.equals("HC720S")
    }
    @JvmOverloads
    fun showAvi(str: String? = "") {
        if (dialogAvi == null) {
            dialogAvi = DialogAvi(requireContext(), str)
        }
        if (dialogAvi!!.isShowing) {
            dialogAvi!!.setText(str)
        }
        if (!dialogAvi!!.isShowing)
          dialogAvi!!.show()

    }

    fun hideAvi() {
        if (dialogAvi == null) {
            dialogAvi = DialogAvi(mActivity!!, "")
        }
        if (dialogAvi != null) {
            dialogAvi!!.dismiss()
        }
    }

//    fun netTips() {
//        if (!NetUtil.checkNet(requireContext())) {
//            "请在联网环境下操作".showStr()
//            return
//        }
//    }

    val companyNum: String
        get() = PreferUtils.getString(context, Constants.companyNumber)
    val userNum: String
        get() = PreferUtils.getString(context, Constants.userNumber)
    val userName: String
        get() = PreferUtils.getString(context, Constants.UserName)
}