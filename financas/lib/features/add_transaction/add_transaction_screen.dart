import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/money.dart';
import '../../data/providers.dart';
import '../../domain/models.dart';

class AddTransactionScreen extends ConsumerStatefulWidget {
  const AddTransactionScreen({super.key});

  @override
  ConsumerState<AddTransactionScreen> createState() => _AddTransactionScreenState();
}

class _AddTransactionScreenState extends ConsumerState<AddTransactionScreen> {
  final _amount = TextEditingController();
  final _note = TextEditingController();
  TxType _type = TxType.expense;
  int? _categoryId;
  String? _error;

  @override
  void dispose() {
    _amount.dispose();
    _note.dispose();
    super.dispose();
  }

  Future<void> _save(List<FinCategory> options) async {
    final cents = parseCents(_amount.text);
    final categoryId = _categoryId ?? options.firstOrNull?.id;
    if (cents == null || cents <= 0 || categoryId == null) {
      setState(() => _error = 'Informe um valor válido');
      return;
    }
    await ref.read(repositoryProvider).add(
          amountCents: cents,
          type: _type,
          date: DateTime.now(),
          note: _note.text.trim(),
          categoryId: categoryId,
        );
    if (mounted) context.pop();
  }

  @override
  Widget build(BuildContext context) {
    final all = ref.watch(categoriesProvider).value ?? const <FinCategory>[];
    final options = all.where((c) => c.type == _type).toList();
    final selected = _categoryId ?? options.firstOrNull?.id;

    return Scaffold(
      appBar: AppBar(title: const Text('Novo lançamento')),
      body: SafeArea(
        child: Center(
          // Limita a largura do formulário em telas grandes.
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 560),
            child: ListView(padding: const EdgeInsets.all(16), children: [
              SegmentedButton<TxType>(
                segments: const [
                  ButtonSegment(value: TxType.expense, label: Text('Despesa')),
                  ButtonSegment(value: TxType.income, label: Text('Receita')),
                ],
                selected: {_type},
                onSelectionChanged: (s) => setState(() {
                  _type = s.first;
                  _categoryId = null;
                }),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: _amount,
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                inputFormatters: [FilteringTextInputFormatter.allow(RegExp(r'[0-9.,]'))],
                decoration: InputDecoration(labelText: 'Valor (R\$)', errorText: _error),
                onChanged: (_) => setState(() => _error = null),
              ),
              const SizedBox(height: 16),
              Text('Categoria', style: Theme.of(context).textTheme.titleSmall),
              const SizedBox(height: 8),
              Wrap(spacing: 8, runSpacing: 8, children: [
                for (final c in options)
                  ChoiceChip(label: Text(c.name), selected: selected == c.id, onSelected: (_) => setState(() => _categoryId = c.id)),
              ]),
              const SizedBox(height: 16),
              TextField(controller: _note, decoration: const InputDecoration(labelText: 'Observação (opcional)')),
              const SizedBox(height: 24),
              FilledButton(onPressed: () => _save(options), child: const Text('Salvar')),
            ]),
          ),
        ),
      ),
    );
  }
}
