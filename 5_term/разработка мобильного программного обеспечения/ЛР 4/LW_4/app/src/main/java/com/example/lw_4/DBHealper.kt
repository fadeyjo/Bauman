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