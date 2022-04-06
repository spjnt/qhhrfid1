package tramais.hnb.hhrfid.util

import android.content.Context
import android.text.TextUtils
import com.apkfuns.logutils.LogUtils
import tramais.hnb.hhrfid.bean.HttpBean
import tramais.hnb.hhrfid.constant.Config
import tramais.hnb.hhrfid.constant.Constants
import tramais.hnb.hhrfid.interfaces.GetList
import tramais.hnb.hhrfid.interfaces.GetOneString
import tramais.hnb.hhrfid.interfaces.OkResponseInterface
import tramais.hnb.hhrfid.net.OkhttpUtil
import tramais.hnb.hhrfid.net.Params
import java.util.*

object UpLoadFileUtil {
    fun upLoadFile(context: Context?, category: String?, num: String?, paths: List<String?>?, getList: GetList) {
        val urls: LinkedList<String> = LinkedList<String>()
        urls.clear()
        if (paths == null || paths.isEmpty()) {
            urls.add("")
            getList.getList(urls)
            return
        }
        var serial = 1
        for (item in paths) {
//            LogUtils.e("$item")
            upLoadFile(context, category.toString(), num, item, serial++) {
                urls.add(it)
                if (urls.size == paths.size) {
                    getList.getList(urls)
                    System.gc()
                }
            }
        }
    }

    fun upLoadFile(context: Context?, num: String?, paths: MutableMap<String, String?>, getList: GetList) {
        val urls: LinkedList<String> = LinkedList<String>()
        urls.clear()
        var serial = 1
        if (paths.isEmpty()) {
            urls.add("")
            getList.getList(urls)
            return
        }
        paths.forEach { it ->
            upLoadFile(context, it.key, num, it.value, serial++) {
                urls.add(it)
                if (urls.size == paths.size) {
                    getList.getList(urls)
                }
            }
        }
    }

    fun upLoadFile(context: Context?, category: String, num: String?, item: String?, serial: Int, getList: GetOneString) {
        if (item != "null" && item!!.isNotEmpty() && !item.contains("http")) {
            val params = Params.createParams()
            params.add("picture", ImageUtils.getStream(item))
            when (category) {
                "身份证" -> params.add("filename", "sfz${num}.jpg")
                "身份证反面" -> params.add("filename", "sfzback${num}.jpg")
                "银行卡" -> params.add("filename", "bank${num}.jpg")
                "养殖户环境照" -> params.add("filename", "farmer${num}.jpg")
                // "种植查勘" -> params.add("filename", "chakan${TimeUtil.getTime(Constants.yyyyMMddHHmmss1)}-${serial}.jpg")
                "耳标照片" -> params.add("filename", "ear${num}-${serial}.jpg")
                "签名" -> params.add("filename", "sign${num}-${TimeUtil.getTime(Constants.hms)}-${serial}.jpg")
                "查勘" -> params.add("filename", "chakan${TimeUtil.getTime(Constants.yyyyMMddHHmmss1)}-${serial}.jpg")
                "被保险人签名" -> params.add("filename", "asign${num}.jpg")
                "查勘员签名" -> params.add("filename", "bsign${num}.jpg")
                "抽样专家签名" -> params.add("filename", "csign${num}.jpg")
                "抽样查勘员签名" -> params.add("filename", "dsign${num}.jpg")
                "抽样农户签名" -> params.add("filename", "esign${num}.jpg")
                "集体投保确认盖章" -> params.add("filename", "seal${num}.jpg")
                "种植险农户签名" -> params.add("filename", "tbdsign${num}.jpg")
                "签名2" -> params.add("filename", "sign${num}-${TimeUtil.getTime(Constants.hms)}-${serial}.jpg")
            }
            OkhttpUtil.getInstance(context).doPosts(Config.UploadPicture, params, object : OkResponseInterface {
                override fun onSuccess(bean: HttpBean, id: Int) {
                    val response = bean.response
                    if (!TextUtils.isEmpty(response)) {
                        val picUrl = response.substring(1, response.length - 1)
                        getList.getString(picUrl)
                    }
                    System.gc()

                }

                override fun onError(e: Exception) {
                    System.gc()
                }
            })

        } else {
            if (item.contains("http"))
                getList.getString(item)
            else
                getList.getString("")
            System.gc()

        }


    }

}