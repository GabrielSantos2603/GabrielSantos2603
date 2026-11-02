package com.gabriel.financas.domain.repository

import com.gabriel.financas.domain.model.Account
import com.gabriel.financas.domain.model.Category
import com.gabriel.financas.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

interface FinanceRepository {
    fun observeTransactions(month: YearMonth): Flow<List<Transaction>>
    fun observeCategories(): Flow<List<Category>>
    fun observeAccounts(): Flow<List<Account>>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Long)
    suspend fun seedDefaultsIfEmpty()
}
