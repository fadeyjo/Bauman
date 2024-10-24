package com.example.lw_5_source

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log

class CompositionContentProvider : ContentProvider() {

    private lateinit var dbHelper: CompositionDBHelper

    companion object {
        const val AUTHORITY = "com.example.lw_5_source.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/compositions")
        private const val COMPOSITIONS = 1
        private const val COMPOSITION_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "compositions", COMPOSITIONS)
            addURI(AUTHORITY, "compositions/#", COMPOSITION_ID)
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = CompositionDBHelper(context!!, null)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        Log.i("MainActivity", "info")
        val db: SQLiteDatabase = dbHelper.readableDatabase
        return when (uriMatcher.match(uri)) {
            //COMPOSITIONS -> dbHelper.getCompositions()
            COMPOSITIONS -> db.query(CompositionDBHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            COMPOSITION_ID -> {
                selectionArgs?.let {
                    db.query(CompositionDBHelper.TABLE_NAME, projection, "${CompositionDBHelper.ID_COL}=?", it, null, null, sortOrder)
                }
            }
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val id = db.insert(CompositionDBHelper.TABLE_NAME, null, values)
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.withAppendedPath(CONTENT_URI, id.toString())
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            COMPOSITION_ID -> db.update(CompositionDBHelper.TABLE_NAME, values, "${CompositionDBHelper.ID_COL}=?", selectionArgs)
            else -> 0
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            COMPOSITION_ID -> db.delete(CompositionDBHelper.TABLE_NAME, "${CompositionDBHelper.ID_COL}=?", selectionArgs)
            else -> 0
        }
        context?.contentResolver?.notifyChange(uri, null)
        return count
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            COMPOSITIONS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.compositions"
            COMPOSITION_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.composition"
            else -> null
        }
    }
}
