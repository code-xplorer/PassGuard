package com.ismail.creatvt.passguard.addpassword

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ismail.creatvt.passguard.R
import com.ismail.creatvt.passguard.model.Website
import com.ismail.creatvt.passguard.helpers.WebsiteFactory

class WebsiteArrayAdapter(
    context: Context,
    val objects: List<Website>
) : ArrayAdapter<Website>(context, R.layout.website_dropdown_item, objects),
    AdapterView.OnItemClickListener {

    private var selected: Int = NO_SELECTION

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = inflater.inflate(R.layout.website_dropdown_item, parent, false)
        val name: TextView = itemView.findViewById(R.id.name_text) as TextView
        val icon: ImageView = itemView.findViewById(R.id.icon) as ImageView

        itemView.rootView.setBackgroundColor(
            itemView.resources.getColor(
                if (selected == position) R.color.lightGrey else R.color.colorSurface,
                itemView.context.theme
            )
        )
        name.text = objects[position].name
        try {
            icon.setImageResource(WebsiteFactory.getIcon(objects[position].icon)?:R.drawable.other)
        } catch (e: Exception) {
            Log.d("WebsiteArrayAdapterLog", "Exception while setting icon: ${e::class.java.simpleName}(\"${e.message}\")")
        }
        return itemView
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selected = position
    }

}