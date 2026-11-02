import 'package:drift/drift.dart';

import '../domain/models.dart';
import 'database.dart';

class FinanceRepository {
  FinanceRepository(this._db);
  final AppDatabase _db;

  Stream<List<FinTransaction>> watchMonth(DateTime month) {
    final start = DateTime(month.year, month.month);
    final end = DateTime(month.year, month.month + 1);
    final q = _db.select(_db.transactions).join([
      innerJoin(_db.categories, _db.categories.id.equalsExp(_db.transactions.categoryId)),
    ])
      ..where(_db.transactions.date.isBiggerOrEqualValue(start) & _db.transactions.date.isSmallerThanValue(end))
      ..orderBy([OrderingTerm.desc(_db.transactions.date), OrderingTerm.desc(_db.transactions.id)]);
    return q.watch().map((rows) => rows.map((r) {
          final t = r.readTable(_db.transactions);
          final c = r.readTable(_db.categories);
          return FinTransaction(
            id: t.id,
            amountCents: t.amountCents,
            type: t.type,
            date: t.date,
            note: t.note,
            accountId: t.accountId,
            category: _category(c),
          );
        }).toList());
  }

  Stream<List<FinCategory>> watchCategories() =>
      (_db.select(_db.categories)..orderBy([(c) => OrderingTerm.asc(c.name)])).watch().map((l) => l.map(_category).toList());

  Future<int> defaultAccountId() async => (await _db.select(_db.accounts).getSingle()).id;

  Future<void> add({
    required int amountCents,
    required TxType type,
    required DateTime date,
    required String note,
    required int categoryId,
  }) async {
    if (amountCents <= 0) throw ArgumentError('O valor deve ser maior que zero');
    await _db.into(_db.transactions).insert(TransactionsCompanion.insert(
          amountCents: amountCents,
          type: type,
          date: date,
          note: Value(note),
          categoryId: categoryId,
          accountId: await defaultAccountId(),
        ));
  }

  Future<void> delete(int id) => (_db.delete(_db.transactions)..where((t) => t.id.equals(id))).go();

  FinCategory _category(Category c) =>
      FinCategory(id: c.id, name: c.name, icon: c.icon, colorValue: c.colorValue, type: c.type);
}
