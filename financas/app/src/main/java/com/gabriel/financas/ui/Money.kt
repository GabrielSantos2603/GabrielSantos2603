package com.gabriel.financas.ui

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

private val ptBr = Locale("pt", "BR")

fun formatCents(cents: Long): String =
    NumberFormat.getCurrencyInstance(ptBr).format(BigDecimal(cents).movePointLeft(2))

/** Converte "12,50" / "12.50" / "1250" digitado em centavos. Retorna null se inválido. */
fun parseCents(input: String): Long? {
    val clean = input.trim().replace(".", "").replace(',', '.')
    if (clean.isEmpty()) return null
    return runCatching { BigDecimal(clean).movePointRight(2).longValueExact() }.getOrNull()
}
