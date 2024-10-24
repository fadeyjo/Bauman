package com.example.lw_5_client

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TableAdapter(private val context: Context, private val items: List<TableItem>) :
    ArrayAdapter<TableItem>(context, R.layout.list_item_table, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_table, parent, false)

        view.findViewById<TextView>(R.id.val1).text = item.id.toString()
        view.findViewById<TextView>(R.id.val2).text = item.author
        view.findViewById<TextView>(R.id.val3).text = item.title
        view.findViewById<TextView>(R.id.val4).text = item.duration.toString()

        return view
    }
}