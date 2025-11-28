package com.example.lw_4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast

class TableAdapter(private val context: Context, private val items: List<TableItem>) :
    ArrayAdapter<TableItem>(context, R.layout.list_item_table, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_table, parent, false)

        val val1: TextView = view.findViewById(R.id.val1)
        val val2: TextView = view.findViewById(R.id.val2)
        val val3: TextView = view.findViewById(R.id.val3)
        val val4: TextView = view.findViewById(R.id.val4)
        val val5: TextView = view.findViewById(R.id.val5)
        val val6: TextView = view.findViewById(R.id.val6)
        val val7: TextView = view.findViewById(R.id.val7)

        val1.text = item.ID.toString()
        val2.text = item.manufacturerName
        val3.text = item.HDDVolume.toString()
        val4.text = if (item.SSDPresent) "Да" else "Нет"
        val5.text = item.RAMVolume.toString()
        val6.text = if (item.isFHD) "Да" else "Нет"
        val7.text = item.screenTime.toString()

        return view
    }
}