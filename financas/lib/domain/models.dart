enum TxType { income, expense }

/// Dinheiro sempre em centavos (int), nunca double.
class FinCategory {
  const FinCategory({required this.id, required this.name, required this.icon, required this.colorValue, required this.type});
  final int id;
  final String name;
  final String icon;
  final int colorValue;
  final TxType type;
}

class FinTransaction {
  const FinTransaction({
    required this.id,
    required this.amountCents,
    required this.type,
    required this.date,
    required this.note,
    required this.category,
    required this.accountId,
  });
  final int id;
  final int amountCents;
  final TxType type;
  final DateTime date;
  final String note;
  final FinCategory category;
  final int accountId;
}

class MonthSummary {
  const MonthSummary({required this.incomeCents, required this.expenseCents});
  final int incomeCents;
  final int expenseCents;
  int get balanceCents => incomeCents - expenseCents;

  factory MonthSummary.of(Iterable<FinTransaction> list) {
    var income = 0, expense = 0;
    for (final t in list) {
      if (t.type == TxType.income) {
        income += t.amountCents;
      } else {
        expense += t.amountCents;
      }
    }
    return MonthSummary(incomeCents: income, expenseCents: expense);
  }
}
