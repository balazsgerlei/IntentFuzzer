package dev.gerlot.intentfuzzer

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import dev.gerlot.intentfuzzer.util.SerializableTest
import dev.gerlot.intentfuzzer.util.Utils

class FuzzerActivity : AppCompatActivity() {

    private var cmpTypes = ipcTypesToNames.values.toList()
    private var currentType = cmpTypes[0]
    private var typeGroup: MaterialButtonToggleGroup? = null
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

        //pkgInfo = getPkgInfo();
        pkgInfo = (application as IntentFuzzerApp).packageInfo
        if (pkgInfo == null) {
            Toast.makeText(this, R.string.pkginfo_null, Toast.LENGTH_LONG).show()
            return
        }

        initView()
        initTypeGroup()
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
        typeGroup = findViewById(R.id.type_select)!!
        cmpListView = findViewById(R.id.cmp_listview)
        fuzzAllNullBtn = findViewById(R.id.fuzz_all_null)
        fuzzAllSeBtn = findViewById(R.id.fuzz_all_se)

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

    private fun initTypeGroup() {
        cmpTypes.forEach {  type ->
            val button = MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
            button.text = type
            typeGroup!!.addView(button)
        }
        typeGroup!!.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked) {
                Log.d("salala", "checkedId: $checkedId")
                cmpTypes.forEachIndexed { index, _ ->
                    if ((typeGroup!!.getChildAt(index) as MaterialButton).id == checkedId) {
                        currentType = cmpTypes[index]
                        updateComponents(currentType)
                    }
                }
            }
        }
        (typeGroup!!.getChildAt(0) as MaterialButton).isChecked = true
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
        }?.forEach {
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
