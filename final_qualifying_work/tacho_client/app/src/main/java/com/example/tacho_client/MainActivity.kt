package com.example.tacho_client

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.collections.mutableListOf

class MainActivity : AppCompatActivity() {

    // Локальное хранение ключ-значение для сохранения целевого устройства Bluetooth
    private lateinit var prefs: SharedPreferences

    // Элементы activity
    private lateinit var tachographConnectionStateTextView: TextView
    private lateinit var EBMConnectionStateTextView: TextView
    private lateinit var mainConnectionButton: TextView

    // Bluetooth
    private lateinit var BTManager: BluetoothManager
    private var BTAdapter: BluetoothAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initActivityComponents()

        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        if (checkPermissionsAreGranted()) {
            initBT()
            return
        }

        requestPermissions(1001)
    }

    private fun initActivityComponents() {
        tachographConnectionStateTextView = findViewById(R.id.tachograph_connection_state_text_view)
        EBMConnectionStateTextView = findViewById(R.id.ebm_connection_state_text_view)

        mainConnectionButton = findViewById(R.id.main_connection_button)
        mainConnectionButton.setOnClickListener {
            mainConnectButtonClicked()
        }
    }

    private fun mainConnectButtonClicked() {
        if (!checkPermissionsAreGranted()) {
            requestPermissions(1002)
            return
        }

        connectToDevice()
    }

    private fun connectToDevice() {
        // Пытаемся взять целевое устройство из локального хранилища
        val savedAddress = prefs.getString("bt_device_address", null)

        // Если целевое устройство найдено
        if (savedAddress != null) {

            return
        }

        // Если целевое устройство не найдено
        val intent = Intent(this, BTConnectionActivity::class.java)
        startActivity(intent)
    }

    private fun initBT() {
        if (BTAdapter != null) return

        BTManager = getSystemService(BluetoothManager::class.java)
        BTAdapter = BTManager.adapter

        if (BTAdapter != null) return

        showDialog(
            "Внимание",
            "Ваше устройство не поддерживаает технологию Bluetooth. " +
                    "Некоторый функционал недоступен."
        )

        // Выключаем функции, если Bluetooth не поддерживается на устройстве
        mainConnectionButton.isEnabled = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        if (!allGranted) {
            showDialog(
                "Внимание",
                "Вы выдали не все разрешения. " +
                        "Некоторый функционал недоступен. " +
                        "Для того, чтобы выдать разрешения, откройте настройки приложения."
            )

            return
        }

        when (requestCode) {
            1001 -> initBT()
            1002 -> connectToDevice()
        }
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

    private fun checkPermissionsAreGranted(): Boolean {
        val scanPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        if (!scanPermission) return false

        val connectPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        if (!connectPermission) return false

        val locationPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return locationPermission
    }

    private fun getNotGrantedPermissions(activity: Activity): List<String> {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED
        ) permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)

        return permissionsToRequest
    }

    private fun requestPermissions(requestCode: Int) {
        val permissionsToRequest = getNotGrantedPermissions(this)

        if (permissionsToRequest.isNotEmpty())
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), requestCode)
    }

}
