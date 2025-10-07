package com.example.tachoclient

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.IOException
import java.util.UUID

class BTConnectionActivity : AppCompatActivity() {

    private lateinit var BTManager: BluetoothManager
    private var BTAdapter: BluetoothAdapter? = null
    private lateinit var BTStateTextView: TextView
    private lateinit var refreshButton: ImageButton
    private lateinit var pairedDevicesListView: ListView
    private lateinit var searchedDevicesListView: ListView
    private lateinit var pairedDeviceAdapter: BTDeviceAdapter
    private var pairedDevices = mutableListOf<BluetoothDevice>()
    private val searchedDevices = mutableListOf<BluetoothDevice>()
    private lateinit var searchedDeviceAdapter: BTDeviceAdapter
    private var isConnecting = false
    private var connectThread: ConnectThread? = null
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bt_connection)

        val toolbar: Toolbar = findViewById(R.id.toolbar_BT)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        refreshButton = findViewById(R.id.refresh_button)
        BTStateTextView = findViewById(R.id.BT_state_text_view)
        pairedDevicesListView = findViewById(R.id.paired_devices_list_view)
        searchedDevicesListView = findViewById(R.id.searched_devices_list_view)

        pairedDeviceAdapter = BTDeviceAdapter(this, pairedDevices)
        pairedDevicesListView.adapter = pairedDeviceAdapter
        searchedDeviceAdapter = BTDeviceAdapter(this, searchedDevices)
        searchedDevicesListView.adapter = searchedDeviceAdapter

        pairedDevicesListView.setOnItemClickListener { _, _, position, _ ->
            val device = pairedDevices[position]
            onDeviceSelected(device)
        }

        searchedDevicesListView.setOnItemClickListener { _, _, position, _ ->
            val device = searchedDevices[position]
            onDeviceSelected(device)
        }

        BTManager = getSystemService(BluetoothManager::class.java) as BluetoothManager
        BTAdapter = BTManager.adapter
        if (BTAdapter == null) {
            showBLEIsNotAvailable()
            finish()
            return
        }

        refreshButton.setOnClickListener {
            restartBluetoothScanning()
        }

        val BTFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, BTFilter)

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_NAME_CHANGED)
        }
        registerReceiver(receiver, filter)

        if (BTAdapter?.isEnabled == true) {
            BTStateTextView.text = getString(R.string.BT_enabled)
            refreshButton.isEnabled = true
            getPairedDevices()
            try {
                BTAdapter?.startDiscovery()
            }
            catch (e: SecurityException) {
                showPermissionsNotGranted()
                finish()
            }

            return
        }

        refreshButton.isEnabled = false
        BTStateTextView.text = getString(R.string.BT_disabled)
    }

    private fun onDeviceSelected(device: BluetoothDevice) {
        if (isConnecting) return

        connectThread

        isConnecting = true
        connectThread = ConnectThread(device)
        connectThread?.start()

        pairedDeviceAdapter.connectingDeviceAddress = device.address
        searchedDeviceAdapter.connectingDeviceAddress = device.address
        pairedDeviceAdapter.notifyDataSetChanged()
        searchedDeviceAdapter.notifyDataSetChanged()
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BTAdapter == null) {
                showBLEIsNotAvailable()
                finish()
                return
            }

            when (intent?.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> {
                    BTStateTextView.text = getString(R.string.BT_disabled)

                    pairedDevices.clear()
                    searchedDevices.clear()
                    pairedDeviceAdapter.notifyDataSetChanged()
                    searchedDeviceAdapter.notifyDataSetChanged()

                    refreshButton.isEnabled = false

                    try {
                        cancelBTDiscovering()
                    }
                    catch (e: SecurityException) {
                        showPermissionsNotGranted()
                        finish()
                    }
                }
                BluetoothAdapter.STATE_ON -> {
                    BTStateTextView.text = getString(R.string.BT_enabled)

                    pairedDevices.clear()
                    searchedDevices.clear()
                    pairedDeviceAdapter.notifyDataSetChanged()
                    searchedDeviceAdapter.notifyDataSetChanged()

                    getPairedDevices()
                    refreshButton.isEnabled = true

                    try {
                        cancelBTDiscovering()

                        BTAdapter?.startDiscovery()
                    }
                    catch (e: SecurityException) {
                        showPermissionsNotGranted()
                        finish()
                    }
                }
            }
        }
    }

    private fun cancelBTDiscovering() {
        try {
            if (BTAdapter?.isDiscovering == true) {
                BTAdapter?.cancelDiscovery()
            }
        }
        catch (e: SecurityException) {
            showPermissionsNotGranted()
            finish()
            return
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            try {
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        device?.let {
                            val searchedAddresses = searchedDevices.map { it -> it.address }.toSet()
                            val pairedAddresses = pairedDevices.map { it -> it.address }.toSet()
                            if (!(searchedAddresses.contains(it.address) || pairedAddresses.contains(it.address))) {
                                searchedDevices.add(it)
                                searchedDeviceAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    BluetoothDevice.ACTION_NAME_CHANGED -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            val indexSearched = searchedDevices.indexOfFirst { d -> d.address == it.address }
                            if (indexSearched >= 0) {
                                searchedDevices[indexSearched] = it
                                searchedDeviceAdapter.notifyDataSetChanged()
                            }
                            val indexPaired = pairedDevices.indexOfFirst { d -> d.address == it.address }
                            if (indexPaired >= 0) {
                                pairedDevices[indexPaired] = it
                                pairedDeviceAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            } catch (e: SecurityException) {
                showPermissionsNotGranted()
                finish()
            }
        }
    }

    private fun restartBluetoothScanning() {
        try {
            cancelBTDiscovering()
            pairedDevices.clear()
            searchedDevices.clear()
            pairedDeviceAdapter.notifyDataSetChanged()
            searchedDeviceAdapter.notifyDataSetChanged()
            getPairedDevices()
            BTAdapter?.startDiscovery()
        } catch (e: SecurityException) {
            showPermissionsNotGranted()
        }
    }

    private fun getPairedDevices() {
        try {
            val bonded = BTAdapter?.bondedDevices ?: emptySet()
            pairedDevices.clear()
            pairedDevices.addAll(bonded)
            pairedDeviceAdapter.notifyDataSetChanged()
        }
        catch (e: SecurityException) {
            showPermissionsNotGranted()
            finish()
        }
    }

    private fun showPermissionsNotGranted(){
        AlertDialog.Builder(this)
            .setTitle("Внимание")
            .setMessage("Вы не предоставили все разрешения для полноценной работы " +
                    "приложения. " +
                    "Некоторые функции недоступны. " +
                    "Для корректной работы приложения предоставьте все возможные разрешения " +
                    "в настройках приложения.")
            .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showBLEIsNotAvailable(){
        AlertDialog.Builder(this)
            .setTitle("Внимание")
            .setMessage("BLE недоступно на этом устройстве")
            .setPositiveButton("ОК") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        unregisterReceiver(bluetoothStateReceiver)
        cancelBTDiscovering()
    }

    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            cancelBTDiscovering()

            try {
                mmSocket?.connect()

                runOnUiThread {
                    isConnecting = false

                    val index = searchedDevices.indexOfFirst { it.address == device.address }
                    if (index >= 0) {
                        searchedDevices.removeAt(index)
                    }

                    if (!pairedDevices.none { it.address == device.address }) {
                        val indexInPaired = pairedDevices.indexOfFirst { it.address == device.address }
                        pairedDevices.removeAt(indexInPaired)
                    }

                    pairedDevices.add(0, device)

                    pairedDeviceAdapter.connectingDeviceAddress = null
                    pairedDeviceAdapter.connectedDeviceAddress = device.address
                    searchedDeviceAdapter.connectingDeviceAddress = null
                    searchedDeviceAdapter.connectedDeviceAddress = null

                    pairedDeviceAdapter.notifyDataSetChanged()
                    searchedDeviceAdapter.notifyDataSetChanged()
                }

            } catch (e: IOException) {
                Log.e("BTConnection", "Ошибка подключения", e)
                runOnUiThread {
                    isConnecting = false
                    pairedDeviceAdapter.connectingDeviceAddress = null
                    searchedDeviceAdapter.connectingDeviceAddress = null
                    pairedDeviceAdapter.notifyDataSetChanged()
                    searchedDeviceAdapter.notifyDataSetChanged()
                }

                try {
                    mmSocket?.close()
                } catch (_: IOException) { }
            }
        }

        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e("BTConnection", "Could not close the client socket", e)
            }
        }
    }

}
