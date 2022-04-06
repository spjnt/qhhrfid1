package tramais.hnb.hhrfid.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.android.synthetic.main.activity_weight_length.*
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.interfaces.ChoicePhoto
import tramais.hnb.hhrfid.ui.popu.PopuChoicePicture
import tramais.hnb.hhrfid.util.GlideEngine

class ActivityWeightBody : BaseActivity(), ChoicePhoto {
    private var mWeight: TextView? = null

    private var mLength: TextView? = null
    private var mCamer: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_length)
    }

    override fun initView() {
        setTitleText("AI理赔")
        mLength = findViewById(R.id.et_length)
        mWeight = findViewById(R.id.et_weight)
        mCamer = findViewById(R.id.camer)
    }

    override fun initData() {

    }

    override fun initListner() {
        mCamer!!.setOnClickListener {
            PopuChoicePicture(this@ActivityWeightBody, this@ActivityWeightBody).showAtLocation(mCamer, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        }
    }

    override fun actionCamera() {

    }

    override fun actionAlbum() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE)
                .maxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: List<LocalMedia>) {
                        if (result.isNullOrEmpty()) return
                        val realPath = result[0].realPath
                        showAvi("")
                        Glide.with(this@ActivityWeightBody).load(realPath).into(iv_phpto)
                        Thread {
                            Thread.sleep(3000)
                            runOnUiThread {
                                mLength!!.text = "77.9 厘米"
                                mWeight!!.text = "18.1 公斤"
                                Glide.with(this@ActivityWeightBody).load(R.mipmap.body_weight_water).into(iv_phpto)
                            }
                            hideAvi()
                        }.start()
                    }
                    override fun onCancel() {}
                })
    }
}