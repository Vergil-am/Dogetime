package dev.dogetime

import android.content.Context

/** Holds the application context for shared code that needs it, such as the database. */
object AndroidApp {
    lateinit var context: Context
        private set

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}
