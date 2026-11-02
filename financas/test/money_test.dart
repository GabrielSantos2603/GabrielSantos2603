import 'package:financas/core/money.dart';
import 'package:financas/domain/models.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('parseCents', () {
    test('aceita vírgula, ponto e milhar', () {
      expect(parseCents('12,50'), 1250);
      expect(parseCents('12.50'), 1250);
      expect(parseCents('10'), 1000);
      expect(parseCents('1.250,00'), 125000);
      expect(parseCents('5,5'), 550);
    });
    test('rejeita inválido', () {
      expect(parseCents(''), isNull);
      expect(parseCents('abc'), isNull);
      expect(parseCents('1,234'), isNull);
    });
  });

  test('MonthSummary soma receitas e despesas', () {
    const cat = FinCategory(id: 1, name: 'x', icon: 'more', colorValue: 0, type: TxType.expense);
    FinTransaction t(int v, TxType type) =>
        FinTransaction(id: 0, amountCents: v, type: type, date: DateTime(2026, 11), note: '', category: cat, accountId: 1);
    final s = MonthSummary.of([t(10000, TxType.income), t(3000, TxType.expense), t(2000, TxType.expense)]);
    expect(s.incomeCents, 10000);
    expect(s.expenseCents, 5000);
    expect(s.balanceCents, 5000);
  });
}
