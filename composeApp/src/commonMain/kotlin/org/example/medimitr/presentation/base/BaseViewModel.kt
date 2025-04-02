package org.example.medimitr.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

// Base ViewModel (Optional but helpful)
abstract class BaseViewModel : ViewModel() {
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
