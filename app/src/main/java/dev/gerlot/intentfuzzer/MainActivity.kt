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

    private var gridView: GridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        gridView = findViewById<View>(R.id.gridview) as GridView
        gridView!!.selector = ColorDrawable(Color.TRANSPARENT)
        gridView!!.setAdapter(MainMenuAdapter(this))
        gridView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                if (position == 0) {
                    val intent = Intent(
                        this@MainActivity,
                        AppInfoActivity::class.java
                    )
                    intent.putExtra("type", Utils.ALL_APPS)
                    startActivity(intent)
                }
                if (position == 1) {
                    val intent = Intent(
                        this@MainActivity,
                        AppInfoActivity::class.java
                    )
                    intent.putExtra("type", Utils.SYSTEM_APPS)
                    startActivity(intent)
                }

                if (position == 2) {
                    val intent = Intent(
                        this@MainActivity,
                        AppInfoActivity::class.java
                    )
                    intent.putExtra("type", Utils.NONSYSTEM_APPS)
                    startActivity(intent)
                }
                if (position == 3) {
                    val dialog = Dialog(this@MainActivity, R.style.dialog)
                    dialog.setContentView(R.layout.dialog)
                    dialog.show()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }*/
}
