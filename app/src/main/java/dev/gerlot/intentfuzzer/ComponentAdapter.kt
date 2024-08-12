package dev.gerlot.intentfuzzer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import dev.gerlot.intentfuzzer.util.ComponentInfo

class ComponentAdapter(context: Context, componentInfos: List<ComponentInfo>?) :
    BaseAdapter() {
    private var mlistComponentInfo: List<ComponentInfo>? = null

    var infater: LayoutInflater? = null

    init {
        infater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mlistComponentInfo = componentInfos
    }

    override fun getCount(): Int {
        return mlistComponentInfo!!.size
    }

    override fun getItem(position: Int): Any {
        return mlistComponentInfo!![position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertview: View?, viewGroup: ViewGroup): View {
        var view: View? = null
        var holder: ViewHolder? = null
        if (convertview?.tag == null) {
            view = infater?.inflate(R.layout.component, null)
            holder = ViewHolder(view!!)
            view.tag = holder
        } else {
            view = convertview
            holder = convertview.tag as ViewHolder
        }
        val componentInfo: ComponentInfo = getItem(position) as ComponentInfo
        val fullClassName = componentInfo.name.className
        val packageName = fullClassName.substring(startIndex = 0, endIndex = fullClassName.lastIndexOf("."))
        val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
        holder.componentPackage.text = packageName
        holder.componentName.text = className
        return view
    }

    internal inner class ViewHolder(view: View) {
        var componentPackage: TextView = view.findViewById<View>(R.id.component_package) as TextView
        var componentName: TextView = view.findViewById<View>(R.id.component_name) as TextView
    }
}
