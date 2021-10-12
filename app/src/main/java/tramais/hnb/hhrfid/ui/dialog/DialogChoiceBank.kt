package tramais.hnb.hhrfid.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.BankInfoDetail
import tramais.hnb.hhrfid.interfaces.GetBankDetail
import tramais.hnb.hhrfid.interfaces.GetTwoString
import tramais.hnb.hhrfid.lv.ExpandedBankAdapter
import java.util.*

class DialogChoiceBank(context: Context, var data: MutableList<BankInfoDetail.GetBankResulDataDTO?>?, var getBankDetail: GetBankDetail) : Dialog(context, R.style.dialog) {
    var towms: MutableList<String>? = ArrayList()
    var volls: MutableList<String>? = ArrayList()
    private var recycler_view: SwipeRecyclerView? = null
    private var mAdapter: ExpandedBankAdapter? = null
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_choice_bank)
        recycler_view = findViewById(R.id.recycler_view)
        recycler_view!!.layoutManager = LinearLayoutManager(context)

//        mIvImg!!.layoutManager = LinearLayoutManager(context)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ColorDrawable(R.color.gray))

        recycler_view!!.addItemDecoration(divider)
        mAdapter = ExpandedBankAdapter(context)
        recycler_view!!.setOnItemClickListener { itemView, position ->
            // 根据原position判断该item是否是parent item
            if (mAdapter!!.isParentItem(position)) {
                // 换取parent position
                val parentPosition = mAdapter!!.parentItemPosition(position)

                val dto_ = mAdapter!!.getData()?.get(parentPosition)
                val dto = dto_!!.getBankResultDetaiData

                if (dto == null || dto.isEmpty()) {
                    Toast.makeText(context, "暂无支行数据", Toast.LENGTH_LONG).show()
                    var detail =BankInfoDetail.GetBankResulDataDTO.GetBankResultDetaiDataDTO()
                    detail.fBankDetailName = dto_.fBackName
                    getBankDetail.getBankInfo(detail)
                    dismiss()
                    return@setOnItemClickListener
                }
                // 判断parent是否打开了二级菜单
                if (mAdapter!!.isExpanded(parentPosition)) {
                    data?.get(parentPosition)!!.isExpanded = false
                    mAdapter!!.notifyParentChanged(parentPosition)
                    // 关闭该parent下的二级菜单
                    mAdapter!!.collapseParent(parentPosition)
                } else {
                    data?.get(parentPosition)!!.isExpanded = true
                    mAdapter!!.notifyParentChanged(parentPosition)
                    // 打开该parent下的二级菜单
                    mAdapter!!.expandParent(parentPosition)
                }
            } else {
                // 换取parent position
                val parentPosition = mAdapter!!.parentItemPosition(position)
                // 换取child position
                val childPosition = mAdapter!!.childItemPosition(position)
                val dto = data?.get(parentPosition)!!.getBankResultDetaiData!![childPosition]
                getBankDetail.getBankInfo(dto)

                dismiss()
                //  Toast.makeText(context, data?.get(parentPosition)!!.getBankResultDetaiData!![childPosition].fBankDetailName, Toast.LENGTH_LONG).show()
            }
        }
        mAdapter!!.setGroupList(data)
        recycler_view!!.adapter = mAdapter
    }
}