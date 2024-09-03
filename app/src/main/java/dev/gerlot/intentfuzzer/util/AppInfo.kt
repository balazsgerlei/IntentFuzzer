package dev.gerlot.intentfuzzer.util

import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppInfo(
    var appName: String = "",
    var packageName: String = "",
    var appIcon: Bitmap? = null,
    var packageInfo: PackageInfo? = null,
) : Parcelable
