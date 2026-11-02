package com.gabriel.financas.data.repository

import com.gabriel.financas.data.local.AccountDao
import com.gabriel.financas.data.local.AccountEntity
import com.gabriel.financas.data.local.CategoryDao
import com.gabriel.financas.data.local.CategoryEntity
import com.gabriel.financas.data.local.TransactionDao
import com.gabriel.financas.data.local.TransactionEntity
import com.gabriel.financas.data.local.TransactionWithCategory
import com.gabriel.financas.domain.model.Account
import com.gabriel.financas.domain.model.Category
import com.gabriel.financas.domain.model.Transaction
import com.gabriel.financas.domain.model.TransactionType
import com.gabriel.financas.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepositoryImpl @Inject constructor(
    private val transactions: TransactionDao,
    private val categories: CategoryDao,
    private val accounts: AccountDao,
) : FinanceRepository {

    override fun observeTransactions(month: YearMonth): Flow<List<Transaction>> =
        transactions.observeBetween(month.atDay(1).toEpochDay(), month.atEndOfMonth().toEpochDay())
            .map { list -> list.map { it.toDomain() } }

    override fun observeCategories(): Flow<List<Category>> =
        categories.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeAccounts(): Flow<List<Account>> =
        accounts.observeAll().map { list -> list.map { Account(it.id, it.name) } }

    override suspend fun addTransaction(transaction: Transaction) {
        transactions.insert(
            TransactionEntity(
                amountCents = transaction.amountCents,
                type = transaction.type.name,
                epochDay = transaction.date.toEpochDay(),
                note = transaction.note,
                categoryId = transaction.category.id,
                accountId = transaction.accountId,
            ),
        )
    }

    override suspend fun deleteTransaction(id: Long) = transactions.delete(id)

    override suspend fun seedDefaultsIfEmpty() {
        if (accounts.count() == 0) accounts.insert(AccountEntity(name = "Carteira"))
        if (categories.count() == 0) categories.insertAll(DefaultCategories.all)
    }
}

private fun CategoryEntity.toDomain() =
    Category(id, name, icon, colorArgb, TransactionType.valueOf(type))

private fun TransactionWithCategory.toDomain() = Transaction(
    id = transaction.id,
    amountCents = transaction.amountCents,
    type = TransactionType.valueOf(transaction.type),
    date = LocalDate.ofEpochDay(transaction.epochDay),
    note = transaction.note,
    category = category.toDomain(),
    accountId = transaction.accountId,
)

private object DefaultCategories {
    private fun e(name: String, icon: String, color: Long) =
        CategoryEntity(name = name, icon = icon, colorArgb = color, type = TransactionType.EXPENSE.name)

    private fun i(name: String, icon: String, color: Long) =
        CategoryEntity(name = name, icon = icon, colorArgb = color, type = TransactionType.INCOME.name)

    val all = listOf(
        e("Alimentação", "restaurant", 0xFFF59E0B),
        e("Mercado", "shopping_cart", 0xFF10B981),
        e("Transporte", "directions_car", 0xFF3B82F6),
        e("Moradia", "home", 0xFF8B5CF6),
        e("Saúde", "favorite", 0xFFEF4444),
        e("Lazer", "movie", 0xFFEC4899),
        e("Educação", "school", 0xFF06B6D4),
        e("Outros", "more_horiz", 0xFF94A3B8),
        i("Salário", "payments", 0xFF22C55E),
        i("Extras", "add_card", 0xFF14B8A6),
    )
}
