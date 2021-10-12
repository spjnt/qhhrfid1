package tramais.hnb.hhrfid.lv

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

// viewpager适配器
@SuppressLint("WrongConstant")
class ViewPagerAdapter internal constructor(fm: FragmentManager?, var fragmentList: MutableList<Fragment>) :
        FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(i: Int): Fragment {
        return fragmentList[i]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

}