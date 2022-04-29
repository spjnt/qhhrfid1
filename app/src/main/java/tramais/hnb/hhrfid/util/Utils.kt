package tramais.hnb.hhrfid.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.litepal.LitePal.findAllAsync
import tramais.hnb.hhrfid.base.QhApplication.Companion.context
import tramais.hnb.hhrfid.bean.Region
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetCommon
import tramais.hnb.hhrfid.litePalBean.AnimalSaveCache
import tramais.hnb.hhrfid.litePalBean.RegionCache
import java.io.File
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException
import kotlin.math.ceil

object Utils {
    @JvmStatic
    fun goToNextUI(cls: Class<*>?) {
        val intent = Intent(context, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(intent)
    }

    fun isNumeric(str: String?): Boolean {
        if (str.isNullOrEmpty()) return false
        /* val pattern = Pattern.compile("[0-9]*")
         val isNum = pattern.matcher(str)
         return isNum.matches()*/
        return isInteger(str) || isDouble(str)
    }

    /**
     * 判断字符串是否是数字
     */
    fun isNumber(value: String): Boolean {
        return isInteger(value) || isDouble(value)
    }

    /**
     * 判断字符串是否是整数
     */
    fun isInteger(value: String?): Boolean {
        return try {
            value!!.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    fun isDouble(value: String?): Boolean {
        return try {
            value!!.toDouble()
            value!!.contains(".")
        } catch (e: NumberFormatException) {
            false
        }
    }

    @JvmStatic
    fun getText(textView: TextView?): String {
        return textView?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

    @JvmStatic
    fun getEdit(textView: EditText?): String {
        return textView?.text?.toString()?.trim { it <= ' ' } ?: ""
    }

    /**
     * 判断手机号是否符合规范
     *
     * @param phoneNo 输入的手机号
     * @return
     */
    @Throws(PatternSyntaxException::class)
    fun isPhoneNum(phoneNo: String?): Boolean {
        //   if (phoneNo.isEmpty()) return false;
        val regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$"
        val p = Pattern.compile(regExp)
        val m = p.matcher(phoneNo)
        return m.matches()
    }

    fun list2String(list: List<String?>?): String? {
        return if (list != null && list.isNotEmpty()) {
            if (list.size == 1) {
                list[0]
            } else {
                val buffer = StringBuffer()
                for (item in list) {
                    buffer.append(item)
                    buffer.append("-")
                }
                buffer.toString()
            }
        } else {
            ""
        }
    }

    fun callPhone(mContext: Context, phoneNum: String?) {
        val intent = Intent(Intent.ACTION_CALL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        mContext.startActivity(intent)
    }

    fun getM(input: String?): String? {
        return if (!TextUtils.isEmpty(input)) {
            val p = Pattern.compile("\\d+")
            val m = p.matcher(input)
            m.find()
            m.group().replace(" ".toRegex(), "")
        } else {
            null
        }
    }

    fun list2String2(list: List<String?>?): String? {
        return if (list != null && list.size > 0) {
            if (list.size == 1) {
                list[0]
            } else {
                val buffer = StringBuffer()
                for (item in list) {
                    buffer.append(item)
                    buffer.append("|")
                }
                buffer.toString()
            }
        } else {
            ""
        }
    }

    fun stringToList(phtots: String): List<String>? {
        val photot: MutableList<String> = ArrayList()
        photot.clear()
        if (!TextUtils.isEmpty(phtots)) {
            if (!phtots.contains("|")) {
                photot.add(phtots)
            } else {
                val split = phtots.split("|").toTypedArray()
                photot.addAll(Arrays.asList(*split))
            }
        } else {
            return null
        }
        return photot
    }

    /*投保清单："IN" + System.DateTime.Now.ToString("yyyyMMdd") + maxno.ToString().PadLeft(5, '0');
    出险报案："BA" + System.DateTime.Now.ToString("yyyyMMdd") + maxno.ToString().PadLeft(5, '0');*/
    fun getTouBaonumber(num: Int): String {
        return "ZIN" + TimeUtil.getTime(Constants.yyyy__MM__dd) + String.format("%05d", num)
    }

    fun getBillnumber(num: Int): String {
        return "ZBA" + TimeUtil.getTime(Constants.yyyy__MM__dd) + String.format("%05d", num)
    }

    @JvmStatic
    fun formatDouble(d: Double): String {
        val df = DecimalFormat("#####0.00")
        return df.format(d)
    }

    fun getRegions(getRegion: GetCommon<Region>) {
        val region = Region()
        findAllAsync(RegionCache::class.java).listen { list ->
            if (list != null && list.size > 0) {
                val cache = list[0]
                region.code = cache.code
                region.fCity = cache.fCity
                region.fCounty = cache.fCounty
                region.fNumber = cache.fNumber
                region.fProvince = cache.fProvince
                region.msg = cache.msg
                //JSONArray jsonArray = cache.getJsonArray();
                //String s = jsonArray.toJSONString();
                val array = JSONArray.parseArray(cache.data_arr)
                val data_: MutableList<Region.DataBean> = ArrayList()
                if (array != null && array.size > 0) {
                    for (i in array.indices) {
                        val o = array[i] as JSONObject
                        val dataBean = Region.DataBean()
                        dataBean.fTown = o.getString("FTown")
                        dataBean.fVillage = o.getString("FVillage")
                        dataBean.fRegionNumber = o.getString("FRegionNumber")
                        if (dataBean != null) data_.add(dataBean)
                    }
                }
                region.data = data_
            }
            getRegion.getCommon(region)
        }
    }

    fun Dp2Px(context: Context, dp: Float): Double {
        val scale = context.resources.displayMetrics.density

        return (dp * scale + 0.5f).toDouble()
    }

    fun Px2Dp(context: Context, px: Float): Double {
        val scale = context.resources.displayMetrics.density
        return (px / scale + 0.5f).toDouble()
    }


    fun splitList(list: List<*>?, len: Int): List<List<*>>? {
        if (list.isNullOrEmpty()) {
            return null
        }
        val result: MutableList<List<*>> = ArrayList()
        val size = list.size
        val count = (size + len - 1) / len
        for (i in 0 until count) {
            val subList = list.subList(i * len, if ((i + 1) * len > size) size else len * (i + 1))
            result.add(subList)
        }
        return result
    }

    fun splitSize(list: List<*>?, len: Int): List<List<*>>? {
        if (list.isNullOrEmpty()) {
            return null
        }
        val size = list.size
        val result: MutableList<List<*>> = ArrayList()
        result.clear()
        val every = ceil(size.toDouble() / len).toInt()
        for (i in 0 until len) {
            val fromIndex = i * every
            val endIndex = if ((i + 1) * every > size) {
                size
            } else {
                (i + 1) * every
            }
            // LogUtils.e("form:  $fromIndex   toEnd:  $endIndex")
            if (fromIndex < endIndex) {
                val subList = list.subList(fromIndex, endIndex)
                if (subList.isNotEmpty())
                    result.add(subList)
            }
        }
        return result
    }

    fun getFilesAllName(path: String?): List<String>? {
        val file = path?.let { File(it) }
        val files: Array<File> = file!!.listFiles()
        if (files == null) {
            Log.e("error", "空目录")
            return null
        }
        val s: MutableList<String> = ArrayList()
        for (i in files.indices) {
            val name = files[i].name
            val name_ = name.split("_")[0]
            if (!s.contains(name_))
                s.add(name_)
        }
        return s
    }

    fun up() {
        val ids = "632221197105172014"
        val path = FileUtil.getSDPath() + Constants.sdk_middle_animal + "$ids/"
        val filesAllName = getFilesAllName(path)
        for (item in filesAllName!!) {
            val saveCache = AnimalSaveCache()
            saveCache.farmName = "罗华前"
            saveCache.tel = "13997402160"
            saveCache.isUpLoad = "0"
            saveCache.statu = "新建"
            saveCache.ageMonth = "12"
            saveCache.latitude = 0.0
            saveCache.longitude = 0.0
            saveCache.animalType = "藏系牦牛~IXO~990319~140248"
            saveCache.isMakeDeal = "0"
            saveCache.category_name = ""
            saveCache.img1 = path + item + "_" + "0.jpg"
            saveCache.img2 = path + item + "_" + "1.jpg"
            saveCache.img3 = path + item + "_" + "2.jpg"
            saveCache.img4 = path + item + "_" + "3.jpg"
            saveCache.lableNum = item
            saveCache.farmID = ids
            saveCache.comPanyNumber = "63222100"
            saveCache.employeeNumber = "19274525"
            saveCache.creatTime = TimeUtil.getTime(Constants.yyyy_MM_ddHHmmss)
            saveCache.save()
        }
        // LogUtils.e("name  $filesAllName")
    }

    infix fun String.plus(str:String):String{
     return this.plus(str)
    }
}