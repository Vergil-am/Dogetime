package dev.dogetime.navigation

import androidx.compose.runtime.mutableStateListOf
import dev.dogetime.domain.MediaType
import dev.dogetime.domain.WatchTarget

sealed interface Destination {
    data object Home : Destination
    data object Browse : Destination
    data object Search : Destination
    data class Details(val type: MediaType, val id: Int) : Destination
    data class Watch(val target: WatchTarget, val title: String, val backdropUrl: String? = null) : Destination
}

/**
 * Back stack for the app. The first entry is always a bottom-bar tab;
 * details screens are pushed on top of it.
 */
class Navigator {
    private val backStack = mutableStateListOf<Destination>(Destination.Home)

    val current: Destination
        get() = backStack.last()

    val root: Destination
        get() = backStack.first()

    val canGoBack: Boolean
        get() = backStack.size > 1

    fun open(destination: Destination) {
        backStack.add(destination)
    }

    fun back() {
        if (canGoBack) backStack.removeAt(backStack.lastIndex)
    }

    fun selectTab(tab: Destination) {
        backStack.clear()
        backStack.add(tab)
    }
}
