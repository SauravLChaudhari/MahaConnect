package com.example.mahaconnect

import android.app.IntentService
import android.content.Intent
import android.util.Log

class SyncService : IntentService("SyncService") {
    private lateinit var dbHelper: DatabaseHelper

    override fun onHandleIntent(intent: Intent?) {
        dbHelper = DatabaseHelper(this)
        if (NetworkUtils.isOnline(this)) {
            val pendingComplaints = dbHelper.getPendingComplaints()
            for (complaint in pendingComplaints) {
                val translatedText = translateText(complaint.text)
                sendComplaintToServer(complaint, translatedText)
                dbHelper.updateComplaintStatus(complaint.id, "synced")
            }
        }
    }

    private fun translateText(text: String): String {
        // Mock Bhashini API call - replace with actual API integration
        return "$text [Translated to English]"
    }

    private fun sendComplaintToServer(complaint: Complaint, translatedText: String) {
        // Mock server POST - replace with actual server endpoint
        Log.d("SyncService", "Sending complaint: $translatedText")
    }
}