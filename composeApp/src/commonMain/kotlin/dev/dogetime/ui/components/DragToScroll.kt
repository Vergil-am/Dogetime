package dev.dogetime.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs

/**
 * Lets a mouse drag scroll a horizontal list, as a finger does: on desktop, Compose only
 * scrolls lists with the wheel or a touchpad. Touch input is left to the list itself. Once the
 * pointer has moved past the touch slop the drag is consumed, so it doesn't also click an item.
 */
fun Modifier.dragToScroll(state: LazyListState): Modifier = pointerInput(state) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (down.type != PointerType.Mouse) return@awaitEachGesture
        var dragged = 0f
        var dragging = false
        while (true) {
            val change = awaitPointerEvent().changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) break
            val dx = change.positionChange().x
            dragged += dx
            if (!dragging && abs(dragged) > viewConfiguration.touchSlop) dragging = true
            if (dragging) {
                state.dispatchRawDelta(-dx)
                change.consume()
            }
        }
    }
}
