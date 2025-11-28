package com.example.lw_4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TableAVGAdapter(private val context: Context, private val items: List<TableAVGItem>) :
    ArrayAdapter<TableAVGItem>(context, R.layout.list_item_avg_table, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_avg_table, parent, false)

        val val1: TextView = view.findViewById(R.id.val1)
        val val2: TextView = view.findViewById(R.id.val2)
        val val3: TextView = view.findViewById(R.id.val3)

        val1.text = item.HDDVolume.toString()
        val2.text = item.RAMVolume.toString()
        val3.text = item.screenTime.toString()

        return view
    }
}