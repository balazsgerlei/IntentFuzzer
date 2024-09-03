package dev.gerlot.intentfuzzer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import dev.gerlot.intentfuzzer.util.AppInfo

class AppInfoAdapter(context: Context, appInfos: List<AppInfo>?) : BaseAdapter() {
    private var mlistAppInfo: List<AppInfo>? = null

    private var infater: LayoutInflater? = null

    init {
        infater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mlistAppInfo = appInfos
    }

    override fun getCount(): Int {
        return mlistAppInfo!!.size
    }

    override fun getItem(position: Int): Any {
        return mlistAppInfo!![position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertview: View?, viewGroup: ViewGroup): View {
        var view: View? = null
        var holder: ViewHolder? = null
        if (convertview?.tag == null) {
            view = infater?.inflate(R.layout.package_info, null)
            holder = ViewHolder(view!!)
            view.tag = holder
        } else {
            view = convertview
            holder = convertview.tag as ViewHolder
        }
        val appInfo: AppInfo = getItem(position) as AppInfo
        holder.appIcon.setImageBitmap(appInfo.appIcon)
        holder.appName.text = appInfo.appName
        holder.packageName.text = appInfo.packageName
        return view
    }

    internal inner class ViewHolder(view: View) {
        var appIcon: ImageView = view.findViewById<View>(R.id.app_icon) as ImageView
        var appName: TextView = view.findViewById<View>(R.id.app_name) as TextView
        var packageName: TextView = view.findViewById<View>(R.id.package_name) as TextView
    }
}
