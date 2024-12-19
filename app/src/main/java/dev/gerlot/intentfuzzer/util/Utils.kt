package dev.gerlot.intentfuzzer.util

import android.content.ComponentName
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
            val applicationInfoFlags = packageInfo.applicationInfo?.flags
            if (type == SYSTEM_APPS) {
                if (applicationInfoFlags != null && (applicationInfoFlags and ApplicationInfo.FLAG_SYSTEM) == 1) {
                    pkgInfoList.add(fillAppInfo(packageInfo, context))
                }
            } else if (type == NONSYSTEM_APPS) {
                if (applicationInfoFlags != null && (applicationInfoFlags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    pkgInfoList.add(fillAppInfo(packageInfo, context))
                }
            } else {
                pkgInfoList.add(fillAppInfo(packageInfo, context))
            }
        }

        return pkgInfoList.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.appName })
    }


    private fun fillAppInfo(packageInfo: PackageInfo, context: Context): AppInfo {
        val appInfo = AppInfo(
            appName = packageInfo.applicationInfo?.loadLabel(context.packageManager).toString(),
            packageName = packageInfo.packageName,
            appIcon = packageInfo.applicationInfo?.loadIcon(context.packageManager)?.toBitmap(),
            activities = packageInfo.activities?.map {
                ComponentName(it.packageName, it.name)
            },
            receivers = packageInfo.receivers?.map {
                ComponentName(it.packageName, it.name)
            },
            services = packageInfo.services?.map {
                ComponentName(it.packageName, it.name)
            },
        )

        return appInfo
    }
}
