package tramais.hnb.hhrfid.lv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.ModuleBean
import tramais.hnb.hhrfid.interfaces.RecyclerItemClickListener

class MainModuleAdapter(private var modules: MutableList<ModuleBean>, private var context: Context?) : RecyclerView.Adapter<MainModuleAdapter.MainViewHolder>() {
    private var listener: RecyclerItemClickListener? = null
    fun setListener(listener: RecyclerItemClickListener?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        if (context == null) context = parent.context
        return MainViewHolder(LayoutInflater.from(context).inflate(R.layout.item_module, parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val moduleBean = modules[position]
        holder.textView.text = moduleBean.module_name
        holder.imageView.setBackgroundResource(moduleBean.module_bit)
        holder.rlRootView.setOnClickListener { view: View? -> listener!!.onClick(view, position, modules[position].module_name) }
    }

    override fun getItemCount(): Int {
        return if (modules.size == 0) 0 else modules.size
    }

    inner class MainViewHolder(itemView: View) : ViewHolder(itemView) {
        var rlRootView: RelativeLayout
        var imageView: ImageView
        var textView: TextView

        init {
            rlRootView = itemView.findViewById(R.id.rl_rootView)
            imageView = itemView.findViewById(R.id.imageView)
            textView = itemView.findViewById(R.id.text_login_in)
        }
    }

    /* <!--  家长端:
    安全模块:
    进出宿舍 校门考勤  在途管理  监控直播  接送管理
    家校模块:
    请假管理 通知
           -->
    <!--
    教师端:
    安全模块:
    接送管理 进出宿舍 校门考勤
    家校模块:
    请假管理
    学生实时定位  学生提问管理
    -->*/
//    init {
////        val moduleBean = getModuleBean(nameList, photoList)
////        moduleBeans = moduleBean
//    }

    var modelues: MutableList<ModuleBean>? = ArrayList()
    fun getModuleBean(namelist: List<String?>?, iclist: List<Int?>?): List<ModuleBean>? {
        if (modelues != null) modelues!!.clear()
        if (namelist == null || namelist.size == 0) return null
        if (iclist == null || iclist.size == 0) return null
        for (i in namelist.indices) {
            modelues!!.add(ModuleBean(namelist[i]!!, iclist[i]!!))
        }
        return modelues
    }
}