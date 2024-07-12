package dev.gerlot.intentfuzzer.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager


object Utils {

    var ALL_APPS: Int = 0
    var SYSTEM_APPS: Int = 1
    var NONSYSTEM_APPS: Int = 2
    var ABOUNT: Int = 3

    val MSG_PROCESSING: Int = 0
    val MSG_DONE: Int = 1
    val MSG_ERROR: Int = 2

    val ACTIVITIES: Int = 0
    val RECEIVERS: Int = 1
    val SERVICES: Int = 2

    val PKGINFO_KEY: String = "pkginfo"
    val APPTYPE_KEY: String = "apptype"

    fun getPackageInfo(context: Context, type: Int): List<AppInfo> {
        val pkgInfoList: MutableList<AppInfo> = ArrayList()

        val packages = context.packageManager.getInstalledPackages(
            PackageManager.GET_DISABLED_COMPONENTS
                    or PackageManager.GET_ACTIVITIES
                    or PackageManager.GET_RECEIVERS
                    or PackageManager.GET_INSTRUMENTATION
                    or PackageManager.GET_SERVICES
        )

        for (i in packages.indices) {
            val packageInfo = packages[i]
            if (type == SYSTEM_APPS) {
                if ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1) {
                    pkgInfoList.add(fillAppInfo(packageInfo, context))
                }
            } else if (type == NONSYSTEM_APPS) {
                if ((packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    pkgInfoList.add(fillAppInfo(packageInfo, context))
                }
            } else {
                pkgInfoList.add(fillAppInfo(packageInfo, context))
            }
        }

        return pkgInfoList
    }


    private fun fillAppInfo(packageInfo: PackageInfo, context: Context): AppInfo {
        val appInfo = AppInfo()
        appInfo.packageInfo = packageInfo
        appInfo.appName = packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        appInfo.packageName = packageInfo.packageName
        appInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.packageManager)

        return appInfo
    }
}
