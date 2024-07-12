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
        // TODO Auto-generated method stub

        return mlistComponentInfo!!.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return mlistComponentInfo!![position]
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return 0
    }

    override fun getView(position: Int, convertview: View?, viewGroup: ViewGroup): View {
        var view: View? = null
        var holder: ViewHolder? = null
        if (convertview?.tag == null) {
            view = infater?.inflate(R.layout.component, null)
            holder = ViewHolder(view!!)
            view!!.tag = holder
        } else {
            view = convertview
            holder = convertview.tag as ViewHolder
        }
        val componentInfo: ComponentInfo = getItem(position) as ComponentInfo
        holder!!.componentName.setText(componentInfo.componentName)
        return view
    }

    internal inner class ViewHolder(view: View) {
        var componentName: TextView = view.findViewById<View>(R.id.component_name) as TextView
    }
}
