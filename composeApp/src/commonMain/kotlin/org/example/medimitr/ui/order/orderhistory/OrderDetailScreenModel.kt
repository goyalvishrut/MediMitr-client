package org.example.medimitr.ui.order.orderhistory

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.order.Order
import org.example.medimitr.domain.order.OrderRepository
import org.example.medimitr.presentation.base.BaseScreenModel

data class OrderDetailUiState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null,
)

class OrderDetailScreenModel(
    private val orderRepository: OrderRepository,
) : BaseScreenModel() {
    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrderDetails(orderId: Int) {
        // Prevent reloading if already loaded or loading
        if (_uiState.value.order?.id == orderId || _uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            orderRepository
                .getOrderById(orderId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load order details: ${e.message}",
                        )
                    }
                }.collect { result ->
                    if (result.isSuccess) {
                        val order = result.getOrNull()
                        if (order != null) {
                            _uiState.update {
                                it.copy(isLoading = false, order = order)
                            }
                        } else {
                            _uiState.update {
                                it.copy(isLoading = false, error = "Order not found.")
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Failed to load order details")
                        }
                    }
                }
        }
    }
}
