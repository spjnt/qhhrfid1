package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.interfaces.GetBool

class DialogUpLoad(context: Context, var serNum: String?, var getBoolean: GetBool) : Dialog(context, R.style.dialog) {
    private var mSer: TextView? = null
    private var mCancle: TextView? = null
    private var mConfim: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_upload)
        mSer = findViewById(R.id.tv_ser)
        mCancle = findViewById(R.id.cancle)
        mConfim = findViewById(R.id.confim)
        mSer!!.text = "共有  $serNum  条数据未上传，去上传?"
        mCancle!!.setOnClickListener {
            getBoolean.getBool(false)
            dismiss()
        }

        mConfim!!.setOnClickListener {
            getBoolean.getBool(true)
            dismiss()
        }
    }
}