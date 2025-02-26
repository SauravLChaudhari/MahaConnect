package com.example.mahaconnect

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "MahaConnectDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE complaints (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT, timestamp INTEGER, status TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle upgrades if needed, e.g., drop and recreate table
    }

    fun insertComplaint(complaint: Complaint): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("text", complaint.text)
            put("timestamp", complaint.timestamp)
            put("status", complaint.status)
        }
        return db.insert("complaints", null, values)
    }

    fun getPendingComplaints(): List<Complaint> {
        val db = readableDatabase
        val cursor = db.query("complaints", null, "status = ?", arrayOf("pending"), null, null, null)
        val complaints = mutableListOf<Complaint>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
            val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
            val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
            complaints.add(Complaint(id, text, timestamp, status))
        }
        cursor.close()
        return complaints
    }

    fun updateComplaintStatus(id: Int, status: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", status)
        }
        db.update("complaints", values, "id = ?", arrayOf(id.toString()))
    }
}