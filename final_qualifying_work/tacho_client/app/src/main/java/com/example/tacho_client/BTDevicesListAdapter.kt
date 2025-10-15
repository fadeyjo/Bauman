package com.example.tacho_client

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class BTDevicesListAdapter(
    private val context: Context,
    private val devices: MutableList<BluetoothDevice>
) : BaseAdapter() {

    override fun getCount(): Int = devices.size

    override fun getItem(position: Int): BluetoothDevice = devices[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.bt_device_list_item, parent, false)

        val device = getItem(position)

        val nameTextView = view.findViewById<TextView>(R.id.device_name_text_view)
        val macTextView = view.findViewById<TextView>(R.id.device_mac_address_text_view)

        nameTextView.text = device.name ?: "Неизвестное устройство"
        macTextView.text = device.address

        return view
    }

    fun addDevice(newDevice: BluetoothDevice) {
        if (devices.any { it.address == newDevice.address }) {
            val existingDeviceIndex = devices.indexOfFirst { it.address == newDevice.address }
            if (existingDeviceIndex == -1) return

            devices[existingDeviceIndex] = newDevice
            notifyDataSetChanged()

            return
        }
        devices.add(newDevice)
        notifyDataSetChanged()
    }

    fun clearAll() {
        devices.clear()
        notifyDataSetChanged()
    }

}
