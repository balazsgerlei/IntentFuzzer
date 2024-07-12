package dev.gerlot.intentfuzzer

import android.app.Dialog
import android.content.Context
import android.os.Bundle

class AboutDialog : Dialog {
    private var context: Context

    constructor(context: Context) : super(context) {
        // TODO Auto-generated constructor stub
        this.context = context
    }

    constructor(context: Context, theme: Int) : super(context, theme) {
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog)
    }
}
