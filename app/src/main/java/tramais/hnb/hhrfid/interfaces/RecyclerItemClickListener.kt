package tramais.hnb.hhrfid.interfaces

import android.view.View

/**
 * recyleView   item点击事件
 */
interface RecyclerItemClickListener {
    fun onClick(view: View?, position: Int, name: String?)
}