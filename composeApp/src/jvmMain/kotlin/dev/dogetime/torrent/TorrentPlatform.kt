package dev.dogetime.torrent

import java.io.File

/** Where torrents are downloaded while they're watched. Cleared each time the engine starts. */
internal expect fun torrentDataDir(): File

/** Makes libtorrent4j's native library loadable. Called once, before the engine starts. */
internal expect fun prepareTorrentLibrary()
