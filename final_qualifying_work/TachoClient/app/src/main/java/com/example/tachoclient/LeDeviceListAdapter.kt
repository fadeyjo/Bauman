import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class LeDeviceListAdapter(private val context: Context) : BaseAdapter() {

    private val devices: MutableList<BluetoothDevice> = mutableListOf()
    private val names: MutableList<String> = mutableListOf()

    fun addDevice(device: BluetoothDevice, name: String?) {
        if (!devices.contains(device)) {
            devices.add(device)
            names.add(name ?: "Unknown")
        }
    }

    fun clear() {
        devices.clear()
    }

    override fun getCount(): Int = devices.size
    override fun getItem(position: Int): BluetoothDevice = devices[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)

        text1.text = names[position]
        text2.text = devices[position].address

        return view
    }
}
