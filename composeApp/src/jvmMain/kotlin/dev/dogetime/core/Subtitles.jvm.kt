package dev.dogetime.core

import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.ZipInputStream

actual fun extractAndSaveSubtitle(bytes: ByteArray, filename: String): String? {
    try {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "dogetime-subtitles").apply { mkdirs() }
        // Check if zip header (PK\x03\x04: 0x50, 0x4B, 0x03, 0x04)
        if (bytes.size >= 4 && bytes[0] == 0x50.toByte() && bytes[1] == 0x4B.toByte()) {
            ZipInputStream(ByteArrayInputStream(bytes)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val entryName = entry.name
                    if (!entry.isDirectory && (
                            entryName.endsWith(".srt", ignoreCase = true) ||
                            entryName.endsWith(".vtt", ignoreCase = true) ||
                            entryName.endsWith(".ass", ignoreCase = true) ||
                            entryName.endsWith(".sub", ignoreCase = true)
                        )) {
                        val ext = entryName.substringAfterLast('.', "srt")
                        val outFile = File(tempDir, "sub_${System.currentTimeMillis()}.$ext")
                        outFile.outputStream().use { fos -> zis.copyTo(fos) }
                        return outFile.absolutePath
                    }
                    entry = zis.nextEntry
                }
            }
        }
        // If not a zip or no recognizable subtitle inside, save bytes directly as .srt
        val outFile = File(tempDir, "sub_${System.currentTimeMillis()}.srt")
        outFile.writeBytes(bytes)
        return outFile.absolutePath
    } catch (_: Exception) {
        return null
    }
}
