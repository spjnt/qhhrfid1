package tramais.hnb.hhrfid.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetBool
import tramais.hnb.hhrfid.util.PreferUtils
import tramais.hnb.hhrfid.util.TimeUtil

class DialogDownLoad(context: Context, var getBoolean: GetBool) : Dialog(context, R.style.dialog) {

    private var mCancle: TextView? = null
    private var mConfim: TextView? = null
    private var ivRemTime: ImageView? = null
    var isRem: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_down_load)
        mCancle = findViewById(R.id.un_down_load)
        mConfim = findViewById(R.id.down_load)
        ivRemTime = findViewById(R.id.iv_rem_time)
        mCancle!!.setOnClickListener {
            getBoolean.getBool(false)
            if (isRem) {
                val featDay = TimeUtil.getFeatDay(7)
                PreferUtils.putString(context, Constants.tips_time, featDay)
            }
            dismiss()
        }
        ivRemTime!!.setOnClickListener {
            isRem = !isRem
            ivRemTime!!.isSelected = isRem

        }
        mConfim!!.setOnClickListener {
            getBoolean.getBool(true)
            if (isRem) {
                val featDay = TimeUtil.getFeatDay(7)
                PreferUtils.putString(context, Constants.tips_time, featDay)
            }
            dismiss()
        }
    }
}