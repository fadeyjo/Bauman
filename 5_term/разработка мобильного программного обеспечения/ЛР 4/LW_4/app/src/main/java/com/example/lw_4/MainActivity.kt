package com.example.lw_4

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.OutputStream

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun writeSortedLaptopList(prop: String, typeSort: String, sortedList: MutableList<Laptop>) {
        val filename = "sort.txt"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, filename)
        if (file.exists()) {
            file.delete()
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents")
        }

        val uri: Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

        uri?.let { uriValue ->
            val outputStream: OutputStream? = contentResolver.openOutputStream(uriValue)
            outputStream?.use { stream ->
                stream.write("Сортировка (${prop}) по ${if (typeSort == "ASC") "возрастанию" else "убыванию"} объёма HDD.\n".toByteArray())
                stream.write("--------------------------------------------------\n".toByteArray())
                for (laptop in sortedList) {
                    stream.write("ID:                      \t${laptop.ID}\n".toByteArray())
                    stream.write("Производитель:           \t${laptop.manufacturerName}\n".toByteArray())
                    stream.write("Объем HDD:               \t${laptop.HDDVolume} ГБ\n".toByteArray())
                    stream.write("Наличие SSD:             \t${if (laptop.SSDPresent) "Да" else "нет"}\n".toByteArray())
                    stream.write("Объём оперативной памяти:\t${laptop.RAMVolume} ГБ\n".toByteArray())
                    stream.write("Наличие FHD:             \t${if (laptop.isFHD) "Да" else "Нет"}\n".toByteArray())
                    stream.write("Время автономной работы: \t${laptop.screenTime} часов\n".toByteArray())
                    stream.write("--------------------------------------------------\n".toByteArray())
                }
                stream.flush()
            }
        }
    }

    private fun writeAVGGroupBy(prop: String, laptops: MutableList<Laptop>) {
        val filename = "AVGGroupBy.txt"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, filename)
        if (file.exists()) {
            file.delete()
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents")
        }

        val uri: Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

        uri?.let { uriValue ->
            val outputStream: OutputStream? = contentResolver.openOutputStream(uriValue)
            outputStream?.use { stream ->
                stream.write("Средние значения (группировка ${prop})\n".toByteArray())
                stream.write("--------------------------------------------------\n".toByteArray())
                if (prop == "Производитель") {
                    for (laptop in laptops) {
                        stream.write("Производитель:           \t${laptop.manufacturerName}\n".toByteArray())
                        stream.write("Объем HDD:               \t${laptop.HDDVolume} ГБ\n".toByteArray())
                        stream.write("Объём оперативной памяти:\t${laptop.RAMVolume} ГБ\n".toByteArray())
                        stream.write("Время автономной работы: \t${laptop.screenTime} часов\n".toByteArray())
                        stream.write("--------------------------------------------------\n".toByteArray())
                    }
                }
                else if (prop == "Наличие SSD") {
                    for (laptop in laptops) {
                        stream.write("Наличие SSD:             \t${if (laptop.SSDPresent) "Да" else "Нет"}\n".toByteArray())
                        stream.write("Объем HDD:               \t${laptop.HDDVolume} ГБ\n".toByteArray())
                        stream.write("Объём оперативной памяти:\t${laptop.RAMVolume} ГБ\n".toByteArray())
                        stream.write("Время автономной работы: \t${laptop.screenTime} часов\n".toByteArray())
                        stream.write("--------------------------------------------------\n".toByteArray())
                    }
                }
                else if (prop == "Наличие FULL HD") {
                    for (laptop in laptops) {
                        stream.write("Наличие FULL HD:         \t${if (laptop.isFHD) "Да" else "Нет"}\n".toByteArray())
                        stream.write("Объем HDD:               \t${laptop.HDDVolume} ГБ\n".toByteArray())
                        stream.write("Объём оперативной памяти:\t${laptop.RAMVolume} ГБ\n".toByteArray())
                        stream.write("Время автономной работы: \t${laptop.screenTime} часов\n".toByteArray())
                        stream.write("--------------------------------------------------\n".toByteArray())
                    }
                }
                else {
                    for (laptop in laptops) {
                        stream.write("Объем HDD:               \t${laptop.HDDVolume} ГБ\n".toByteArray())
                        stream.write("Объём оперативной памяти:\t${laptop.RAMVolume} ГБ\n".toByteArray())
                        stream.write("Время автономной работы: \t${laptop.screenTime} часов\n".toByteArray())
                        stream.write("--------------------------------------------------\n".toByteArray())
                    }
                }
                stream.flush()
            }
        }
    }

    private fun writeSum(sum: Int, prop: String) {
        val filename = "sum.txt"
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, filename)
        if (file.exists()) {
            file.delete()
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents")
        }

        val uri: Uri? = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)

        uri?.let { uriValue ->
            val outputStream: OutputStream? = contentResolver.openOutputStream(uriValue)
            outputStream?.use { stream ->
                stream.write("Сумма значений (${prop}) = ${sum}".toByteArray())
                stream.flush()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort -> {
                val dialogView: View = LayoutInflater.from(this).inflate(R.layout.sort_option, null)
                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.sortRadioGroup)

                val spinner = dialogView.findViewById<Spinner>(R.id.prop)
                val props = arrayOf("Объём HDD", "Объём RAM", "Время автономной работы")
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Выберите тип сортировки")
                    .setView(dialogView)
                    .setPositiveButton("OK") { _, _ ->
                        val selectedId = radioGroup.checkedRadioButtonId
                        if (selectedId == -1) {
                            Toast.makeText(this, "Выберите тип сортировки", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            val prop = spinner.selectedItem.toString()
                            val typeSort = dialogView.findViewById<RadioButton>(selectedId).text.toString()

                            val sortedList: MutableList<Laptop> = this.db.getSortedList(prop, typeSort)
                            this.writeSortedLaptopList(prop, typeSort, sortedList)
                            Log.i("MainActivity", "Список записан в файл sort.txt")
                            Toast.makeText(this, "Список записан в файл sort.txt", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

                builder.create().show()
                true
            }

            R.id.double_group -> {
                val dialogView: View = LayoutInflater.from(this).inflate(R.layout.double_group_option, null)
                val spinner1 = dialogView.findViewById<Spinner>(R.id.prop1)
                val spinner2 = dialogView.findViewById<Spinner>(R.id.prop2)
                val props = arrayOf(
                    "Производитель",
                    "Объём HDD",
                    "Наличие SSD",
                    "Объём RAM",
                    "Наличие FULL HD",
                    "Время автономной работы"
                )
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner1.adapter = adapter
                spinner2.adapter = adapter

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Выберите тип сортировки")
                    .setView(dialogView)
                    .setPositiveButton("OK") { _, _ ->
                        val option1 = spinner1.selectedItem.toString()
                        val option2 = spinner2.selectedItem.toString()
                        if (option1 != option2) {
                            val laptopsDoubleGroup = this.db.doubleGroup(option1, option2)
                            val intent = Intent(this, double_group::class.java)
                            intent.putParcelableArrayListExtra(
                                "laptops",
                                ArrayList(laptopsDoubleGroup)
                            )
                            intent.putExtra("col1", option1)
                            intent.putExtra("col2", option2)
                            startActivity(intent)
                        }
                        else {
                            Toast.makeText(this, "Выберите разные категории", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

                builder.create().show()
                true
            }

            R.id.RAM_sum -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.sum_option, null)
                val spinner = dialogView.findViewById<Spinner>(R.id.prop)

                val props = arrayOf("Объём HDD", "Объём RAM", "Время автономной работы")

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Выберите поле")
                dialogBuilder.setView(dialogView)

                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    val prop = spinner.selectedItem.toString()
                    val sum = this.db.getSum(prop)
                    this.writeSum(sum, prop)
                    Log.i("MainActivity", "Вычеслнена сумма значений ${prop} = ${sum}")
                    Toast.makeText(this, "Сумма записана в файл sum.txt", Toast.LENGTH_SHORT).show()
                }

                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
                true
            }

            R.id.AVG_group_option -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.avg_group_option, null)
                val spinner = dialogView.findViewById<Spinner>(R.id.prop)

                val props = arrayOf(
                    "Производитель",
                    "Объём HDD",
                    "Наличие SSD",
                    "Объём RAM",
                    "Наличие FULL HD",
                    "Время автономной работы"
                )

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Группировать по")
                dialogBuilder.setView(dialogView)

                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    val prop = spinner.selectedItem.toString()
                    val groupedList: MutableList<Laptop> = this.db.groupBy(prop)
                    this.writeAVGGroupBy(prop, groupedList)
                    Toast.makeText(
                        this,
                        "Средние значение сгруппированных по полям выведены в файл AVGGroupBy.txt",
                        Toast.LENGTH_SHORT).show()
                    Log.i("MainActivity", "Средние значение сгруппированных по полям выведены в файл AVGGroupBy.txt")
                    val intent = Intent(this, avg_group::class.java)
                    intent.putExtra("prop", prop)
                    intent.putParcelableArrayListExtra("laptops", ArrayList(groupedList))
                    startActivity(intent)
                }

                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
                true
            }

            R.id.laptop_max_value -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.laptop_max_value_option, null)
                val spinner = dialogView.findViewById<Spinner>(R.id.prop)

                val props = arrayOf(
                    "Объём HDD",
                    "Объём RAM",
                    "Время автономной работы"
                )

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Поле")
                dialogBuilder.setView(dialogView)

                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    val prop = spinner.selectedItem.toString()
                    val laptopsWithMaxValues: MutableList<Laptop> = this.db.laptopsWithMaxValue(prop)
                    var out = "--------------------------------------\n"
                    for (laptop in laptopsWithMaxValues) {
                        out += "ID: ${laptop.ID}\n" +
                                "HDD volume: ${laptop.HDDVolume}\n" +
                                "SSD present: ${laptop.SSDPresent}\n" +
                                "RAM volume: ${laptop.RAMVolume}\n" +
                                "Is FHD: ${laptop.isFHD}\n" +
                                "Screen time: ${laptop.screenTime}\n" +
                                "--------------------------------------\n"
                    }
                    Log.i("MainActivity", "Максимальное значение по ${prop}\n" + out)
                }

                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
                true
            }

            R.id.value_greater_than -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.value_greater_than_option, null)
                val spinner = dialogView.findViewById<Spinner>(R.id.prop)
                val valueMax = dialogView.findViewById<EditText>(R.id.valueMax)

                val props = arrayOf(
                    "Объём HDD",
                    "Объём RAM",
                    "Время автономной работы"
                )

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Поле и значение")
                dialogBuilder.setView(dialogView)

                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    val prop = spinner.selectedItem.toString()
                    if (Regex("^[0-9]+$").matches(valueMax.text.toString())) {
                        val laptopsValueGreaterThan: MutableList<Laptop> = this.db.laptopsValueGreaterThan(prop, valueMax.text.toString().toInt())
                        val intent = Intent(this, value_greater_than::class.java)
                        intent.putParcelableArrayListExtra("laptops", ArrayList(laptopsValueGreaterThan))
                        startActivity(intent)
                        var out = "--------------------------------------\n"
                        for (laptop in laptopsValueGreaterThan) {
                            out += "ID: ${laptop.ID}\n" +
                                    "HDD volume: ${laptop.HDDVolume}\n" +
                                    "SSD present: ${laptop.SSDPresent}\n" +
                                    "RAM volume: ${laptop.RAMVolume}\n" +
                                    "Is FHD: ${laptop.isFHD}\n" +
                                    "Screen time: ${laptop.screenTime}\n" +
                                    "--------------------------------------\n"
                        }
                        Log.i("MainActivity", "Ноутбуки, где ${prop} > ${valueMax.text}\n" + out)
                    }
                }

                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
                true
            }

            R.id.lower_than_AVG -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.lower_than_avg_option, null)
                val spinner = dialogView.findViewById<Spinner>(R.id.prop)

                val props = arrayOf(
                    "Объём HDD",
                    "Объём RAM",
                    "Время автономной работы"
                )

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Поле и значение")
                dialogBuilder.setView(dialogView)

                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    val prop = spinner.selectedItem.toString()
                    val laptopsValueLowerAVG: MutableList<Laptop> = this.db.laptopsValueLowerAVG(prop)


                    val intent = Intent(this, lower_than_AVG::class.java)
                    intent.putParcelableArrayListExtra("laptops", ArrayList(laptopsValueLowerAVG))
                    startActivity(intent)
                    var out = "--------------------------------------\n"
                    for (laptop in laptopsValueLowerAVG) {
                        out += "ID: ${laptop.ID}\n" +
                                "HDD volume: ${laptop.HDDVolume}\n" +
                                "SSD present: ${laptop.SSDPresent}\n" +
                                "RAM volume: ${laptop.RAMVolume}\n" +
                                "Is FHD: ${laptop.isFHD}\n" +
                                "Screen time: ${laptop.screenTime}\n" +
                                "--------------------------------------\n"
                    }
                    Log.i("MainActivity", "Ноутбуки, где значение ${prop} < среднего\n" + out)
                }

                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
                true
            }

            R.id.lower_than_AVG_one -> {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.value_greater_than_option, null)
                val spinner = dialogView.findViewById<Spinner>(R.id.prop)
                val valueMax = dialogView.findViewById<EditText>(R.id.valueMax)

                val props = arrayOf(
                    "Объём HDD",
                    "Объём RAM",
                    "Время автономной работы"
                )

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, props)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spinner.adapter = adapter

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Поле и значение")
                dialogBuilder.setView(dialogView)

                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    val prop = spinner.selectedItem.toString()
                    if (Regex("^[0-9]+$").matches(valueMax.text.toString())) {
                        val laptopGreterThan: Laptop = this.db.laptopsValueGreaterThanOne(prop, valueMax.text.toString().toInt())
                        var out = "ID: ${laptopGreterThan.ID}\n" +
                                "HDD volume: ${laptopGreterThan.HDDVolume}\n" +
                                "SSD present: ${laptopGreterThan.SSDPresent}\n" +
                                "RAM volume: ${laptopGreterThan.RAMVolume}\n" +
                                "Is FHD: ${laptopGreterThan.isFHD}\n" +
                                "Screen time: ${laptopGreterThan.screenTime}\n" +
                                "--------------------------------------\n"
                        Log.i("MainActivity", "Ноутбук (1), где ${prop} > ${valueMax.text}\n" + out)
                    }
                }

                dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                dialogBuilder.create().show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}