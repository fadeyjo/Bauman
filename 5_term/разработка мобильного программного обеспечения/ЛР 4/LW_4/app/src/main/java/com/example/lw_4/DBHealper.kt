package com.example.lw_4

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                MANUFACTURE_NAME_COL + " VARCHAR(30) NOT NULL, " +
                HDD_VOLUME_COL + " INTEGER NOT NULL, " +
                SSD_PRESENT_COL + " BOOLEAN NOT NULL, " +
                RAM_VOLUME_COL + " INTEGER NOT NULL, " +
                IS_FHD_COL + " BOOLEAN NOT NULL, " +
                SCREEN_TIME + " INTEGER NOT NULL);")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun getSortedList(prop: String, typeSort: String): MutableList<Laptop> {
        var col = ""
        if (prop == "Объём HDD") {
            col = HDD_VOLUME_COL
        }
        else if (prop == "Объём RAM") {
            col = RAM_VOLUME_COL
        }
        else {
            col = SCREEN_TIME
        }

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY ${col} $typeSort", null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return mutableListOf()
        }

        val laptops: MutableList<Laptop> = mutableListOf()

        var ID = 0
        var manufacturerName = ""
        var HDDVolume = 0
        var SSDPresent = 0
        var RAMVolume = 0
        var isFHD = 0
        var screenTime = 0
        do {
            var columnIndex = cursor.getColumnIndex(DBHelper.ID_COL)
            ID = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.MANUFACTURE_NAME_COL)
            manufacturerName = cursor.getString(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.HDD_VOLUME_COL)
            HDDVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SSD_PRESENT_COL)
            SSDPresent = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.RAM_VOLUME_COL)
            RAMVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.IS_FHD_COL)
            isFHD = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SCREEN_TIME)
            screenTime = cursor.getInt(columnIndex)
            laptops.add(Laptop(
                ID,
                manufacturerName,
                HDDVolume,
                if (SSDPresent == 1) true else false,
                RAMVolume,
                if (isFHD == 1) true else false, screenTime)
            )
        } while (cursor.moveToNext())

        cursor.close()

        return laptops
    }

    fun addLaptop(
        manufacturerName: String,
        HDDVolume: Int, SSDPresent: Boolean,
        RAMVolume: Int,
        isFHD: Boolean,
        screenTime: Int
    ){
        val values = ContentValues()
        values.put(MANUFACTURE_NAME_COL, manufacturerName)
        values.put(HDD_VOLUME_COL, HDDVolume)
        values.put(SSD_PRESENT_COL, SSDPresent)
        values.put(RAM_VOLUME_COL, RAMVolume)
        values.put(IS_FHD_COL, isFHD)
        values.put(SCREEN_TIME, screenTime)

        val db = this.writableDatabase

        db.insert(TABLE_NAME, null, values)

        db.close()
    }

    fun deleteLaptopById(id: Int) {
        val db = this.writableDatabase
        val whereClause = "id = ?"
        val whereArgs = arrayOf(id.toString())

        db.delete(TABLE_NAME, whereClause, whereArgs)

        db.close()
    }

    fun getLaptops(): MutableList<Laptop> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return mutableListOf()
        }

        val laptops: MutableList<Laptop> = mutableListOf()

        var ID = 0
        var manufacturerName = ""
        var HDDVolume = 0
        var SSDPresent = 0
        var RAMVolume = 0
        var isFHD = 0
        var screenTime = 0
        do {
            var columnIndex = cursor.getColumnIndex(DBHelper.ID_COL)
            ID = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.MANUFACTURE_NAME_COL)
            manufacturerName = cursor.getString(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.HDD_VOLUME_COL)
            HDDVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SSD_PRESENT_COL)
            SSDPresent = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.RAM_VOLUME_COL)
            RAMVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.IS_FHD_COL)
            isFHD = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SCREEN_TIME)
            screenTime = cursor.getInt(columnIndex)
            laptops.add(Laptop(
                ID,
                manufacturerName,
                HDDVolume,
                if (SSDPresent == 1) true else false,
                RAMVolume,
                if (isFHD == 1) true else false, screenTime)
            )
        } while (cursor.moveToNext())

        cursor.close()

        return laptops
    }

    fun getIDs(): List<Int> {
        val db = this.readableDatabase
        val idList = mutableListOf<Int>()
        val cursor = db.rawQuery("SELECT id FROM " + TABLE_NAME, null)
        val index = cursor.getColumnIndex(ID_COL)
        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(index)
                idList.add(ID)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return idList
    }

    fun getSum(prop: String): Int {
        var col = ""
        if (prop == "Объём HDD") {
            col = HDD_VOLUME_COL
        }
        else if (prop == "Объём RAM") {
            col = RAM_VOLUME_COL
        }
        else {
            col = SCREEN_TIME
        }

        val db = this.readableDatabase
        var total = 0
        val cursor = db.rawQuery("SELECT SUM(${col}) AS total FROM ${TABLE_NAME}", null)
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex("total")
            total = cursor.getInt(index)
        }
        cursor.close()
        return total
    }

    fun doubleGroup(option1: String, option2: String): MutableList<Laptop> {
       var col1 = ""
       if (option1 == "Производитель") {
           col1 = MANUFACTURE_NAME_COL
       }
       else if (option1 == "Объём HDD") {
           col1 = HDD_VOLUME_COL
       }
       else if (option1 == "Наличие SSD") {
           col1 = SSD_PRESENT_COL
       }
       else if (option1 == "Объём RAM") {
           col1 = RAM_VOLUME_COL
       }
       else if (option1 == "Наличие FULL HD") {
           col1 = IS_FHD_COL
       }
       else {
           col1 = SCREEN_TIME
       }

       var col2 = ""
       if (option2 == "Производитель") {
           col2 = MANUFACTURE_NAME_COL
       }
       else if (option2 == "Объём HDD") {
           col2 = HDD_VOLUME_COL
       }
       else if (option2 == "Наличие SSD") {
           col2 = SSD_PRESENT_COL
       }
       else if (option2 == "Объём RAM") {
           col2 = RAM_VOLUME_COL
       }
       else if (option2 == "Наличие FULL HD") {
           col2 = IS_FHD_COL
       }
       else {
           col2 = SCREEN_TIME
       }

       val db = this.readableDatabase
       val cursor = db.rawQuery("SELECT ${col1}, ${col2}, COUNT(*) as CountLines FROM ${TABLE_NAME} GROUP BY ${col1}, ${col2}", null)

       if (!cursor.moveToFirst()) {
           cursor.close()
           return mutableListOf()
       }

       val laptops: MutableList<Laptop> = mutableListOf()

       var columnIndex = 0
       var laptop = Laptop()

       do {
           laptop = Laptop()

           columnIndex = cursor.getColumnIndex(col1)
           if (col1 == MANUFACTURE_NAME_COL) {
               laptop.manufacturerName = cursor.getString(columnIndex)
           }
           else if (col1 == HDD_VOLUME_COL) {
               laptop.HDDVolume = cursor.getInt(columnIndex)
           }
           else if (col1 == SSD_PRESENT_COL) {
               val baf = cursor.getInt(columnIndex)
               laptop.SSDPresent = if (baf == 1) true else false
           }
           else if (col1 == RAM_VOLUME_COL) {
               laptop.RAMVolume = cursor.getInt(columnIndex)
           }
           else if (col1 == IS_FHD_COL) {
               val baf = cursor.getInt(columnIndex)
               laptop.isFHD = if (baf == 1) true else false
           }
           else {
               laptop.screenTime = cursor.getInt(columnIndex)
           }

           columnIndex = cursor.getColumnIndex(col2)
           if (col2 == MANUFACTURE_NAME_COL) {
               laptop.manufacturerName = cursor.getString(columnIndex)
           }
           else if (col2 == HDD_VOLUME_COL) {
               laptop.HDDVolume = cursor.getInt(columnIndex)
           }
           else if (col2 == SSD_PRESENT_COL) {
               val baf = cursor.getInt(columnIndex)
               laptop.SSDPresent = if (baf == 1) true else false
           }
           else if (col2 == RAM_VOLUME_COL) {
               laptop.RAMVolume = cursor.getInt(columnIndex)
           }
           else if (col2 == IS_FHD_COL) {
               val baf = cursor.getInt(columnIndex)
               laptop.isFHD = if (baf == 1) true else false
           }
           else {
               laptop.screenTime = cursor.getInt(columnIndex)
           }

           columnIndex = cursor.getColumnIndex("CountLines")

           laptop.count = cursor.getInt(columnIndex)
           laptops.add(laptop)
       } while (cursor.moveToNext())

       cursor.close()

       return laptops
    }

    fun groupBy(prop: String): MutableList<Laptop> {
        var col = ""
        val cols: MutableList<String> = mutableListOf(
            HDD_VOLUME_COL,
            RAM_VOLUME_COL,
            SCREEN_TIME
        )
        if (prop == "Производитель") {
            col = MANUFACTURE_NAME_COL
        }
        else if (prop == "Объём HDD") {
            col = HDD_VOLUME_COL
        }
        else if (prop == "Наличие SSD") {
            col = SSD_PRESENT_COL
        }
        else if (prop == "Объём RAM") {
            col = RAM_VOLUME_COL
        }
        else if (prop == "Наличие FULL HD") {
            col = IS_FHD_COL
        }
        else {
            col = SCREEN_TIME
        }

        val db = this.readableDatabase

        val cursor: Cursor

        if (cols.contains(col)) {
            cursor = db.rawQuery(
                "SELECT " +
                        "AVG(${cols[0]}) as ${cols[0]}, " +
                        "AVG(${cols[1]}) as ${cols[1]}, " +
                        "AVG(${cols[2]}) as ${cols[2]} " +
                        "FROM $TABLE_NAME " +
                        "GROUP BY $col",
                null)
        }
        else {
            cursor = db.rawQuery(
                "SELECT " +
                        "$col, " +
                        "AVG(${cols[0]}) as ${cols[0]}, " +
                        "AVG(${cols[1]}) as ${cols[1]}, " +
                        "AVG(${cols[2]}) as ${cols[2]} " +
                        "FROM $TABLE_NAME " +
                        "GROUP BY $col",
                null)
        }


        if (!cursor.moveToFirst()) {
            cursor.close()
            return mutableListOf()
        }

        val laptops: MutableList<Laptop> =  mutableListOf()

        var HDDVolume = 0
        var RAMVolume = 0
        var screenTime = 0

        var laptop: Laptop
        var columnIndex = 0
        if (cols.contains(col)) {
            do {
                columnIndex = cursor.getColumnIndex(HDD_VOLUME_COL)
                HDDVolume = cursor.getInt(columnIndex)
                columnIndex = cursor.getColumnIndex(RAM_VOLUME_COL)
                RAMVolume = cursor.getInt(columnIndex)
                columnIndex = cursor.getColumnIndex(SCREEN_TIME)
                screenTime = cursor.getInt(columnIndex)
                laptop = Laptop()
                laptop.HDDVolume = HDDVolume
                laptop.RAMVolume = RAMVolume
                laptop.screenTime = screenTime
                laptops.add(laptop)
            } while (cursor.moveToNext())
        }
        else {
            var someString = ""
            var someBoolean = 0
            var isBoolean = false
            if (col != MANUFACTURE_NAME_COL) {
                isBoolean = true
            }
            do {
                columnIndex = cursor.getColumnIndex(HDD_VOLUME_COL)
                HDDVolume = cursor.getInt(columnIndex)
                columnIndex = cursor.getColumnIndex(RAM_VOLUME_COL)
                RAMVolume = cursor.getInt(columnIndex)
                columnIndex = cursor.getColumnIndex(SCREEN_TIME)
                screenTime = cursor.getInt(columnIndex)
                columnIndex = cursor.getColumnIndex(col)
                if (isBoolean) {
                    someBoolean = cursor.getInt(columnIndex)
                }
                else {
                    someString = cursor.getString(columnIndex)
                }
                laptop = Laptop()
                laptop.HDDVolume = HDDVolume
                laptop.RAMVolume = RAMVolume
                laptop.screenTime = screenTime
                if (col == SSD_PRESENT_COL) {
                    laptop.SSDPresent = if (someBoolean == 1) true else false
                }
                else if (col == IS_FHD_COL) {
                    laptop.isFHD = if (someBoolean == 1) true else false
                }
                else {
                    laptop.manufacturerName = someString
                }
                laptops.add(laptop)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return laptops
    }

    fun laptopsWithMaxValue(prop: String): MutableList<Laptop> {
        var col = ""
        if (prop == "Объём HDD") {
            col = HDD_VOLUME_COL
        }
        else if (prop == "Объём RAM") {
            col = RAM_VOLUME_COL
        }
        else {
            col = SCREEN_TIME
        }

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TABLE_NAME} WHERE ${col} = (SELECT MAX(${col}) FROM ${TABLE_NAME})", null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return mutableListOf()
        }

        val laptops: MutableList<Laptop> = mutableListOf()

        var ID = 0
        var manufacturerName = ""
        var HDDVolume = 0
        var SSDPresent = 0
        var RAMVolume = 0
        var isFHD = 0
        var screenTime = 0
        do {
            var columnIndex = cursor.getColumnIndex(DBHelper.ID_COL)
            ID = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.MANUFACTURE_NAME_COL)
            manufacturerName = cursor.getString(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.HDD_VOLUME_COL)
            HDDVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SSD_PRESENT_COL)
            SSDPresent = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.RAM_VOLUME_COL)
            RAMVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.IS_FHD_COL)
            isFHD = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SCREEN_TIME)
            screenTime = cursor.getInt(columnIndex)
            laptops.add(Laptop(
                ID,
                manufacturerName,
                HDDVolume,
                if (SSDPresent == 1) true else false,
                RAMVolume,
                if (isFHD == 1) true else false, screenTime)
            )
        } while (cursor.moveToNext())

        cursor.close()

        return laptops
    }

    fun laptopsValueGreaterThan(prop: String, value: Int): MutableList<Laptop> {
        var col = ""
        if (prop == "Объём HDD") {
            col = HDD_VOLUME_COL
        }
        else if (prop == "Объём RAM") {
            col = RAM_VOLUME_COL
        }
        else {
            col = SCREEN_TIME
        }

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TABLE_NAME} WHERE ${col} > ${value}", null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return mutableListOf()
        }

        val laptops: MutableList<Laptop> = mutableListOf()

        var ID = 0
        var manufacturerName = ""
        var HDDVolume = 0
        var SSDPresent = 0
        var RAMVolume = 0
        var isFHD = 0
        var screenTime = 0
        do {
            var columnIndex = cursor.getColumnIndex(DBHelper.ID_COL)
            ID = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.MANUFACTURE_NAME_COL)
            manufacturerName = cursor.getString(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.HDD_VOLUME_COL)
            HDDVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SSD_PRESENT_COL)
            SSDPresent = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.RAM_VOLUME_COL)
            RAMVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.IS_FHD_COL)
            isFHD = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SCREEN_TIME)
            screenTime = cursor.getInt(columnIndex)
            laptops.add(Laptop(
                ID,
                manufacturerName,
                HDDVolume,
                if (SSDPresent == 1) true else false,
                RAMVolume,
                if (isFHD == 1) true else false, screenTime)
            )
        } while (cursor.moveToNext())

        cursor.close()

        return laptops
    }

    fun laptopsValueGreaterThanOne(prop: String, value: Int): Laptop {
        var col = ""
        if (prop == "Объём HDD") {
            col = HDD_VOLUME_COL
        }
        else if (prop == "Объём RAM") {
            col = RAM_VOLUME_COL
        }
        else {
            col = SCREEN_TIME
        }

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TABLE_NAME} WHERE ${col} > ${value}", null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return Laptop()
        }

        var ID = 0
        var manufacturerName = ""
        var HDDVolume = 0
        var SSDPresent = 0
        var RAMVolume = 0
        var isFHD = 0
        var screenTime = 0

        var columnIndex = cursor.getColumnIndex(DBHelper.ID_COL)
        ID = cursor.getInt(columnIndex)
        columnIndex = cursor.getColumnIndex(DBHelper.MANUFACTURE_NAME_COL)
        manufacturerName = cursor.getString(columnIndex)
        columnIndex = cursor.getColumnIndex(DBHelper.HDD_VOLUME_COL)
        HDDVolume = cursor.getInt(columnIndex)
        columnIndex = cursor.getColumnIndex(DBHelper.SSD_PRESENT_COL)
        SSDPresent = cursor.getInt(columnIndex)
        columnIndex = cursor.getColumnIndex(DBHelper.RAM_VOLUME_COL)
        RAMVolume = cursor.getInt(columnIndex)
        columnIndex = cursor.getColumnIndex(DBHelper.IS_FHD_COL)
        isFHD = cursor.getInt(columnIndex)
        columnIndex = cursor.getColumnIndex(DBHelper.SCREEN_TIME)
        screenTime = cursor.getInt(columnIndex)


        cursor.close()
        return Laptop(
            ID,
            manufacturerName,
            HDDVolume,
            if (SSDPresent == 1) true else false,
            RAMVolume,
            if (isFHD == 1) true else false, screenTime
        )
    }

    fun laptopsValueLowerAVG(prop: String): MutableList<Laptop> {
        var col = ""
        if (prop == "Объём HDD") {
            col = HDD_VOLUME_COL
        }
        else if (prop == "Объём RAM") {
            col = RAM_VOLUME_COL
        }
        else {
            col = SCREEN_TIME
        }

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TABLE_NAME} WHERE ${col} < (SELECT AVG(${col}) FROM ${TABLE_NAME})", null)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return mutableListOf()
        }

        val laptops: MutableList<Laptop> = mutableListOf()

        var ID = 0
        var manufacturerName = ""
        var HDDVolume = 0
        var SSDPresent = 0
        var RAMVolume = 0
        var isFHD = 0
        var screenTime = 0
        do {
            var columnIndex = cursor.getColumnIndex(DBHelper.ID_COL)
            ID = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.MANUFACTURE_NAME_COL)
            manufacturerName = cursor.getString(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.HDD_VOLUME_COL)
            HDDVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SSD_PRESENT_COL)
            SSDPresent = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.RAM_VOLUME_COL)
            RAMVolume = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.IS_FHD_COL)
            isFHD = cursor.getInt(columnIndex)
            columnIndex = cursor.getColumnIndex(DBHelper.SCREEN_TIME)
            screenTime = cursor.getInt(columnIndex)
            laptops.add(Laptop(
                ID,
                manufacturerName,
                HDDVolume,
                if (SSDPresent == 1) true else false,
                RAMVolume,
                if (isFHD == 1) true else false, screenTime)
            )
        } while (cursor.moveToNext())

        cursor.close()

        return laptops
    }

    companion object{
        private val DATABASE_NAME = "dns"

        private val DATABASE_VERSION = 2

        val TABLE_NAME = "laptops"

        val ID_COL = "id"

        val MANUFACTURE_NAME_COL = "manufacturer_name"

        val HDD_VOLUME_COL = "HDD_volume"

        val SSD_PRESENT_COL = "SSD_present"

        val RAM_VOLUME_COL = "RAM_volume"

        val IS_FHD_COL = "is_FHD"

        val SCREEN_TIME = "screen_time"
    }
}