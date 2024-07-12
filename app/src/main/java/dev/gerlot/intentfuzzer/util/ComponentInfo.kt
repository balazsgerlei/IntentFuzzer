package dev.gerlot.intentfuzzer.util

class ComponentInfo {
    var compType: Int = Utils.ACTIVITIES

    var componentName: String? = null
        private set

    fun setClassName(componentName: String?) {
        this.componentName = componentName
    }
}
