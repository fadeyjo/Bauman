package com.example.lw_5_source

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var compositions: MutableList<Composition>
    private val db = CompositionDBHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        this.loadData()
        this.renderData()

        findViewById<Button>(R.id.addComposition).setOnClickListener {
            startActivity(Intent(this, AddNewCompositionActivity::class.java))
        }

        findViewById<Button>(R.id.deleteComposition).setOnClickListener {
            this.openDialogWindowToDeleteComposition()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadData() {
        this.compositions = this.db.getAllCompositions()
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

    private fun openDialogWindowToDeleteComposition() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.delete_composition_dialog_window, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Введите id удаляемой композиции")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                val stringId = dialogView.findViewById<EditText>(R.id.deletableId).text.toString()
                if (stringId == "") {
                    Toast.makeText(this, "Неккоректный ввод", Toast.LENGTH_SHORT).show()
                }
                else {
                    val ids: MutableList<Int> = this.db.getIds()
                    val intId = stringId.toInt()
                    if (ids.contains(intId)) {
                        this.db.deleteComposition(intId)
                    }
                    else {
                        Toast.makeText(
                            this,
                            "Записи с таким id не существует",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.cancel()
            }
        dialogBuilder.show()
    }
}