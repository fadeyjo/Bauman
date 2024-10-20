package com.example.lw_4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class avg_group : AppCompatActivity() {
    private lateinit var laptops: MutableList<Laptop>
    private lateinit var prop: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_avg_group)

        val prop = intent.getStringExtra("prop")
        if (prop != null) {
            this.prop = prop
        }
        this.laptops = intent.getParcelableArrayListExtra<Laptop>("laptops") as ArrayList<Laptop>

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
        val laptopsListViews = findViewById<ListView>(R.id.listView)

        if (this.prop == "Производитель") {
            val laptopsItems: MutableList<TableAVGSomeItem> = mutableListOf()
            for (i in 0..this.laptops.size - 1) {
                laptopsItems.add(
                    TableAVGSomeItem(
                    "Производитель",
                    this.laptops[i].manufacturerName,
                    this.laptops[i].HDDVolume,
                    this.laptops[i].RAMVolume,
                    this.laptops[i].screenTime
                ))
            }
            val adapter = TableAVGSomeAdapter(this, laptopsItems)
            laptopsListViews.adapter = adapter
        }
        else if (this.prop == "Наличие SSD") {
            val laptopsItems: MutableList<TableAVGSomeItem> = mutableListOf()
            for (i in 0..this.laptops.size - 1) {
                laptopsItems.add(
                    TableAVGSomeItem(
                        "Наличие SSD",
                        this.laptops[i].SSDPresent.toString(),
                        this.laptops[i].HDDVolume,
                        this.laptops[i].RAMVolume,
                        this.laptops[i].screenTime
                    ))
            }
            val adapter = TableAVGSomeAdapter(this, laptopsItems)
            laptopsListViews.adapter = adapter
        }
        else if (this.prop == "Наличие FULL HD") {
            val laptopsItems: MutableList<TableAVGSomeItem> = mutableListOf()
            for (i in 0..this.laptops.size - 1) {
                laptopsItems.add(
                    TableAVGSomeItem(
                        "Наличие FULL HD",
                        this.laptops[i].isFHD.toString(),
                        this.laptops[i].HDDVolume,
                        this.laptops[i].RAMVolume,
                        this.laptops[i].screenTime
                    ))
            }
            val adapter = TableAVGSomeAdapter(this, laptopsItems)
            laptopsListViews.adapter = adapter
        }
        else {
            val laptopsItems: MutableList<TableAVGItem> = mutableListOf()
            for (i in 0..this.laptops.size - 1) {
                laptopsItems.add(
                    TableAVGItem(
                        this.laptops[i].HDDVolume,
                        this.laptops[i].RAMVolume,
                        this.laptops[i].screenTime
                    )
                )
            }
            val adapter = TableAVGAdapter(this, laptopsItems)
            laptopsListViews.adapter = adapter
        }
    }
}