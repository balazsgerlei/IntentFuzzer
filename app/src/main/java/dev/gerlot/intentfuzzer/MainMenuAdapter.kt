package dev.gerlot.intentfuzzer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MainMenuAdapter(context: Context) : BaseAdapter() {
    private var mContext: Context? = null
    private val alpha = 180

    private var infater: LayoutInflater? = null

    override fun getCount(): Int {
        return mThumbIds.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val holder: ViewHolder?
        if (convertView == null) {
            view = infater?.inflate(R.layout.menu_item, null)
            holder = ViewHolder(view!!)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        holder.menuImg.setImageResource(mThumbIds[position])
        holder.menuImg.setAlpha(alpha)
        holder.menuLabel.setText(menuLabels[position])

        return view
    }

    internal inner class ViewHolder(view: View) {
        var menuImg: ImageView = view.findViewById<View>(R.id.img_menu) as ImageView
        var menuLabel: TextView = view.findViewById<View>(R.id.label_menu) as TextView
    }

    private val mThumbIds = arrayOf(
        R.drawable.allapps,
        R.drawable.system,
        R.drawable.application,
        R.drawable.about,
    )

    private val menuLabels = arrayOf(
        R.string.all_apps,
        R.string.system_apps,
        R.string.nonsystem_apps,
        R.string.about
    )

    init {
        mContext = context
        infater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}
