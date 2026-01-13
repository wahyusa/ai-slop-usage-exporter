package com.example.usageexporter.export

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ZipExporter {

    fun export(json: String, outFile: File) {
        ZipOutputStream(FileOutputStream(outFile)).use { zip ->
            zip.putNextEntry(ZipEntry("usage.json"))
            zip.write(json.toByteArray())
            zip.closeEntry()
        }
    }
}
