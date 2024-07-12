package dev.gerlot.intentfuzzer.util

import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable


data class AppInfo (
    var appName: String = "",
    var packageName: String = "",
    var appIcon: Drawable? = null,
    var packageInfo: PackageInfo? = null,
)
