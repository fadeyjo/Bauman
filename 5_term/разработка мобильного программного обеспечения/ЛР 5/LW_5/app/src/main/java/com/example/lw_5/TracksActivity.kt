package com.example.lw_5

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
class TracksActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
    private var minDuration: Long = 0
    private var maxDuration: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracks)
        minDuration = intent.getLongExtra("MIN_DURATION", 0)
        maxDuration = intent.getLongExtra("MAX_DURATION", Long.MAX_VALUE)
        if (checkPermission()) {
            loadTracks()
        } else {
            requestPermission()
        }
    }
    private fun getPermission(): String {
        return if (android.os.Build.VERSION.SDK_INT < 33) {
            Manifest.permission.READ_EXTERNAL_STORAGE
        } else {
            Manifest.permission.READ_MEDIA_AUDIO
        }
    }
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            getPermission()
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(getPermission()),
            PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions,
            grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED) {
            loadTracks()
        }
    }
    private fun loadTracks() {
        val tracks = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.DURATION} BETWEEN ? AND ?"
        val selectionArgs = arrayOf(minDuration.toString(),
            maxDuration.toString())
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            while (cursor.moveToNext()) {
                val artist = cursor.getString(artistColumn)
                val title = cursor.getString(titleColumn)
                tracks.add("$artist - $title")
            }
        }
        val listView = findViewById<ListView>(R.id.tracksListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            tracks)
        listView.adapter = adapter
        if (tracks.isEmpty()) {
            Toast.makeText(this, "No tracks found in the specified duration range", Toast.LENGTH_SHORT).show()
        }
    }
}