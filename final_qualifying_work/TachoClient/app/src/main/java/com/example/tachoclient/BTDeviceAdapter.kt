package com.example.tachoclient

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class BTDeviceAdapter(
    private val context: Context,
    private val devices: List<BluetoothDevice>
) : BaseAdapter() {

    var connectingDeviceAddress: String? = null
    var connectedDeviceAddress: String? = null

    override fun getCount(): Int = devices.size

    override fun getItem(position: Int): BluetoothDevice = devices[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.bt_device_item, parent, false)

        val device = getItem(position)

        val nameTextView = view.findViewById<TextView>(R.id.device_name)
        val addressTextView = view.findViewById<TextView>(R.id.device_address)

        try {
            nameTextView.text = device.name ?: "Неизвестное устройство"
        } catch (e: SecurityException) {
            nameTextView.text = "Неизвестное устройство"
        }

        when (device.address) {
            connectingDeviceAddress -> addressTextView.text = "Подключение..."
            connectedDeviceAddress -> addressTextView.text = "Подключено"
            else -> addressTextView.text = device.address
        }

        return view
    }
}


