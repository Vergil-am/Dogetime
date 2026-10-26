package dev.dogetime.torrent

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private val appDir = File(System.getProperty("user.home"), ".dogetime")

internal actual fun torrentDataDir(): File = File(appDir, "torrents")

/**
 * The native library ships inside a jar, where System.loadLibrary can't find it, so it is copied
 * out once and its path handed to libtorrent4j.
 */
internal actual fun prepareTorrentLibrary() {
    val os = System.getProperty("os.name")
    val arm = System.getProperty("os.arch").let { it == "aarch64" || it == "arm64" }
    val resource = when {
        os.startsWith("Windows") -> "lib/x86_64/libtorrent4j.dll"
        os.contains("Mac") -> "lib/arm64/libtorrent4j.dylib"
        arm -> "lib/arm/libtorrent4j.so"
        else -> "lib/x86_64/libtorrent4j.so"
    }
    val input = Thread.currentThread().contextClassLoader.getResourceAsStream(resource)
        ?: throw IllegalStateException("This build has no torrent engine for $os.")
    val file = File(appDir, "native/${resource.substringAfterLast('/')}")
    file.parentFile.mkdirs()
    // Copied each start, so an app update never runs an older library. The copy is written next
    // to it and moved into place: overwriting the file itself would corrupt it under any process
    // that has it loaded. On Windows a loaded library can't be replaced, so the existing one is used.
    runCatching {
        val temp = File.createTempFile("libtorrent4j", ".tmp", file.parentFile)
        try {
            input.use { source -> temp.outputStream().use { source.copyTo(it) } }
            Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } finally {
            temp.delete()
        }
    }.onFailure { if (!file.isFile) throw it }
    System.setProperty("libtorrent4j.jni.path", file.absolutePath)
}
