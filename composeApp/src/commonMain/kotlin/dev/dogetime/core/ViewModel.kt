package dev.dogetime.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Minimal multiplatform ViewModel base. Holds a coroutine scope that outlives
 * recomposition. Whoever owns the ViewModel calls [clear] when it is no longer needed.
 */
abstract class ViewModel {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun clear() {
        scope.cancel()
    }
}
