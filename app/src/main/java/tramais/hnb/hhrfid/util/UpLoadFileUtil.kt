package tramais.hnb.hhrfid.util

import android.content.Context
import android.os.Build
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
import kotlin.system.measureTimeMillis

object UpLoadFileUtil {
    fun ifC72(): Boolean {
        return Build.MODEL.contains("HC72") || Build.MODEL.equals("SAH6380") || Build.MODEL.contains("c72") || Build.MODEL.contains("HC720")
    }

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
            if (item.isNullOrEmpty() || (item.isNotEmpty() && item.contains("http"))) {
                urls.add("")
                getList.getList(urls)
                return
            }
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
        // LogUtils.e("category  $category   $num  $item   $serial")
        if (item.isNullOrEmpty() || (!item.isNullOrEmpty() && item.contains("http"))) {
            if (!item.isNullOrEmpty() && item.contains("http")) {
                getList.getString(item)
            } else {
                getList.getString("")
            }

            System.gc()
            return
        }
        val params = Params.createParams()
        val steam = if (ifC72()) {
            ImageUtils.getStreamT(item)
        } else {
            ImageUtils.getStream(item)
        }
        //  LogUtils.e("stream  $steam")
        params.add("picture", steam)
        when (category) {
            "?????????" -> params.add("filename", "sfz${num}.jpg")
            "???????????????" -> params.add("filename", "sfzback${num}.jpg")
            "?????????" -> params.add("filename", "bank${num}.jpg")
            "??????????????????" -> params.add("filename", "farmer${num}.jpg")
            // "????????????" -> params.add("filename", "chakan${TimeUtil.getTime(Constants.yyyyMMddHHmmss1)}-${serial}.jpg")
            "????????????" -> params.add("filename", "ear${num}-${serial}.jpg")
            "??????" -> params.add("filename", "sign${num}-${TimeUtil.getTime(Constants.hms)}-${serial}.jpg")
            "??????" -> params.add("filename", "chakan${TimeUtil.getTime(Constants.yyyyMMddHHmmss1)}-${serial}.jpg")
            "??????????????????" -> params.add("filename", "asign${num}.jpg")
            "???????????????" -> params.add("filename", "bsign${num}.jpg")
            "??????????????????" -> params.add("filename", "csign${num}.jpg")
            "?????????????????????" -> params.add("filename", "dsign${num}.jpg")
            "??????????????????" -> params.add("filename", "esign${num}.jpg")
            "????????????????????????" -> params.add("filename", "seal${num}.jpg")
            "?????????????????????" -> params.add("filename", "tbdsign${num}.jpg")
            "??????2" -> params.add("filename", "sign${num}-${TimeUtil.getTime(Constants.hms)}-${serial}.jpg")
        }
        OkhttpUtil.getInstance(context).doPosts(Config.UploadPicture, params, object : OkResponseInterface {
            override fun onSuccess(bean: HttpBean, id: Int) {
                val response = bean.response
                // LogUtils.e("response  $response")
                if (!TextUtils.isEmpty(response)) {
                    val picUrl = response.substring(1, response.length - 1)
                    getList.getString(picUrl)
                }
                System.gc()

            }

            override fun onError(e: Exception) {
                getList.getString("")
                System.gc()
            }
        })


    }

}