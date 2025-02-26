package com.example.mahaconnect

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var complaintEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var speechButton: Button
    private val SPEECH_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        complaintEditText = findViewById(R.id.complaintEditText)
        submitButton = findViewById(R.id.submitButton)
        speechButton = findViewById(R.id.speechButton)

        submitButton.setOnClickListener {
            val text = complaintEditText.text.toString()
            if (text.isNotEmpty()) {
                val complaint = Complaint(text = text)
                dbHelper.insertComplaint(complaint)
                Toast.makeText(this, "Complaint saved locally", Toast.LENGTH_SHORT).show()
                complaintEditText.text.clear()
                startSyncService()
            } else {
                Toast.makeText(this, "Please enter a complaint", Toast.LENGTH_SHORT).show()
            }
        }

        speechButton.setOnClickListener {
            startSpeechRecognition()
        }

        // Start sync service when app launches
        startSyncService()
    }

    override fun onResume() {
        super.onResume()
        // Request RECORD_AUDIO permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    private fun startSyncService() {
        val intent = Intent(this, SyncService::class.java)
        startService(intent)
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "mr-IN") // Marathi language code
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            complaintEditText.setText(results?.get(0) ?: "")
        }
    }
}