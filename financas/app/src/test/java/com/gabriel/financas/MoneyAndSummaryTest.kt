package com.gabriel.financas

import com.gabriel.financas.domain.model.Category
import com.gabriel.financas.domain.model.Transaction
import com.gabriel.financas.domain.model.TransactionType
import com.gabriel.financas.domain.usecase.ObserveMonthSummary
import com.gabriel.financas.ui.parseCents
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class MoneyAndSummaryTest {

    @Test fun parseCents_aceitaVirgulaEPonto() {
        assertEquals(1250L, parseCents("12,50"))
        assertEquals(1250L, parseCents("12.50"))
        assertEquals(1000L, parseCents("10"))
    }

    @Test fun parseCents_rejeitaInvalido() {
        assertNull(parseCents(""))
        assertNull(parseCents("abc"))
        assertNull(parseCents("1,234")) // mais de 2 casas decimais
    }

    @Test fun summarize_somaReceitasEDespesas() {
        val cat = Category(1, "x", "i", 0, TransactionType.EXPENSE)
        fun t(v: Long, type: TransactionType) = Transaction(0, v, type, LocalDate.now(), "", cat, 1)
        val s = ObserveMonthSummary.summarize(
            listOf(t(100_00, TransactionType.INCOME), t(30_00, TransactionType.EXPENSE), t(20_00, TransactionType.EXPENSE)),
        )
        assertEquals(100_00, s.incomeCents)
        assertEquals(50_00, s.expenseCents)
        assertEquals(50_00, s.balanceCents)
    }
}
