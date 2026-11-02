package com.gabriel.financas.ui.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabriel.financas.domain.model.Category
import com.gabriel.financas.domain.model.Transaction
import com.gabriel.financas.domain.model.TransactionType
import com.gabriel.financas.domain.repository.FinanceRepository
import com.gabriel.financas.domain.usecase.AddTransaction
import com.gabriel.financas.ui.parseCents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddUiState(
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val note: String = "",
    val categories: List<Category> = emptyList(),
    val selected: Category? = null,
    val error: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: FinanceRepository,
    private val addTransaction: AddTransaction,
) : ViewModel() {

    private val _state = MutableStateFlow(AddUiState())
    val state: StateFlow<AddUiState> = _state.asStateFlow()

    private var allCategories: List<Category> = emptyList()

    init {
        viewModelScope.launch {
            allCategories = repository.observeCategories().first { it.isNotEmpty() }
            refreshCategories(TransactionType.EXPENSE)
        }
    }

    private fun refreshCategories(type: TransactionType) = _state.update {
        val list = allCategories.filter { c -> c.type == type }
        it.copy(type = type, categories = list, selected = list.firstOrNull())
    }

    fun onAmount(v: String) = _state.update { it.copy(amount = v.filter { c -> c.isDigit() || c == ',' || c == '.' }, error = null) }
    fun onNote(v: String) = _state.update { it.copy(note = v) }
    fun onType(t: TransactionType) = refreshCategories(t)
    fun onCategory(c: Category) = _state.update { it.copy(selected = c) }

    fun save() {
        val s = _state.value
        val cents = parseCents(s.amount)
        val category = s.selected
        if (cents == null || category == null) {
            _state.update { it.copy(error = "Informe um valor válido") }
            return
        }
        viewModelScope.launch {
            val account = repository.observeAccounts().first { it.isNotEmpty() }.first()
            addTransaction(
                Transaction(
                    amountCents = cents, type = s.type, date = LocalDate.now(),
                    note = s.note.trim(), category = category, accountId = account.id,
                ),
            ).onSuccess { _state.update { it.copy(saved = true) } }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }
}
