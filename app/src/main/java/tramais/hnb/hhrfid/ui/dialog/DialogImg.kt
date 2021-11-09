package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.apkfuns.logutils.LogUtils
import com.bumptech.glide.Glide
import tramais.hnb.hhrfid.R

class DialogImg(context: Context, private val url: String?) : Dialog(context, R.style.dialog) {
    private var mIvPic: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_commom)
        mIvPic = findViewById(R.id.iv_pic)
        Glide.with(context).load(url).into(mIvPic!!)
        //LogUtils.e("URL  $url")
        mIvPic!!.setOnClickListener { view: View? -> dismiss() }
    }
}