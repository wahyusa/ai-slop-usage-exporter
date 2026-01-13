package com.example.usageexporter

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.usageexporter.data.UsageRepository
import com.example.usageexporter.export.ZipExporter
import java.io.File
import java.time.Year

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.btnExport)

        btn.setOnClickListener {
            if (!hasUsagePermission()) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                Toast.makeText(this, "Grant usage access first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val year = Year.now().value
            val repo = UsageRepository(this)
            val json = repo.exportYear(year)

            val outFile = File(getExternalFilesDir(null), "usage_$year.zip")
            ZipExporter.export(json, outFile)

            Toast.makeText(this, "Exported: ${outFile.absolutePath}", Toast.LENGTH_LONG).show()
        }
    }

    private fun hasUsagePermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
}
