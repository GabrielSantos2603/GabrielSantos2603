import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';

import '../../core/money.dart';
import '../../core/theme.dart';
import '../../data/providers.dart';
import '../../domain/models.dart';

class HomeScreen extends ConsumerWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final txs = ref.watch(monthTransactionsProvider);
    return Scaffold(
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => context.push('/add'),
        icon: const Icon(Icons.add),
        label: const Text('Lançamento'),
      ),
      body: SafeArea(
        child: txs.when(
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (e, _) => Center(child: Text('Erro: $e')),
          data: (list) => LayoutBuilder(builder: (context, c) {
            final summary = _Summary(list: list);
            final items = _TransactionList(list: list);
            // Tablet / dobrável aberto: duas colunas. Celular: uma coluna.
            if (c.maxWidth >= 840) {
              return Padding(
                padding: const EdgeInsets.all(16),
                child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
                  Expanded(flex: 4, child: SingleChildScrollView(child: Column(children: [const _MonthSelector(), summary]))),
                  const SizedBox(width: 16),
                  Expanded(flex: 6, child: items),
                ]),
              );
            }
            return Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Column(children: [const _MonthSelector(), summary, const SizedBox(height: 8), Expanded(child: items)]),
            );
          }),
        ),
      ),
    );
  }
}

class _MonthSelector extends ConsumerWidget {
  const _MonthSelector();

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final month = ref.watch(selectedMonthProvider);
    final label = toBeginningOfSentenceCase(DateFormat("MMMM 'de' yyyy", 'pt_BR').format(month));
    final notifier = ref.read(selectedMonthProvider.notifier);
    return Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
      IconButton(onPressed: notifier.previous, icon: const Icon(Icons.chevron_left), tooltip: 'Mês anterior'),
      Text(label, style: Theme.of(context).textTheme.titleLarge),
      IconButton(onPressed: notifier.next, icon: const Icon(Icons.chevron_right), tooltip: 'Próximo mês'),
    ]);
  }
}

class _Summary extends StatelessWidget {
  const _Summary({required this.list});
  final List<FinTransaction> list;

  @override
  Widget build(BuildContext context) {
    final s = MonthSummary.of(list);
    final theme = Theme.of(context);
    return Card(
      color: theme.colorScheme.primaryContainer,
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Text('Saldo do mês', style: theme.textTheme.labelLarge),
          const SizedBox(height: 4),
          Text(formatCents(s.balanceCents), style: theme.textTheme.displaySmall),
          const SizedBox(height: 12),
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            _Figure('Receitas', s.incomeCents, incomeColor),
            _Figure('Despesas', s.expenseCents, expenseColor, end: true),
          ]),
          _ExpenseChart(list: list),
        ]),
      ),
    );
  }
}

class _Figure extends StatelessWidget {
  const _Figure(this.label, this.cents, this.color, {this.end = false});
  final String label;
  final int cents;
  final Color color;
  final bool end;

  @override
  Widget build(BuildContext context) {
    final t = Theme.of(context).textTheme;
    return Column(crossAxisAlignment: end ? CrossAxisAlignment.end : CrossAxisAlignment.start, children: [
      Text(label, style: t.labelMedium),
      Text(formatCents(cents), style: t.titleMedium?.copyWith(color: color)),
    ]);
  }
}

/// Rosca de despesas por categoria.
class _ExpenseChart extends StatelessWidget {
  const _ExpenseChart({required this.list});
  final List<FinTransaction> list;

  @override
  Widget build(BuildContext context) {
    final byCat = <FinCategory, int>{};
    for (final t in list.where((t) => t.type == TxType.expense)) {
      byCat.update(t.category, (v) => v + t.amountCents, ifAbsent: () => t.amountCents);
    }
    if (byCat.isEmpty) return const SizedBox.shrink();
    return Padding(
      padding: const EdgeInsets.only(top: 16),
      child: SizedBox(
        height: 140,
        child: PieChart(PieChartData(
          centerSpaceRadius: 40,
          sectionsSpace: 2,
          sections: [
            for (final e in byCat.entries)
              PieChartSectionData(value: e.value.toDouble(), color: Color(e.key.colorValue), title: '', radius: 22),
          ],
        )),
      ),
    );
  }
}

class _TransactionList extends ConsumerWidget {
  const _TransactionList({required this.list});
  final List<FinTransaction> list;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    if (list.isEmpty) {
      return const Center(child: Text('Nenhum lançamento neste mês'));
    }
    final fmt = DateFormat('dd/MM', 'pt_BR');
    return ListView.builder(
      padding: const EdgeInsets.only(bottom: 88),
      itemCount: list.length,
      itemBuilder: (context, i) {
        final t = list[i];
        final income = t.type == TxType.income;
        return Dismissible(
          key: ValueKey(t.id),
          direction: DismissDirection.endToStart,
          background: Container(
            color: Theme.of(context).colorScheme.errorContainer,
            alignment: Alignment.centerRight,
            padding: const EdgeInsets.only(right: 24),
            child: const Icon(Icons.delete),
          ),
          onDismissed: (_) => ref.read(repositoryProvider).delete(t.id),
          child: ListTile(
            leading: CircleAvatar(
              backgroundColor: Color(t.category.colorValue).withValues(alpha: 0.2),
              child: Icon(iconFor(t.category.icon), color: Color(t.category.colorValue)),
            ),
            title: Text(t.category.name),
            subtitle: Text(t.note.isEmpty ? fmt.format(t.date) : '${fmt.format(t.date)} · ${t.note}'),
            trailing: Text(
              '${income ? '+' : '-'} ${formatCents(t.amountCents)}',
              style: Theme.of(context).textTheme.titleSmall?.copyWith(color: income ? incomeColor : expenseColor),
            ),
          ),
        );
      },
    );
  }
}
