package com.example.lw_4

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private var laptops: MutableList<Laptop> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        this.loadData()
        this.renderLaptopListViews()

        val newLaptopButton = findViewById<Button>(R.id.newLaptopButton)
        newLaptopButton.setOnClickListener {
            this.newLaptop()
        }

        val deleteLaptopButton = findViewById<Button>(R.id.deleteLaptopButton)
        deleteLaptopButton.setOnClickListener {
            this.deleteLaptop()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun newLaptop() {
        val intent = Intent(this, NewLaptop::class.java)
        startActivity(intent)
    }

    private fun deleteLaptop() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.delete_laptop, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Введите ID удаляемого ноутбука")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                val deletableID = dialogView.findViewById<EditText>(R.id.deletableID)
                val ID = deletableID.text.toString()
                if (!Regex("^[0-9]+$").matches(ID)) {
                    Toast.makeText(this, "Неккоректный ввод", Toast.LENGTH_SHORT).show()
                }
                else {
                    val intID = ID.toInt()
                    if (this.db.getIDs().contains(intID)) {
                        this.db.deleteLaptopById(intID)
                        this.refreshLocalData()
                        this.renderLaptopListViews()
                    }
                    else {
                        Toast.makeText(
                            this,
                            "Такого ноутбука не существует",
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

    private fun refreshLocalData() {
        this.laptops = mutableListOf()
        this.loadData()
    }

    private fun renderLaptopListViews() {
        val laptopsListViews = findViewById<ListView>(R.id.laptopsListViews)
        val laptopsItems: MutableList<TableItem> = mutableListOf()

        for (i in 0..this.laptops.size - 1) {
            laptopsItems.add(TableItem(
                this.laptops[i].ID,
                this.laptops[i].manufacturerName,
                this.laptops[i].HDDVolume,
                this.laptops[i].SSDPresent,
                this.laptops[i].RAMVolume,
                this.laptops[i].isFHD,
                this.laptops[i].screenTime
            ))
        }
        val adapter = TableAdapter(this, laptopsItems)
        laptopsListViews.adapter = adapter
    }

    private fun loadData() {
        this.db = DBHelper(this, null)
        this.laptops = this.db.getLaptops()
    }
}