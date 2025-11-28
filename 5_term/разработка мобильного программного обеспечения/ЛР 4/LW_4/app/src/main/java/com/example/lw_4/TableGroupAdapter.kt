package com.example.lw_4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class TableGroupAdapter(private val context: Context, private val items: List<TableGroupItem>) :
    ArrayAdapter<TableGroupItem>(context, R.layout.list_item_group_table, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)!!
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_group_table, parent, false)

        val val1: TextView = view.findViewById(R.id.val1)
        val val2: TextView = view.findViewById(R.id.val2)
        val val3: TextView = view.findViewById(R.id.val3)

        val prop1: TextView = view.findViewById(R.id.prop1)
        val prop2: TextView = view.findViewById(R.id.prop2)

        if (item.col1 == "Производитель") {
            prop1.text = "Производитель"
            val1.text = item.value1
        }
        else if(item.col1 == "Объём HDD") {
            prop1.text = "Объём HDD"
            val1.text = item.value1
        }
        else if(item.col1 == "Наличие SSD") {
            prop1.text = "Наличие SSD"
            val1.text = if (item.value1 == "true") "Да" else "Нет"
        }
        else if(item.col1 == "Объём RAM") {
            prop1.text = "Объём ОП"
            val1.text = item.value1
        }
        else if(item.col1 == "Наличие FUL HD") {
            prop1.text = "Наличие FUL HD"
            val1.text = if (item.value1 == "true") "Да" else "Нет"
        }
        else {
            prop1.text = "Время автономной работы"
            val1.text = item.value1
        }

        if (item.col2 == "Производитель") {
            prop2.text = "Производитель"
            val2.text = item.value2
        }
        else if(item.col2 == "Объём HDD") {
            prop2.text = "Объём HDD"
            val2.text = item.value2
        }
        else if(item.col2 == "Наличие SSD") {
            prop2.text = "Наличие SSD"
            val2.text = if (item.value2 == "true") "Да" else "Нет"
        }
        else if(item.col2 == "Объём RAM") {
            prop2.text = "Объём ОП"
            val2.text = item.value2
        }
        else if(item.col2 == "Наличие FULL HD") {
            prop2.text = "Наличие FHD"
            val2.text = if (item.value2 == "true") "Да" else "Нет"
        }
        else {
            prop2.text = "Время автономной работы"
            val2.text = item.value2
        }

        val3.text = item.count.toString()

        return view
    }
}