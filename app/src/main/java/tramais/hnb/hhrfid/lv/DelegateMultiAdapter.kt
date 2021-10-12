package tramais.hnb.hhrfid.lv

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.bean.NewsList

class DelegateMultiAdapter(protected var mContext: Context) : BaseDelegateMultiAdapter<NewsList?, BaseViewHolder>() {
     override fun convert(helper: BaseViewHolder, item: NewsList?) {
        when (helper?.itemViewType) {
            NewsList.TEXT -> {
                helper.setText(R.id.news_title, item?.title)
                helper.setText(R.id.news_time, item?.updatetime)
            }
            NewsList.IMG_TEXT -> {
                helper.setText(R.id.news_title, item?.title)
                helper.setText(R.id.news_time, item?.updatetime)
                val view = helper.getView<ImageView>(R.id.news_iv)
                Glide.with(mContext).load(item?.pictureurl).into(view)
            }
            else -> {
            }
        }
    }

    // 方式二：实现自己的代理类
    internal class MyMultiTypeDelegate : BaseMultiTypeDelegate<NewsList?>() {
        override fun getItemType(data: List<NewsList?>, position: Int): Int {
            val pictureurl = data[position]!!.pictureurl
            return if (pictureurl.isNullOrEmpty()) {
                NewsList.TEXT
            } else {
                NewsList.IMG_TEXT
            }

        }

        init {
            addItemType(NewsList.TEXT, R.layout.item_home_news_no_img)

            addItemType(NewsList.IMG_TEXT, R.layout.item_home_news_with_img)
        }
    }

    init {


        //******************************************************************************************
        // 方式二，实现自己的代理类：
        setMultiTypeDelegate(MyMultiTypeDelegate())
    }


}