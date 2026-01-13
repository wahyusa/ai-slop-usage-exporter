package com.example.usageexporter.data

import android.app.usage.UsageStatsManager
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneId

class UsageRepository(private val context: Context) {

    fun exportYear(year: Int): String {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE)
                as UsageStatsManager

        val start = LocalDate.of(year, 1, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val end = LocalDate.of(year, 12, 31)
            .atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_YEARLY,
            start,
            end
        )

        val arr = JSONArray()
        for (s in stats) {
            val o = JSONObject()
            o.put("package", s.packageName)
            o.put("foreground_ms", s.totalTimeInForeground)
            arr.put(o)
        }

        return arr.toString(2)
    }
}
