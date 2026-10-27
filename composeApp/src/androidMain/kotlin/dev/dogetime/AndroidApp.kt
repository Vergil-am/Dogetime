package dev.dogetime

import android.content.Context
import `is`.xyz.mpv.Utils

/** Holds the application context for shared code that needs it, such as the database. */
object AndroidApp {
    lateinit var context: Context
        private set

    fun init(context: Context) {
        val app = context.applicationContext
        this.context = app
        try {
            Utils.copyAssets(app)
        } catch (_: Exception) {}
    }
}
