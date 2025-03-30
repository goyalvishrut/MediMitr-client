package org.example.medimitr.presentation.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent // Make ViewModel KoinComponent

// Base ViewModel (Optional but helpful)
abstract class BaseViewModel : KoinComponent {
    // Create a dedicated scope for ViewModels, using Dispatchers.Main for UI updates
    // SupervisorJob prevents crash if one child coroutine fails
    // Using Main dispatcher (KMM provides expect/actual for this) is often default for UI interaction
    protected val viewModelScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    // Override in iOS part if needed
    open fun clear() {
        // Cancel the scope when the ViewModel is cleared
        // viewModelScope.cancel() // Needs platform-specific handling
    }
}
