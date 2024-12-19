package dev.gerlot.intentfuzzer.util

import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppInfo(
    val appName: String = "",
    val packageName: String = "",
    @IgnoredOnParcel val appIcon: Bitmap? = null,
    val activities: List<ComponentName>? = null,
    val receivers: List<ComponentName>? = null,
    val services: List<ComponentName>? = null,
) : Parcelable
