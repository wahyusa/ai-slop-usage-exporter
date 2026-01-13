package com.example.usageexporter

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            statusText = findViewById(R.id.tv_status)
            val btnExport = findViewById<Button>(R.id.btn_export)
            val btnPermission = findViewById<Button>(R.id.btn_permission)

            btnPermission.setOnClickListener {
                try {
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                } catch (e: Exception) {
                    Toast.makeText(this, "Could not open settings automatically", Toast.LENGTH_LONG).show()
                }
            }

            btnExport.setOnClickListener {
                if (hasUsageStatsPermission()) {
                    exportToDownloads()
                } else {
                    Toast.makeText(this, "Please grant permission first!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionStatus()
    }

    private fun checkPermissionStatus() {
        try {
            if (hasUsageStatsPermission()) {
                statusText.text = "Status: Ready to Export"
                statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            } else {
                statusText.text = "Status: Permission Needed"
                statusText.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
        } catch (e: Exception) {
            // Ignore UI errors
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun exportToDownloads() {
        try {
            // 1. Get Usage Stats
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.YEAR, -1)
            val startTime = calendar.timeInMillis

            val usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            if (usageStatsList == null || usageStatsList.isEmpty()) {
                statusText.text = "Permission active, but no stats found."
                return
            }

            // 2. Build the CSV String
            val sb = StringBuilder()
            sb.append("Package Name, Last Time Used, Total Time Foreground (ms)\n")
            for (stats in usageStatsList) {
                if (stats.totalTimeInForeground > 0) {
                    sb.append("${stats.packageName}, ${stats.lastTimeUsed}, ${stats.totalTimeInForeground}\n")
                }
            }

            // 3. Save to Downloads/UsageExporter/
            val folderName = "UsageExporter"
            val fileName = "usage_stats_${System.currentTimeMillis()}.csv"
            
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                // THIS LINE creates the subfolder
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + folderName)
            }

            val resolver = contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                val outputStream = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    outputStream.write(sb.toString().toByteArray())
                    outputStream.close()
                    
                    statusText.text = "Saved to Downloads/$folderName/\nFile: $fileName"
                    Toast.makeText(this, "Saved to Downloads/$folderName/", Toast.LENGTH_LONG).show()
                } else {
                    statusText.text = "Failed to open output stream."
                }
            } else {
                statusText.text = "Failed to create file. (Check if folder exists?)"
            }

        } catch (e: Exception) {
            statusText.text = "Error: ${e.message}"
            e.printStackTrace()
        }
    }
}