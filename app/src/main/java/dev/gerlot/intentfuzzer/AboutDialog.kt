package dev.gerlot.intentfuzzer

import android.app.Dialog
import android.content.Context
import android.os.Bundle

class AboutDialog : Dialog {
    private var context: Context

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, theme: Int) : super(context, theme) {
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog)
    }
}
