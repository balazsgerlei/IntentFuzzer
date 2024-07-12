package dev.gerlot.intentfuzzer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
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

    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Utils.MSG_DONE -> {
                    progressBar!!.visibility = View.GONE
                    listView!!.adapter = appInfoAdapter
                }

                Utils.MSG_PROCESSING -> progressBar!!.visibility = View.VISIBLE
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
        setProgressBarVisibility(true)
        setContentView(R.layout.package_infos)

        progressBar = findViewById<View>(R.id.progressbar) as ProgressBar
        listView = findViewById<View>(R.id.app_listview) as ListView

        progressBar!!.isIndeterminate = false

        mHandler.obtainMessage(msg).sendToTarget()

        val intent = intent
        if (intent.hasExtra("type")) {
            appType = intent.getIntExtra("type", Utils.ALL_APPS)
        }

        if (mGetPkgInfoThread == null) {
            mGetPkgInfoThread = Thread(pkgInfoRunnable)
            mGetPkgInfoThread!!.start()
        }

        listView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // TODO Auto-generated method stub
                val appInfo: AppInfo = appInfoAdapter!!.getItem(position) as AppInfo
                (application as IntentFuzzerApp).packageInfo = appInfo.packageInfo

                val intent = Intent(
                    this@AppInfoActivity,
                    FuzzerActivity::class.java
                )
                //Bundle bundle = new Bundle();
                //bundle.putParcelable(Utils.PKGINFO_KEY, appInfo.getPackageInfo());
                //intent.putExtras(bundle);
                startActivity(intent)
            }
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.package_infos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }*/
}
