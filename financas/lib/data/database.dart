import 'package:drift/drift.dart';
import 'package:drift_flutter/drift_flutter.dart';

import '../domain/models.dart';

part 'database.g.dart';

class Accounts extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get name => text()();
}

class Categories extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get name => text()();
  TextColumn get icon => text()();
  IntColumn get colorValue => integer()();
  IntColumn get type => intEnum<TxType>()();
}

class Transactions extends Table {
  IntColumn get id => integer().autoIncrement()();
  IntColumn get amountCents => integer()();
  IntColumn get type => intEnum<TxType>()();
  DateTimeColumn get date => dateTime()();
  TextColumn get note => text().withDefault(const Constant(''))();
  IntColumn get categoryId => integer().references(Categories, #id)();
  IntColumn get accountId => integer().references(Accounts, #id, onDelete: KeyAction.cascade)();
}

@DriftDatabase(tables: [Accounts, Categories, Transactions])
class AppDatabase extends _$AppDatabase {
  AppDatabase([QueryExecutor? executor]) : super(executor ?? driftDatabase(name: 'financas'));

  @override
  int get schemaVersion => 1;

  @override
  MigrationStrategy get migration => MigrationStrategy(
        onCreate: (m) async {
          await m.createAll();
          await into(accounts).insert(AccountsCompanion.insert(name: 'Carteira'));
          await batch((b) => b.insertAll(categories, _defaults));
        },
        beforeOpen: (details) async => customStatement('PRAGMA foreign_keys = ON'),
      );
}

CategoriesCompanion _c(String name, String icon, int color, TxType type) =>
    CategoriesCompanion.insert(name: name, icon: icon, colorValue: color, type: type);

final _defaults = [
  _c('Alimentação', 'restaurant', 0xFFF59E0B, TxType.expense),
  _c('Mercado', 'cart', 0xFF10B981, TxType.expense),
  _c('Transporte', 'car', 0xFF3B82F6, TxType.expense),
  _c('Moradia', 'home', 0xFF8B5CF6, TxType.expense),
  _c('Saúde', 'health', 0xFFEF4444, TxType.expense),
  _c('Lazer', 'movie', 0xFFEC4899, TxType.expense),
  _c('Educação', 'school', 0xFF06B6D4, TxType.expense),
  _c('Outros', 'more', 0xFF94A3B8, TxType.expense),
  _c('Salário', 'payments', 0xFF22C55E, TxType.income),
  _c('Extras', 'card', 0xFF14B8A6, TxType.income),
];
