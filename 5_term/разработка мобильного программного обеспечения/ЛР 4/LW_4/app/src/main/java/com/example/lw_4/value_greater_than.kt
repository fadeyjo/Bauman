package com.example.lw_4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class value_greater_than : AppCompatActivity() {
    private lateinit var laptops: MutableList<Laptop>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_value_greater_than)

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
}