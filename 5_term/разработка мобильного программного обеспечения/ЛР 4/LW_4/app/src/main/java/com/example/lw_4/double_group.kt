package com.example.lw_4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class double_group : AppCompatActivity() {
    private lateinit var laptops: MutableList<Laptop>
    private lateinit var col1: String
    private lateinit var col2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_double_group)

        this.laptops = intent.getParcelableArrayListExtra<Laptop>("laptops") as ArrayList<Laptop>

        var buf: String? = intent.getStringExtra("col1")
        if (buf != null) {
            this.col1 = buf
        }

        buf = intent.getStringExtra("col2")
        if (buf != null) {
            this.col2 = buf
        }

        this.renderLaptopListViews()

        val mainButton = findViewById<Button>(R.id.mainButton)
        mainButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun renderLaptopListViews() {
        val laptopsListViews = findViewById<ListView>(R.id.laptopsListViews)
        val laptopsItems: MutableList<TableGroupItem> = mutableListOf()

        for (i in 0..this.laptops.size - 1) {
            var opt1 = ""
            if (this.col1 == "Производитель") {
                opt1 = laptops[i].manufacturerName
            }
            else if (col1 == "Объём HDD") {
                opt1 = laptops[i].HDDVolume.toString()
            }
            else if (col1 == "Наличие SSD") {
                opt1 = laptops[i].SSDPresent.toString()
            }
            else if (col1 == "Объём RAM") {
                opt1 = laptops[i].RAMVolume.toString()
            }
            else if (col1 == "Наличие FULL HD") {
                opt1 = laptops[i].isFHD.toString()
            }
            else {
                opt1 = laptops[i].screenTime.toString()
            }

            var opt2 = ""
            if (this.col2 == "Производитель") {
                opt2 = laptops[i].manufacturerName
            }
            else if (col2 == "Объём HDD") {
                opt2 = laptops[i].HDDVolume.toString()
            }
            else if (col2 == "Наличие SSD") {
                opt2 = laptops[i].SSDPresent.toString()
            }
            else if (col2 == "Объём RAM") {
                opt2 = laptops[i].RAMVolume.toString()
            }
            else if (col2 == "Наличие FULL HD") {
                opt2 = laptops[i].isFHD.toString()
            }
            else {
                opt2 = laptops[i].screenTime.toString()
            }


            laptopsItems.add(TableGroupItem(
                this.col1,
                this.col2,
                opt1,
                opt2,
                this.laptops[i].count
            ))
        }
        val adapter = TableGroupAdapter(this, laptopsItems)
        laptopsListViews.adapter = adapter
    }
}