package com.gabriel.financas.ui.addtransaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gabriel.financas.domain.model.TransactionType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTransactionScreen(onDone: () -> Unit, viewModel: AddTransactionViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) { if (state.saved) onDone() }

    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // widthIn mantém o formulário legível em telas largas.
            Column(Modifier.widthIn(max = 560.dp).fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Novo lançamento", style = MaterialTheme.typography.headlineMedium)

                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    val types = listOf(TransactionType.EXPENSE to "Despesa", TransactionType.INCOME to "Receita")
                    types.forEachIndexed { i, (type, label) ->
                        SegmentedButton(
                            selected = state.type == type,
                            onClick = { viewModel.onType(type) },
                            shape = SegmentedButtonDefaults.itemShape(i, types.size),
                        ) { Text(label) }
                    }
                }

                OutlinedTextField(
                    value = state.amount, onValueChange = viewModel::onAmount,
                    label = { Text("Valor (R$)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.error != null, supportingText = { state.error?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Categoria", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.categories.forEach { c ->
                        FilterChip(selected = state.selected?.id == c.id, onClick = { viewModel.onCategory(c) }, label = { Text(c.name) })
                    }
                }

                OutlinedTextField(
                    value = state.note, onValueChange = viewModel::onNote,
                    label = { Text("Observação (opcional)") }, modifier = Modifier.fillMaxWidth(),
                )

                Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) { Text("Salvar") }
            }
        }
    }
}
