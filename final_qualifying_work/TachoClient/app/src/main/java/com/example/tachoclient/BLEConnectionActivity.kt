package com.example.tachoclient

import LeDeviceListAdapter
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class BLEConnectionActivity : AppCompatActivity() {

    private lateinit var searchedDevicesAdapter: LeDeviceListAdapter
    private lateinit var BTManager: BluetoothManager
    private var BTAdapter: BluetoothAdapter? = null
    private lateinit var BLEStateTextView: TextView
    private lateinit var pairedDevicesListView: ListView
    private lateinit var searchedDevicesListView: ListView
    private var scanning = false
    private val handler = Handler()
    private lateinit var BLEScaner: BluetoothLeScanner

    private val SCAN_PERIOD: Long = 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ble_connection)

        val toolbar: Toolbar = findViewById(R.id.toolbar_ble)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        BLEStateTextView = findViewById(R.id.BLE_state_text_view)
        pairedDevicesListView = findViewById(R.id.paired_devices_list_view)
        searchedDevicesListView = findViewById(R.id.searched_devices_list_view)

        searchedDevicesAdapter = LeDeviceListAdapter(this)
        searchedDevicesListView.adapter = searchedDevicesAdapter

        BTManager = getSystemService(BluetoothManager::class.java) as BluetoothManager
        BTAdapter = BTManager.adapter
        if (BTAdapter == null)
        {
            showBLEIsNotAvailable()
            finish()
            return
        }

        if (BTAdapter?.isEnabled == true) {
            BLEStateTextView.text = getString(R.string.BLE_enabled)
            loadDevices()
            return
        }

        BLEStateTextView.text = getString(R.string.BLE_disabled)
    }

    private fun loadDevices(){
        scanSearchedDevices()
    }

    private fun scanSearchedDevices(){
        try{
            BLEScaner = BTAdapter!!.bluetoothLeScanner

            if (!scanning) {
                handler.postDelayed({
                    scanning = false
                    BLEScaner.stopScan(leScanCallback)
                }, SCAN_PERIOD)
                scanning = true
                BLEScaner.startScan(leScanCallback)
            } else {
                scanning = false
                BLEScaner.stopScan(leScanCallback)
            }
        } catch (e: SecurityException) {
            BLEScaner.stopScan(leScanCallback)
            showPermissionsNotGranted()
            finish()
        }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            try{
                val device = result.device
                val scanRecord = result.scanRecord
                val deviceName = device.name ?: scanRecord?.deviceName ?: "Unknown"

                searchedDevicesAdapter.addDevice(device, deviceName)
                searchedDevicesAdapter.notifyDataSetChanged()
            } catch (e: SecurityException) {
                BLEScaner.stopScan(leScanCallback)
                showPermissionsNotGranted()
                finish()
            }
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
}
