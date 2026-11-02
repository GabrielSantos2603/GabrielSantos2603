package com.gabriel.financas.domain.model

import java.time.LocalDate

enum class TransactionType { INCOME, EXPENSE }

/** Dinheiro sempre em centavos (Long) para evitar erros de ponto flutuante. */
data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val colorArgb: Long,
    val type: TransactionType,
)

data class Account(
    val id: Long = 0,
    val name: String,
)

data class Transaction(
    val id: Long = 0,
    val amountCents: Long,
    val type: TransactionType,
    val date: LocalDate,
    val note: String,
    val category: Category,
    val accountId: Long,
)

data class MonthSummary(
    val incomeCents: Long,
    val expenseCents: Long,
) {
    val balanceCents: Long get() = incomeCents - expenseCents
}
