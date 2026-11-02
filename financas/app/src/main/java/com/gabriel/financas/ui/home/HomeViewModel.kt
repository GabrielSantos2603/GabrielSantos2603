package com.gabriel.financas.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.financas.domain.model.MonthSummary
import com.gabriel.financas.domain.model.Transaction
import com.gabriel.financas.domain.repository.FinanceRepository
import com.gabriel.financas.domain.usecase.ObserveMonthSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

data class HomeUiState(
    val month: YearMonth = YearMonth.now(),
    val summary: MonthSummary = MonthSummary(0, 0),
    val transactions: List<Transaction> = emptyList(),
    val loading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FinanceRepository,
    observeSummary: ObserveMonthSummary,
) : ViewModel() {

    private val month = MutableStateFlow(YearMonth.now())

    val state: StateFlow<HomeUiState> = month.flatMapLatest { m ->
        combine(observeSummary(m), repository.observeTransactions(m)) { summary, list ->
            HomeUiState(m, summary, list, loading = false)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun previousMonth() = month.update { it.minusMonths(1) }
    fun nextMonth() = month.update { it.plusMonths(1) }
    fun delete(id: Long) = viewModelScope.launch { repository.deleteTransaction(id) }
}
