package com.example.lw_2

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var animationRotate: Animation
    private lateinit var animationFadeOut: Animation
    private lateinit var animationCompose: Animation
    private val myTag = "MyLog"

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            imageView = findViewById(R.id.imageView)
            registerForContextMenu(imageView)

            animationRotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
            animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            animationCompose = AnimationUtils.loadAnimation(this, R.anim.compose)
        }
        catch (e: Exception) {
            Log.e(myTag, "Select Ilya")
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.rotate -> {
                Toast.makeText(this, "Select rotation", Toast.LENGTH_SHORT).show()
                Log.i(myTag, "Select rotation")
                imageView.startAnimation(animationRotate)
            }
            R.id.fade_out -> {
                Log.i(myTag, "Select fade out")
                Toast.makeText(this, "Select fade out", Toast.LENGTH_SHORT).show()
                imageView.startAnimation(animationFadeOut)
            }
            R.id.compose -> {
                Log.i(myTag, "Select compose")
                Toast.makeText(this, "Select compose", Toast.LENGTH_SHORT).show()
                imageView.startAnimation(animationCompose)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Choose image")
        menu.add(0, v.id, 0, "Ilya")
        menu.add(0, v.id, 0, "Artem")
        menu.add(0, v.id, 0, "Roma")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title === "Ilya") {
            Log.i(myTag, "Select Ilya")
            Toast.makeText(this, "Select Ilya", Toast.LENGTH_SHORT).show()
            imageView.setImageResource(R.drawable.ilya)
        } else if (item.title === "Artem") {
            Log.i(myTag, "Select Artem")
            Toast.makeText(this, "Select Artem", Toast.LENGTH_SHORT).show()
            imageView.setImageResource(R.drawable.artem)
        } else if (item.title === "Roma") {
            Log.i(myTag, "Select Roma")
            Toast.makeText(this, "Select Roma", Toast.LENGTH_SHORT).show()
            imageView.setImageResource(R.drawable.rome)
        }
        return super.onContextItemSelected(item)
    }
}