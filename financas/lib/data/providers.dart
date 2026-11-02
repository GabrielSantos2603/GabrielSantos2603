import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../domain/models.dart';
import 'database.dart';
import 'repository.dart';

final databaseProvider = Provider<AppDatabase>((ref) {
  final db = AppDatabase();
  ref.onDispose(db.close);
  return db;
});

final repositoryProvider = Provider((ref) => FinanceRepository(ref.watch(databaseProvider)));

/// Mês exibido na Home (sempre dia 1).
class SelectedMonth extends Notifier<DateTime> {
  @override
  DateTime build() {
    final now = DateTime.now();
    return DateTime(now.year, now.month);
  }

  void previous() => state = DateTime(state.year, state.month - 1);
  void next() => state = DateTime(state.year, state.month + 1);
}

final selectedMonthProvider = NotifierProvider<SelectedMonth, DateTime>(SelectedMonth.new);

final monthTransactionsProvider = StreamProvider.autoDispose<List<FinTransaction>>(
  (ref) => ref.watch(repositoryProvider).watchMonth(ref.watch(selectedMonthProvider)),
);

final categoriesProvider = StreamProvider.autoDispose<List<FinCategory>>(
  (ref) => ref.watch(repositoryProvider).watchCategories(),
);
