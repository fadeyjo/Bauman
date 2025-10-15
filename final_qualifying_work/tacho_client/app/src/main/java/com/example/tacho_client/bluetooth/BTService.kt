package com.example.tacho_client.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService(
    private val adapter: BluetoothAdapter,
    private val onConnected: (BluetoothDevice) -> Unit,
    private val onConnectionFailed: (BluetoothDevice, String) -> Unit,
    private val onMessageReceived: (BluetoothDevice, String) -> Unit
) {

    private val pendingCommands: MutableList<Command> = mutableListOf()

    companion object {
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null

    fun connect(device: BluetoothDevice) {
        connectThread?.cancel()
        connectThread = ConnectThread(device).apply { start() }
    }

    fun disconnect() {
        connectThread?.cancel()
        connectedThread?.cancel()
    }

    fun send(data: String) {
        connectedThread?.write(data.toByteArray())
    }

    // -----------------------
    // Поток подключения
    // -----------------------
    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {
        private val socket: BluetoothSocket? by lazy {
            try { device.createRfcommSocketToServiceRecord(SPP_UUID) }
            catch (e: IOException) { null }
        }

        override fun run() {
            adapter.cancelDiscovery()
            socket?.let { sock ->
                try {
                    sock.connect()
                    onConnected(device)
                    connectedThread = ConnectedThread(sock).apply { start() }
                } catch (e: IOException) {
                    onConnectionFailed(device, e.message ?: "Ошибка подключения")
                    try { sock.close() } catch (_: IOException) {}
                }
            }
        }

        fun cancel() {
            try { socket?.close() } catch (_: IOException) {}
        }
    }

    // -----------------------
    // Поток обмена данными
    // -----------------------
    private inner class ConnectedThread(private val socket: BluetoothSocket) : Thread() {
        private val inStream: InputStream? = try { socket.inputStream } catch (_: IOException) { null }
        private val outStream: OutputStream? = try { socket.outputStream } catch (_: IOException) { null }

        private var running = true

        override fun run() {
            val buffer = ByteArray(1024)
            val sb = StringBuilder()

            while (running) {
                try {
                    val bytes = inStream?.read(buffer) ?: -1
                    if (bytes > 0) {
                        val text = String(buffer, 0, bytes)
                        sb.append(text)
                        if (text.contains("\n")) {
                            onMessageReceived(socket.remoteDevice, sb.toString().trim())
                            sb.clear()
                        }
                    }
                } catch (e: IOException) {
                    running = false
                    onConnectionFailed(socket.remoteDevice, "Потеряно соединение")
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                outStream?.write(bytes)
                outStream?.flush()
            } catch (e: IOException) {
                onConnectionFailed(socket.remoteDevice, "Ошибка отправки")
            }
        }

        fun cancel() {
            running = false
            try { socket.close() } catch (_: IOException) {}
        }
    }

    private data class Command(
        val id: Int,
        val text: String,
        val callback: (String) -> Unit
    )

}
