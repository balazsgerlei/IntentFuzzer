package dev.gerlot.intentfuzzer

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.gerlot.intentfuzzer.util.SerializableTest
import dev.gerlot.intentfuzzer.util.Utils


class FuzzerActivity : AppCompatActivity() {

    private val cmpTypes = ArrayList<String>()
    private var currentType: String? = null
    private var typeSpinner: Spinner? = null
    private var cmpListView: ListView? = null
    private var fuzzAllNullBtn: Button? = null
    private var fuzzAllSeBtn: Button? = null

    private var cmpAdapter: ArrayAdapter<String>? = null

    private val cmpNames = ArrayList<String>()
    private var components = ArrayList<ComponentName>()
    private var pkgInfo: PackageInfo? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fuzzer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        for (name in ipcTypesToNames.values) cmpTypes.add(name)
        currentType = cmpTypes[0]

        initView()
        initTypeSpinner()


        //pkgInfo = getPkgInfo();
        pkgInfo = (application as IntentFuzzerApp).packageInfo
        if (pkgInfo == null) {
            Toast.makeText(this, R.string.pkginfo_null, Toast.LENGTH_LONG).show()
            return
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

    private fun getPkgInfo(): PackageInfo? {
        var pkgInfo: PackageInfo? = null

        val intent = intent
        if (intent.hasExtra(Utils.PKGINFO_KEY)) {
            pkgInfo = intent.getParcelableExtra(Utils.PKGINFO_KEY)
        }

        return pkgInfo
    }

    private fun initView() {
        typeSpinner = findViewById<View>(R.id.type_select) as Spinner
        cmpListView = findViewById<View>(R.id.cmp_listview) as ListView
        fuzzAllNullBtn = findViewById<View>(R.id.fuzz_all_null) as Button
        fuzzAllSeBtn = findViewById<View>(R.id.fuzz_all_se) as Button

        cmpListView!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // TODO Auto-generated method stub
                var toSend: ComponentName? = null
                val intent = Intent()
                val className = cmpAdapter!!.getItem(position).toString()
                for (cmpName in components) {
                    if (cmpName.className == className) {
                        toSend = cmpName
                        break
                    }
                }

                intent.setComponent(toSend)
                if (sendIntentByType(intent, currentType)) {
                    Toast.makeText(this@FuzzerActivity, "Sent Null $intent", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this@FuzzerActivity, "Send $intent Failed!", Toast.LENGTH_LONG)
                        .show()
                }
            }

        cmpListView!!.onItemLongClickListener =
            OnItemLongClickListener { parent, view, position, id -> // TODO Auto-generated method stub
                var toSend: ComponentName? = null
                val intent = Intent()
                val className = cmpAdapter!!.getItem(position).toString()
                for (cmpName in components) {
                    if (cmpName.className == className) {
                        toSend = cmpName
                        break
                    }
                }

                intent.setComponent(toSend)
                intent.putExtra("test", SerializableTest())

                if (sendIntentByType(intent, currentType)) {
                    Toast.makeText(
                        this@FuzzerActivity,
                        "Sent Serializeable $intent", Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this@FuzzerActivity, "Send $intent Failed!", Toast.LENGTH_LONG)
                        .show()
                }
                true
            }


        fuzzAllNullBtn!!.setOnClickListener { // TODO Auto-generated method stub
            for (cmpName in components) {
                val intent = Intent()
                intent.setComponent(cmpName)
                if (sendIntentByType(intent, currentType)) {
                    Toast.makeText(this@FuzzerActivity, "Sent Null $intent", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this@FuzzerActivity, R.string.send_faild, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        fuzzAllSeBtn!!.setOnClickListener { // TODO Auto-generated method stub
            for (cmpName in components) {
                val intent = Intent()
                intent.setComponent(cmpName)
                intent.putExtra("test", SerializableTest())
                if (sendIntentByType(intent, currentType)) {
                    Toast.makeText(
                        this@FuzzerActivity,
                        "Sent Serializeable $intent", Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this@FuzzerActivity, R.string.send_faild, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun initTypeSpinner() {
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, cmpTypes
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner!!.adapter = typeAdapter

        typeSpinner!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateType()
                updateComponents(currentType)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
            }
        })
    }

    private fun updateType() {
        val selector = typeSpinner!!.selectedItem
        if (selector != null) {
            currentType = typeSpinner!!.selectedItem.toString()
        }
    }


    private fun updateComponents(currentType: String?) {
        fuzzAllNullBtn!!.visibility = View.INVISIBLE
        fuzzAllSeBtn!!.visibility = View.INVISIBLE
        components = getComponents(currentType)
        cmpNames.clear()
        if (!components.isEmpty()) {
            for (cmpName in components) {
                if (!cmpNames.contains(cmpName.className)) {
                    cmpNames.add(cmpName.className)
                }
            }

            fuzzAllNullBtn!!.visibility = View.VISIBLE
            fuzzAllSeBtn!!.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, R.string.no_compt, Toast.LENGTH_LONG).show()
        }
        setListView()
    }


    private fun getComponents(currentType: String?): ArrayList<ComponentName> {
        val cmpsFound = ArrayList<ComponentName>()
        when (ipcNamesToTypes[currentType]) {
            Utils.ACTIVITIES -> pkgInfo!!.activities
            Utils.RECEIVERS -> pkgInfo!!.receivers
            Utils.SERVICES -> pkgInfo!!.services
            else -> pkgInfo!!.activities
        }.forEach {
            cmpsFound.add(ComponentName(pkgInfo!!.packageName, it.name))
        }

        return cmpsFound
    }

    private fun setListView() {
        cmpAdapter = ArrayAdapter<String>(this, R.layout.component, cmpNames)
        cmpListView!!.adapter = cmpAdapter
    }

    private fun sendIntentByType(intent: Intent, type: String?): Boolean {
        try {
            when (ipcNamesToTypes[type]) {
                Utils.ACTIVITIES -> {
                    startActivity(intent)
                    return true
                }

                Utils.RECEIVERS -> {
                    sendBroadcast(intent)
                    return true
                }

                Utils.SERVICES -> {
                    startService(intent)
                    return true
                }

                else -> return true
            }
        } catch (e: Exception) {
            //e.printStackTrace();
            return false
        }
    }

    companion object {
        private var ipcTypesToNames: Map<Int, String> = sortedMapOf(
            Utils.ACTIVITIES to "Activities",
            Utils.RECEIVERS to "Receivers",
            Utils.SERVICES to "Services",
        )
        private var ipcNamesToTypes: Map<String, Int> = ipcTypesToNames.map { (k, v) -> v to k }.toMap()
    }

}
