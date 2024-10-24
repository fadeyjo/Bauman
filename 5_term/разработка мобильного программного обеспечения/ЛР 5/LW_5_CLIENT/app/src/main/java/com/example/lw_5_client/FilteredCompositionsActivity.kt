package com.example.lw_5_client

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FilteredCompositionsActivity : AppCompatActivity() {
    private var fromMinutes: Int = 0
    private var fromSeconds: Int = 0
    private var toMinutes: Int = 0
    private var toSeconds: Int = 0
    private val CONTENT_URI: Uri = Uri.parse("content://com.example.lw_5_source.provider/compositions")
    private val compositions = mutableListOf<Composition>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filtered_compositions)

        this.fromMinutes = intent.getIntExtra("fromMinutes", -1)
        this.fromSeconds = intent.getIntExtra("fromSeconds", -1)
        this.toMinutes = intent.getIntExtra("toMinutes", -1)
        this.toSeconds = intent.getIntExtra("toSeconds", -1)

        this.loadData(this)
        this.renderData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadData(context: Context) {
        val contentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor == null) {
            Toast.makeText(context, "Не удалось получить данные", Toast.LENGTH_SHORT).show()
            return
        }

        cursor.use {
            val idColIndex = it.getColumnIndexOrThrow("id")
            val authorColIndex = it.getColumnIndexOrThrow("author")
            val titleColIndex = it.getColumnIndexOrThrow("title")
            val durationColIndex = it.getColumnIndexOrThrow("duration")

            while (it.moveToNext()) {
                val id = it.getInt(idColIndex)
                val author = it.getString(authorColIndex)
                val title = it.getString(titleColIndex)
                val duration = it.getInt(durationColIndex)

                this.compositions.add(Composition(id, author, title, duration))
            }
        }
    }

    private fun renderData() {
        val items: MutableList<TableItem> = mutableListOf()

        for (i in 0..this.compositions.size - 1) {
            items.add(TableItem(
                this.compositions[i].id,
                this.compositions[i].author,
                this.compositions[i].title,
                this.compositions[i].duration
            ))
        }
        val adapter = TableAdapter(this, items)
        findViewById<ListView>(R.id.listView).adapter = adapter
    }
}