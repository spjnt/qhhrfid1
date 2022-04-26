package tramais.hnb.hhrfid.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import tramais.hnb.hhrfid.R
import tramais.hnb.hhrfid.base.BaseActivity
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.constant.Constants.Sign_Path
import tramais.hnb.hhrfid.ui.view.LinePathView
import tramais.hnb.hhrfid.util.FileUtil
import tramais.hnb.hhrfid.util.TimeUtil


class ActivitySign : BaseActivity() {
    private var mTitle: TextView? = null
    private var mClear: TextView? = null
    private var mSave: TextView? = null
    private var about: TextView? = null
    private var iv_sign: ImageView? = null
    private var rem_about: ImageView? = null
    private var isRember: Boolean = false
    private lateinit var mLinePath: LinePathView
    private var fromOrientation = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        hideAllTitle()
        fromOrientation = intent.getIntExtra("fromOrientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    override fun initView() {
        iv_sign = findViewById(R.id.iv_sign)
        mLinePath = findViewById(R.id.line_path)
        mTitle = findViewById(R.id.title)
        mClear = findViewById(R.id.clear)
        mSave = findViewById(R.id.save)
        about = findViewById(R.id.about)
        about!!.text = Html.fromHtml(resources.getString(R.string.checkbox_text))
        rem_about = findViewById(R.id.iv_rem_pass)
        rem_about!!.isSelected = false
        mLinePath.setPaintWidth(6)
    }

    var sign_common: String? = null
    var signature: String? = null
    var type: String? = null
    override fun initData() {
        intent?.let {
            type = it.getStringExtra(Constants.Type)
            val for_sign = it.getStringExtra(Constants.For_Sign)
            sign_common = it.getStringExtra(Constants.Sign_Common)
            mTitle!!.text = for_sign
        }
    }

    override fun initListner() {
        about!!.setOnClickListener {
            val intent = Intent(this, PdfActivity::class.java)
            intent.putExtra(Constants.Type, type)
            startActivity(intent)
        }
        rem_about!!.setOnClickListener {
            isRember = !isRember
            rem_about!!.isSelected = isRember
        }
        mClear!!.setOnClickListener {
            if (iv_sign!!.visibility == View.VISIBLE) {
                iv_sign!!.visibility = View.GONE
                mLinePath.visibility = View.VISIBLE
            }
            if (mLinePath.visibility == View.VISIBLE && mLinePath.touched)
                mLinePath.clear()
        }
        mSave!!.setOnClickListener {
            if (!isRember) {
                showStr("请先阅读条款并同意")
                return@setOnClickListener
            }
            if (mLinePath.visibility == View.VISIBLE && mLinePath.touched) {
                val time = TimeUtil.getTime(Constants.yyyyMMddHHmmss)
                val path = FileUtil.getSDPath() + Constants.sdk_middle_sign
                val childPaht = "$time.jpg"
                // FileUtil.makeFilePath(path, childPaht)
                mLinePath.save(path + childPaht)
                val intent = Intent()
                //  LogUtils.e("path + childPaht:"+(path + childPaht))
                intent.putExtra(Sign_Path, path + childPaht)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                val intent = Intent()
                intent.putExtra(Sign_Path, signature)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fromOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
    }
}