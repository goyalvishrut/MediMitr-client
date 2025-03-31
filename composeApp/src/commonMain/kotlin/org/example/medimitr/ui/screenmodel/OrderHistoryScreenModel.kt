package org.example.medimitr.ui.screenmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.order.Order
import org.example.medimitr.domain.order.OrderRepository
import org.example.medimitr.presentation.base.BaseScreenModel

data class OrderHistoryUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null,
)

class OrderHistoryScreenModel(
    private val orderRepository: OrderRepository,
) : BaseScreenModel() {
    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            orderRepository
                .getOrderHistory()
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to load history: ${e.message}")
                    }
                }.collect { result ->
                    if (result.isSuccess) {
                        val orders = result.getOrNull() ?: emptyList()
                        _uiState.update {
                            it.copy(isLoading = false, orders = orders)
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Failed to load history")
                        }
                    }
                }
        }
    }
}
