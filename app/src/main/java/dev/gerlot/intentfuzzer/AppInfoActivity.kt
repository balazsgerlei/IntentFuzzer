package dev.gerlot.intentfuzzer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import dev.gerlot.intentfuzzer.util.AppInfo
import dev.gerlot.intentfuzzer.util.Utils
import dev.gerlot.intentfuzzer.util.Utils.getPackageInfo

class AppInfoActivity : AppCompatActivity() {

    private var msg = Utils.MSG_PROCESSING
    private val context: Context = this

    private var appType = Utils.ALL_APPS
    private var progressBar: ProgressBar? = null
    private var listView: ListView? = null
    private var listAppInfo: List<AppInfo>? = null
    private var appInfoAdapter: AppInfoAdapter? = null

    private var mGetPkgInfoThread: Thread? = null

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Utils.MSG_DONE -> {
                    progressBar?.visibility = View.GONE
                    listView?.adapter = appInfoAdapter
                }

                Utils.MSG_PROCESSING -> progressBar?.visibility = View.VISIBLE
                Utils.MSG_ERROR -> {}
            }
        }
    }

    private val pkgInfoRunnable = Runnable {
        listAppInfo = getPackageInfo(context, appType)
        appInfoAdapter = AppInfoAdapter(context, listAppInfo)
        msg = Utils.MSG_DONE
        mHandler.obtainMessage(msg).sendToTarget()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.package_infos)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar = findViewById<View>(R.id.progressbar) as ProgressBar
        listView = findViewById<View>(R.id.app_listview) as ListView

        progressBar?.isIndeterminate = false

        mHandler.obtainMessage(msg).sendToTarget()

        if (intent.hasExtra(Utils.APPTYPE_KEY)) {
            appType = intent.getIntExtra(Utils.APPTYPE_KEY, Utils.ALL_APPS)
        }

        if (mGetPkgInfoThread == null) {
            mGetPkgInfoThread = Thread(pkgInfoRunnable)
            mGetPkgInfoThread!!.start()
        }

        listView?.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                (appInfoAdapter?.getItem(position) as AppInfo).let { appInfo ->
                    Intent(this, FuzzerActivity::class.java).also { newIntent ->
                        Bundle().apply {
                            putParcelable(Utils.APPINFO_KEY, appInfo)
                        }.also {
                            newIntent.putExtras(it);
                            startActivity(newIntent)
                        }
                    }
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
