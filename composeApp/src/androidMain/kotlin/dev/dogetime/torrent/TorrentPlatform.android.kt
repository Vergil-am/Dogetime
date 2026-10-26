package dev.dogetime.torrent

import dev.dogetime.AndroidApp
import java.io.File

internal actual fun torrentDataDir(): File = File(AndroidApp.context.cacheDir, "torrents")

// The library is packaged in the APK's lib/<abi>/ folder, where System.loadLibrary finds it.
internal actual fun prepareTorrentLibrary() = Unit
