package tramais.hnb.hhrfid.lv

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.CropSearch
import tramais.hnb.hhrfid.bean.NewsList
import tramais.hnb.hhrfid.ui.dialog.DialogImg

class CropDelegateMultiAdapter(protected var mContext: Context) : BaseDelegateMultiAdapter<CropSearch.DataDTO?, BaseViewHolder>() {
    override fun convert(helper: BaseViewHolder, item: CropSearch.DataDTO?) {
        when (helper?.itemViewType) {
            NewsList.TEXT -> {
                helper.setText(R.id.land_name, item?.landName)
                helper.setText(R.id.land_category, item?.category)
                helper.setText(R.id.land_square, item?.fCheckSquare + "亩")
            }
            NewsList.IMG_TEXT -> {
                helper.setText(R.id.land_name, item?.landName)
                helper.setText(R.id.land_category, item?.category)
                helper.setText(R.id.land_square, item?.fCheckSquare + "亩")
                val view = helper.getView<ImageView>(R.id.land_iv)
                Glide.with(mContext).load(item?.fGISPicture).into(view)
                view.setOnClickListener { DialogImg(mContext, item?.fGISPicture).show() }
            }
            else -> {
            }
        }
    }

    // 方式二：实现自己的代理类
    internal class MyMultiTypeDelegate : BaseMultiTypeDelegate<CropSearch.DataDTO?>() {
        override fun getItemType(data: List<CropSearch.DataDTO?>, position: Int): Int {
            val pictureurl = data[position]!!.fGISPicture
            return if (pictureurl.isNullOrEmpty()) {
                NewsList.TEXT
            } else {
                NewsList.IMG_TEXT
            }

        }

        init {
            addItemType(NewsList.TEXT, R.layout.item_crop_search_no_img)

            addItemType(NewsList.IMG_TEXT, R.layout.item_crop_search_with_img)
        }
    }

    init {


        //******************************************************************************************
        // 方式二，实现自己的代理类：
        setMultiTypeDelegate(MyMultiTypeDelegate())
    }


}