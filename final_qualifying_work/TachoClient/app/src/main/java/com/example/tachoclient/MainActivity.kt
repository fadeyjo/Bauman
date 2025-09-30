package com.example.tachoclient

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.size
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private val enableBtLauncherToOpenBLEConnectionActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (BTAdapter?.isEnabled == true) {
            startActivity(Intent(this, BLEConnectionActivity::class.java))
        } else {
            AlertDialog.Builder(this)
                .setTitle("Внимание")
                .setMessage("Bluetooth необходим для работы приложения")
                .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private val REQUEST_CODE_BLUETOOTH = 1001
    private var BLEIsAvailable: Boolean = false
    private var allPermissionsGranted: Boolean = false
    private lateinit var BTManager: BluetoothManager
    private var BTAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        BLEIsAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        if (!BLEIsAvailable){
            showBLEIsNotAvailable()
            invalidateOptionsMenu()
            return
        }

        allPermissionsGranted = checkIfPermissionsGranted()

        if (allPermissionsGranted){
            BTManager = getSystemService(BluetoothManager::class.java)
            BTAdapter = BTManager.adapter
            if (BTAdapter != null) {
                return
            }

            showBLEIsNotAvailable()
            BLEIsAvailable = false
            invalidateOptionsMenu()
            return
        }

        checkAndRequestPermissions()
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

    private fun checkIfPermissionsGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != REQUEST_CODE_BLUETOOTH) {
            return
        }

        allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        if (!allPermissionsGranted){
            showPermissionsNotGranted()
            invalidateOptionsMenu()
            return
        }
        BTManager = getSystemService(BluetoothManager::class.java)
        BTAdapter = BTManager.adapter
        if (BTAdapter != null) {
            return
        }

        showBLEIsNotAvailable()
        BLEIsAvailable = false
        invalidateOptionsMenu()
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            for (i in 0 until it.size) {
                val menuItem = it[i]
                menuItem.isEnabled = BLEIsAvailable == true && allPermissionsGranted == true
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), REQUEST_CODE_BLUETOOTH)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_ble_state -> {
                return true
            }
            R.id.menu_ble_connect -> {
                if (!BLEIsAvailable){
                    showBLEIsNotAvailable()
                    return true
                }

                if (!allPermissionsGranted){
                    showPermissionsNotGranted()
                    return true
                }

                try {
                    if (BTAdapter?.isEnabled == false) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        enableBtLauncherToOpenBLEConnectionActivity.launch(enableBtIntent)
                        return true
                    }
                } catch (e: SecurityException) {
                    showPermissionsNotGranted()
                    checkAndRequestPermissions()
                    return true
                }

                startActivity(Intent(this, BLEConnectionActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}