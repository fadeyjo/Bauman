package com.example.lw_5_source

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CompositionDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                AUTHOR_COL + " VARCHAR(30) NOT NULL, " +
                TITLE_COL + " VARCHAR(30) NOT NULL, " +
                DURATION_COL + " INTEGER NOT NULL);")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addComposition(composition: Composition) {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(AUTHOR_COL, composition.author)
            put(TITLE_COL, composition.title)
            put(DURATION_COL, composition.duration)
        }

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun deleteComposition(compositionId: Int) {
        val db = this.writableDatabase

        db.delete(TABLE_NAME, "$ID_COL = ?", arrayOf(compositionId.toString()))

        db.close()
    }

    fun getIds(): MutableList<Int> {
        val ids = mutableListOf<Int>()
        val db = this.readableDatabase

        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(ID_COL),
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ID_COL))
                ids.add(id)
            }
        }
        cursor.close()
        db.close()
        return ids
    }

    fun getCompositions(): Cursor {
        val db = this.readableDatabase

        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(ID_COL, AUTHOR_COL, TITLE_COL, DURATION_COL),
            null,
            null,
            null,
            null,
            null
        )
        return cursor
    }

    fun getAllCompositions(): MutableList<Composition> {
        val compositions = mutableListOf<Composition>()
        val db = this.readableDatabase

        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(ID_COL, AUTHOR_COL, TITLE_COL, DURATION_COL),
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ID_COL))
                val author = getString(getColumnIndexOrThrow(AUTHOR_COL))
                val title = getString(getColumnIndexOrThrow(TITLE_COL))
                val duration = getInt(getColumnIndexOrThrow(DURATION_COL))
                compositions.add(Composition(id, author, title, duration))
            }
        }
        cursor.close()
        db.close()
        return compositions
    }

    companion object{
        private val DATABASE_NAME = "composition_library"

        private val DATABASE_VERSION = 2

        val TABLE_NAME = "compositions"

        val ID_COL = "id"

        val AUTHOR_COL = "author"

        val TITLE_COL = "title"

        val DURATION_COL = "duration"
    }
}