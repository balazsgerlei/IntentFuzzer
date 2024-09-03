package dev.gerlot.intentfuzzer.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.graphics.drawable.toBitmap


object Utils {

    const val ALL_APPS: Int = 0
    const val SYSTEM_APPS: Int = 1
    const val NONSYSTEM_APPS: Int = 2
    const val ABOUT: Int = 3

    const val MSG_PROCESSING: Int = 0
    const val MSG_DONE: Int = 1
    const val MSG_ERROR: Int = 2

    const val ACTIVITIES: Int = 0
    const val RECEIVERS: Int = 1
    const val SERVICES: Int = 2

    const val APPINFO_KEY: String = "appinfo"
    const val APPTYPE_KEY: String = "apptype"

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
        appInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.packageManager).toBitmap()

        return appInfo
    }
}
