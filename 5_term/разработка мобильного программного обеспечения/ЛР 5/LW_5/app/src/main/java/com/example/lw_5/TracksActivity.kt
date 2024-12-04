package com.example.lw_5

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
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
        minDuration = intent.getLongExtra("from", 0)
        maxDuration = intent.getLongExtra("to", Long.MAX_VALUE)
        if (havePermission()) {
            perTracks()
        } else {
            reqPermission()
        }
    }
    private fun getPermission(): String {
        return if (android.os.Build.VERSION.SDK_INT < 33) {
            Manifest.permission.READ_EXTERNAL_STORAGE
        } else {
            Manifest.permission.READ_MEDIA_AUDIO
        }
    }
    private fun havePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            getPermission()
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun reqPermission() {
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
            perTracks()
        }
    }
    private fun perTracks() {
        val tracks = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION
        )
        val query = "${MediaStore.Audio.Media.DURATION} BETWEEN ? AND ?"
        val args = arrayOf(minDuration.toString(),
            maxDuration.toString())
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            query,
            args,
            null
        )?.use { cursor ->
            val artistCol =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val titleCol =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            while (cursor.moveToNext()) {
                val artist = cursor.getString(artistCol)
                val title = cursor.getString(titleCol)
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