package dev.gerlot.intentfuzzer.util

import java.io.Serializable


class SerializableTest : Serializable {
    var s: Boolean = false
    var t: Short = 0

    companion object {
        private const val serialVersionUID = 1L
    }
}
