package dev.gerlot.intentfuzzer

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import dev.gerlot.intentfuzzer.util.AppInfo
import dev.gerlot.intentfuzzer.util.ComponentInfo
import dev.gerlot.intentfuzzer.util.SerializableTest
import dev.gerlot.intentfuzzer.util.Utils

class FuzzerActivity : AppCompatActivity() {

    private var cmpTypes = ipcTypesToNames.values.toList()
    private var currentType = cmpTypes[0]
    private var typeGroup: MaterialButtonToggleGroup? = null
    private var cmpListView: ListView? = null
    private var fuzzAllNullBtn: Button? = null
    private var fuzzAllSeBtn: Button? = null

    private var cmpAdapter: ComponentAdapter? = null

    private val cmpNames = ArrayList<String>()
    private var components: List<ComponentInfo> = emptyList()
    private var pkgInfo: PackageInfo? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fuzzer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val appInfo: AppInfo? = if (intent.hasExtra(Utils.APPINFO_KEY)) {
            intent.getParcelableExtra(Utils.APPINFO_KEY)
        } else null

        if (appInfo != null) {
            title = appInfo.appName
            pkgInfo = appInfo.packageInfo
        } else {
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

    private fun initView() {
        typeGroup = findViewById(R.id.type_select)
        cmpListView = findViewById(R.id.cmp_listview)
        fuzzAllNullBtn = findViewById(R.id.fuzz_all_null)
        fuzzAllSeBtn = findViewById(R.id.fuzz_all_se)

        cmpListView?.onItemClickListener =
            OnItemClickListener { _, _, position, _ ->
                cmpAdapter?.getItem(position).toString().let { className ->
                    val intentToSend = Intent()
                    var targetComponentName: ComponentName? = null
                    for (component in components) {
                        if (component.name.className == className) {
                            targetComponentName = component.name
                            break
                        }
                    }

                    intentToSend.setComponent(targetComponentName)
                    if (sendIntentByType(intentToSend, currentType)) {
                        Toast.makeText(this@FuzzerActivity, "Sent Null $intentToSend", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(this@FuzzerActivity, "Send $intentToSend Failed!", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

        cmpListView?.onItemLongClickListener =
            OnItemLongClickListener { _, _, position, _ ->
                cmpAdapter?.getItem(position).toString().let { className ->
                    val intentToSend = Intent()
                    var targetComponentName: ComponentName? = null
                    for (component in components) {
                        if (component.name.className == className) {
                            targetComponentName = component.name
                            break
                        }
                    }

                    intentToSend.setComponent(targetComponentName)
                    intentToSend.putExtra("test", SerializableTest())

                    if (sendIntentByType(intentToSend, currentType)) {
                        Toast.makeText(
                            this@FuzzerActivity,
                            "Sent Serializeable $intentToSend", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this@FuzzerActivity, "Send $intentToSend Failed!", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                true
            }


        fuzzAllNullBtn?.setOnClickListener {
            for (component in components) {
                val intentToSend = Intent()
                intentToSend.setComponent(component.name)
                if (sendIntentByType(intentToSend, currentType)) {
                    Toast.makeText(this@FuzzerActivity, "Sent Null $intentToSend", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this@FuzzerActivity, R.string.send_faild, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        fuzzAllSeBtn?.setOnClickListener {
            for (component in components) {
                val intentToSend = Intent()
                intentToSend.setComponent(component.name)
                intentToSend.putExtra("test", SerializableTest())
                if (sendIntentByType(intentToSend, currentType)) {
                    Toast.makeText(
                        this@FuzzerActivity,
                        "Sent Serializeable $intentToSend", Toast.LENGTH_LONG
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
            typeGroup?.addView(button)
        }
        typeGroup?.addOnButtonCheckedListener { _, checkedId, isChecked ->
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
        (typeGroup?.getChildAt(0) as MaterialButton?)?.isChecked = true
    }

    private fun updateComponents(currentType: String?) {
        fuzzAllNullBtn?.visibility = View.INVISIBLE
        fuzzAllSeBtn?.visibility = View.INVISIBLE
        components = getComponents(currentType)
        cmpNames.clear()
        if (components.isNotEmpty()) {
            for (component in components) {
                if (!cmpNames.contains(component.name.className)) {
                    cmpNames.add(component.name.className)
                }
            }

            fuzzAllNullBtn?.visibility = View.VISIBLE
            fuzzAllSeBtn?.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, R.string.no_compt, Toast.LENGTH_LONG).show()
        }
        setListView()
    }


    private fun getComponents(currentType: String?): List<ComponentInfo> {
        val cmpsFound = ArrayList<ComponentInfo>()
        val type = ipcNamesToTypes[currentType]
        when (type) {
            Utils.ACTIVITIES -> pkgInfo!!.activities
            Utils.RECEIVERS -> pkgInfo!!.receivers
            Utils.SERVICES -> pkgInfo!!.services
            else -> pkgInfo!!.activities
        }?.forEach {
            cmpsFound.add(
                ComponentInfo(
                    type = type!!,
                    name = ComponentName(pkgInfo!!.packageName, it.name)
                )
            )
        }

        return cmpsFound
    }

    private fun setListView() {
        cmpAdapter = ComponentAdapter(this, components)
        cmpListView?.adapter = cmpAdapter
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
