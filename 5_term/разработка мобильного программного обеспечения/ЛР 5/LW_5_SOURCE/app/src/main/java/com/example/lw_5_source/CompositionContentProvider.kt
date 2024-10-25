package com.example.lw_5_source

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.database.SQLException

class CompositionContentProvider : ContentProvider() {

    private lateinit var dbHelper: CompositionDBHelper

    override fun onCreate(): Boolean {
        dbHelper = CompositionDBHelper(context as Context, null)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            COMPOSITIONS -> db.query(
                CompositionDBHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )
            COMPOSITION_ID -> db.query(
                CompositionDBHelper.TABLE_NAME,
                projection,
                "${CompositionDBHelper.ID_COL} = ?",
                arrayOf(ContentUris.parseId(uri).toString()),
                null,
                null,
                sortOrder
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = when (uriMatcher.match(uri)) {
            COMPOSITIONS -> db.insert(CompositionDBHelper.TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Invalid URI for insert operation: $uri")
        }
        if (id > 0) {
            val insertUri = ContentUris.withAppendedId(CONTENT_URI, id)
            context?.contentResolver?.notifyChange(insertUri, null)
            return insertUri
        }
        throw SQLException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val deletedRows = when (uriMatcher.match(uri)) {
            COMPOSITIONS -> db.delete(CompositionDBHelper.TABLE_NAME, selection, selectionArgs)
            COMPOSITION_ID -> db.delete(
                CompositionDBHelper.TABLE_NAME,
                "${CompositionDBHelper.ID_COL} = ?",
                arrayOf(ContentUris.parseId(uri).toString())
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (deletedRows > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return deletedRows
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val updatedRows = when (uriMatcher.match(uri)) {
            COMPOSITIONS -> db.update(CompositionDBHelper.TABLE_NAME, values, selection, selectionArgs)
            COMPOSITION_ID -> db.update(
                CompositionDBHelper.TABLE_NAME,
                values,
                "${CompositionDBHelper.ID_COL} = ?",
                arrayOf(ContentUris.parseId(uri).toString())
            )
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (updatedRows > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return updatedRows
    }

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        COMPOSITIONS -> "vnd.android.cursor.dir/$AUTHORITY.$PATH_COMPOSITIONS"
        COMPOSITION_ID -> "vnd.android.cursor.item/$AUTHORITY.$PATH_COMPOSITIONS"
        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }

    companion object {
        const val AUTHORITY = "com.example.lw_5_source.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${CompositionDBHelper.TABLE_NAME}")

        private const val COMPOSITIONS = 1
        private const val COMPOSITION_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, CompositionDBHelper.TABLE_NAME, COMPOSITIONS)
            addURI(AUTHORITY, "${CompositionDBHelper.TABLE_NAME}/#", COMPOSITION_ID)
        }

        const val PATH_COMPOSITIONS = "compositions"
    }
}
