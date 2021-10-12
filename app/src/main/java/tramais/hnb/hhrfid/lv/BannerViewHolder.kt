package tramais.hnb.hhrfid.lv

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.zhouwei.mzbanner.holder.MZViewHolder
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.util.TransformationUtils

internal class BannerViewHolder : MZViewHolder<String?> {
    private var mImageView: ImageView? = null
    // com.zhouwei.mzbanner.holder.MZViewHolder



    override fun createView(context: Context?): View {
        val inflate: View = LayoutInflater.from(context).inflate(R.layout.banner_item, null as ViewGroup?)
        mImageView = inflate.findViewById(R.id.banner_image) as ImageView
        return inflate
    }

    override fun onBind(context: Context?, position: Int, data: String?) {
        Glide.with(context!!).asBitmap().load(data).into(TransformationUtils(mImageView))
    }


}