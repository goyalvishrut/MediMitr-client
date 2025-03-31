package org.example.medimitr.presentation.base

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseScreenModel : ScreenModel {
    protected val viewModelScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    // Override in iOS part if needed
    open fun clear() {
        // Cancel the scope when the ViewModel is cleared
        // viewModelScope.cancel() // Needs platform-specific handling
    }
}
