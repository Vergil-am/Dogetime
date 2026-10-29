package dev.dogetime.core

/**
 * Extracts a subtitle file (.srt, .vtt, .ass) from downloaded bytes (which is typically a zip
 * archive on SubDL) and saves it to a temporary file on the device.
 * Returns the absolute path of the extracted subtitle file, or null if extraction fails.
 */
expect fun extractAndSaveSubtitle(bytes: ByteArray, filename: String): String?
