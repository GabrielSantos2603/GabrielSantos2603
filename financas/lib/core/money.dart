import 'package:intl/intl.dart';

final _brl = NumberFormat.currency(locale: 'pt_BR', symbol: r'R$');

String formatCents(int cents) => _brl.format(cents / 100);

/// Converte "12,50" / "12.50" / "1.250,00" em centavos. Retorna null se inválido.
int? parseCents(String input) {
  var s = input.trim();
  if (s.isEmpty) return null;
  if (s.contains(',')) {
    s = s.replaceAll('.', '').replaceAll(',', '.');
  }
  final m = RegExp(r'^(\d+)(?:\.(\d{1,2}))?$').firstMatch(s);
  if (m == null) return null;
  final reais = int.parse(m.group(1)!);
  final frac = (m.group(2) ?? '').padRight(2, '0');
  return reais * 100 + int.parse(frac.isEmpty ? '0' : frac);
}
