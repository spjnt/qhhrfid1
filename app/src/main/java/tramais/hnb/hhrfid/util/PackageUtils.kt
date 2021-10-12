package tramais.hnb.hhrfid.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object PackageUtils {
    // 获取版本名字
    fun getVersionName(context: Context): String? {
        val pm = context.packageManager // 获取包的管理者
        val packageInfo: PackageInfo
        try {
            packageInfo = pm.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName //获取版本名字
        } catch (e: PackageManager.NameNotFoundException) {

            e.printStackTrace()
        }
        return null
    }

    // 获取版本号
    fun getVersionCode(context: Context): Int {
        val pm = context.packageManager // 获取包的管理者
        val packageInfo: PackageInfo
        try {
            packageInfo = pm.getPackageInfo(context.packageName, 0)
            return packageInfo.versionCode //获取版本号
        } catch (e: PackageManager.NameNotFoundException) {

            e.printStackTrace()
        }
        return -1
    }
}