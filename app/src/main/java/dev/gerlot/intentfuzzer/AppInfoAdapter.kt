package dev.gerlot.intentfuzzer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import dev.gerlot.intentfuzzer.util.AppInfo


class AppInfoAdapter(context: Context, appInfos: List<AppInfo>?) :
    BaseAdapter() {
    private var mlistAppInfo: List<AppInfo>? = null

    var infater: LayoutInflater? = null

    init {
        infater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mlistAppInfo = appInfos
    }

    override fun getCount(): Int {
        // TODO Auto-generated method stub

        return mlistAppInfo!!.size
    }

    override fun getItem(position: Int): Any {
        // TODO Auto-generated method stub
        return mlistAppInfo!![position]
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return 0
    }

    override fun getView(position: Int, convertview: View?, viewGroup: ViewGroup): View {
        var view: View? = null
        var holder: ViewHolder? = null
        if (convertview?.tag == null) {
            view = infater?.inflate(R.layout.package_info, null)
            holder = ViewHolder(view!!)
            view!!.tag = holder
        } else {
            view = convertview
            holder = convertview.tag as ViewHolder
        }
        val appInfo: AppInfo = getItem(position) as AppInfo
        holder!!.appIcon.setImageDrawable(appInfo.appIcon)
        holder.appName.setText(appInfo.appName)
        holder.packageName.setText(appInfo.packageName)
        return view!!
    }

    internal inner class ViewHolder(view: View) {
        var appIcon: ImageView = view.findViewById<View>(R.id.app_icon) as ImageView
        var appName: TextView = view.findViewById<View>(R.id.app_name) as TextView
        var packageName: TextView = view.findViewById<View>(R.id.package_name) as TextView
    }
}
