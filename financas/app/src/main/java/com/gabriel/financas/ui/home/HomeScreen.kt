package com.gabriel.financas.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabriel.financas.domain.model.Transaction
import com.gabriel.financas.domain.model.TransactionType
import com.gabriel.financas.ui.formatCents
import com.gabriel.financas.ui.theme.Expense
import com.gabriel.financas.ui.theme.Income
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    widthClass: WindowWidthSizeClass,
    onAdd: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val wide = widthClass != WindowWidthSizeClass.Compact

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd, icon = { Icon(Icons.Default.Add, null) }, text = { Text("Lançamento") })
        },
    ) { padding ->
        if (wide) {
            // Tablet / dobrável aberto: resumo à esquerda, lista à direita.
            Row(Modifier.fillMaxSize().padding(padding).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(Modifier.weight(0.4f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    MonthSelector(state, viewModel::previousMonth, viewModel::nextMonth)
                    SummaryCard(state)
                }
                TransactionList(state.transactions, viewModel::delete, Modifier.weight(0.6f))
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MonthSelector(state, viewModel::previousMonth, viewModel::nextMonth)
                SummaryCard(state)
                TransactionList(state.transactions, viewModel::delete, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MonthSelector(state: HomeUiState, onPrev: () -> Unit, onNext: () -> Unit) {
    val label = state.month.format(DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR")))
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        IconButton(onPrev) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mês anterior") }
        Text(label.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleLarge)
        IconButton(onNext) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Próximo mês") }
    }
}

@Composable
private fun SummaryCard(state: HomeUiState) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Saldo do mês", style = MaterialTheme.typography.labelLarge)
            Text(formatCents(state.summary.balanceCents), style = MaterialTheme.typography.displaySmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Receitas", style = MaterialTheme.typography.labelMedium)
                    Text(formatCents(state.summary.incomeCents), color = Income, style = MaterialTheme.typography.titleMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Despesas", style = MaterialTheme.typography.labelMedium)
                    Text(formatCents(state.summary.expenseCents), color = Expense, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun TransactionList(items: List<Transaction>, onDelete: (Long) -> Unit, modifier: Modifier = Modifier) {
    if (items.isEmpty()) {
        Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Nenhum lançamento neste mês", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    LazyColumn(modifier, contentPadding = PaddingValues(bottom = 88.dp)) {
        items(items, key = { it.id }) { t ->
            val sign = if (t.type == TransactionType.INCOME) "+" else "-"
            ListItem(
                headlineContent = { Text(t.category.name) },
                supportingContent = { Text(t.note.ifBlank { t.date.format(DateTimeFormatter.ofPattern("dd/MM")) }) },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "$sign ${formatCents(t.amountCents)}",
                            color = if (t.type == TransactionType.INCOME) Income else Expense,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        IconButton({ onDelete(t.id) }) { Icon(Icons.Default.Delete, "Excluir") }
                    }
                },
            )
        }
    }
}
