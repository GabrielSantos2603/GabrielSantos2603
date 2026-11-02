package com.gabriel.financas.domain.usecase

import com.gabriel.financas.domain.model.MonthSummary
import com.gabriel.financas.domain.model.Transaction
import com.gabriel.financas.domain.model.TransactionType
import com.gabriel.financas.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

class ObserveMonthSummary @Inject constructor(private val repo: FinanceRepository) {
    operator fun invoke(month: YearMonth): Flow<MonthSummary> =
        repo.observeTransactions(month).map { list -> summarize(list) }

    companion object {
        fun summarize(list: List<Transaction>) = MonthSummary(
            incomeCents = list.filter { it.type == TransactionType.INCOME }.sumOf { it.amountCents },
            expenseCents = list.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountCents },
        )
    }
}

class AddTransaction @Inject constructor(private val repo: FinanceRepository) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        if (transaction.amountCents <= 0) {
            return Result.failure(IllegalArgumentException("O valor deve ser maior que zero"))
        }
        return runCatching { repo.addTransaction(transaction) }
    }
}
