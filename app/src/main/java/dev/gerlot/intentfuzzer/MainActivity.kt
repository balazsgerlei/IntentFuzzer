package dev.gerlot.intentfuzzer

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import dev.gerlot.intentfuzzer.util.Utils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val gridView: GridView = findViewById(R.id.gridview)
        gridView.selector = ColorDrawable(Color.TRANSPARENT)
        gridView.setAdapter(MainMenuAdapter(this))
        gridView.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                when(position) {
                    Utils.ALL_APPS -> Intent(this@MainActivity, AppInfoActivity::class.java).apply {
                        putExtra(Utils.APPTYPE_KEY, Utils.ALL_APPS)
                    }.also {
                        startActivity(it)
                    }
                    Utils.SYSTEM_APPS -> Intent(this@MainActivity, AppInfoActivity::class.java).apply {
                        putExtra(Utils.APPTYPE_KEY, Utils.SYSTEM_APPS)
                    }.also {
                        startActivity(it)
                    }
                    Utils.NONSYSTEM_APPS -> Intent(this@MainActivity, AppInfoActivity::class.java).apply {
                        putExtra(Utils.APPTYPE_KEY, Utils.NONSYSTEM_APPS)
                    }.also {
                        startActivity(it)
                    }
                    Utils.ABOUT -> Dialog(this@MainActivity, R.style.dialog).apply {
                        setContentView(R.layout.dialog)
                    }.show()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

}
