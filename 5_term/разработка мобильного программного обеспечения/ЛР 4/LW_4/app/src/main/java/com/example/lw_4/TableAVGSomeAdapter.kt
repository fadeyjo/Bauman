package com.example.lw_4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TableAVGSomeAdapter(private val context: Context, private val items: List<TableAVGSomeItem>) :
    ArrayAdapter<TableAVGSomeItem>(context, R.layout.list_item_avg_some_table, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_avg_some_table, parent, false)

        val val1: TextView = view.findViewById(R.id.val1)
        val val2: TextView = view.findViewById(R.id.val2)
        val val3: TextView = view.findViewById(R.id.val3)
        val val4: TextView = view.findViewById(R.id.val4)
        val prop4: TextView = view.findViewById(R.id.prop4)

        val1.text = item.HDDVolume.toString()
        val2.text = item.RAMVolume.toString()
        val3.text = item.screenTime.toString()

        if (item.prop == "Производитель") {
            prop4.text = "Производитель"
            val4.text = item.value
        }
        else if (item.prop == "Наличие SSD") {
            prop4.text = "Наличие SSD"
            val4.text = if (item.value == "true") "Да" else "Нет"
        }
        else {
            prop4.text = "HULL HD"
            val4.text = if (item.value == "true") "Да" else "Нет"
        }

        return view
    }
}