package dev.gerlot.intentfuzzer.util

import android.content.ComponentName

data class ComponentInfo(
    val type: Int = Utils.ACTIVITIES,
    val name: ComponentName,
)
