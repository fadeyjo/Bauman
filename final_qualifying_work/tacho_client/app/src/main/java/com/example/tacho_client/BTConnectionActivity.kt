package com.example.tacho_client

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tacho_client.bluetooth.BluetoothService

class BTConnectionActivity : AppCompatActivity() {

    // Адаптер для работы со списокм
    private lateinit var adapter: BTDevicesListAdapter

    // Компоненты Activity
    private lateinit var BTDevicesListView: ListView
    private lateinit var BTStateTextView: TextView
    private lateinit var researchDevicesImageButton: ImageButton

    // Bluetooth
    private lateinit var BTManager: BluetoothManager
    private var BTAdapter: BluetoothAdapter? = null
    private lateinit var BTService: BluetoothService

    // Найденные устройста Bluetooth
    private val devices: MutableList<BluetoothDevice> = mutableListOf()

    // Работа с Bluetooth
    private val BTReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            if (action == null) {
                runOnUiThread {
                    showDialog(
                        "Ошибка",
                        "Неизвестная команда обработки при работе с Bluetooth"
                    )
                    finish()
                }

                return
            }

            when(action) {
                BluetoothDevice.ACTION_FOUND,
                BluetoothDevice.ACTION_NAME_CHANGED -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    if (device == null) {
                        runOnUiThread {
                            showDialog(
                                "Ошибка",
                                "Получен null  в качестве устройства Bluetooth"
                            )
                            finish()
                        }

                        return
                    }

                    adapter.addDevice(device)
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_ON -> setBTState()
                        BluetoothAdapter.STATE_OFF -> setBTState()
                    }
                }
            }
        }
    }

    // Для подключения BT
    companion object {
        private const val IDENTIFICATION_COMMAND = "ID\n"
        private const val IDENTIFICATION_PREFIX = "ESP32_TACHO"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bt_connection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initActivityComponents()

        if (!initBT()) return

        registerBTReceiver()

        if (!BTAdapter!!.isEnabled)
            showDialog(
                "Внимание",
                "Для обнаружения устройств включите Bluetooth"
            )

        setBTState()
    }

    private fun setBTState() {
        if (!BTAdapter!!.isEnabled) {
            researchDevicesImageButton.isEnabled = false
            BTStateTextView.text = getString(R.string.bt_off_text)
            if (BTAdapter!!.isDiscovering) BTAdapter!!.cancelDiscovery()
            adapter.clearAll()
            return
        }

        researchDevicesImageButton.isEnabled = true
        BTStateTextView.text = getString(R.string.bt_on_text)
        if (BTAdapter!!.isDiscovering) BTAdapter!!.cancelDiscovery()
        adapter.clearAll()
        Handler(Looper.getMainLooper()).postDelayed({
            BTAdapter!!.startDiscovery()
        }, 500)
    }

    private fun registerBTReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_NAME_CHANGED)
        }
        registerReceiver(BTReceiver, filter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun BTOnConnected(device: BluetoothDevice) {  }

    private fun BTOnConnectionFailed(device: BluetoothDevice, error: String) {  }

    private fun BTOnMessageReceived(device: BluetoothDevice, message: String) {  }

    private fun initActivityComponents() {
        val toolbar = findViewById<Toolbar>(R.id.bt_connection_activity_tool_bar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        BTDevicesListView = findViewById(R.id.bt_devices_list_view)
        adapter = BTDevicesListAdapter(this, devices)
        BTDevicesListView.adapter = adapter
        BTDevicesListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val device = adapter.getItem(position)

            BTService = BluetoothService(BTAdapter!!, ::BTOnConnected, ::BTOnConnectionFailed, ::BTOnMessageReceived)
            BTService.connect(device)
            BTService.send(IDENTIFICATION_COMMAND)
        }

        BTStateTextView = findViewById(R.id.bt_state_text_view)

        researchDevicesImageButton = findViewById(R.id.research_devices_image_button)
        researchDevicesImageButton.setOnClickListener {
            setBTState()
        }
    }

    private fun initBT(): Boolean {
        if (BTAdapter != null) return true

        BTManager = getSystemService(BluetoothManager::class.java)
        BTAdapter = BTManager.adapter

        if (BTAdapter != null) return true

        showDialog(
            "Внимание",
            "Ваше устройство не поддерживаает технологию Bluetooth. " +
                    "Некоторый функционал недоступен."
        )

        finish()

        return false
    }

    private fun showDialog(headerText: String, messageText: String) {
        AlertDialog.Builder(this)
            .setTitle(headerText)
            .setMessage(messageText)
            .setCancelable(false)
            .setPositiveButton("Ок") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (BTAdapter!!.isDiscovering) BTAdapter!!.cancelDiscovery()
        // Удаление приёмника команд для работы с Bluetooth
        unregisterReceiver(BTReceiver)
    }

}
