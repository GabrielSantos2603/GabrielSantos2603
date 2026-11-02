# Finanças (Flutter · Android)

App de finanças pessoais **100% offline**: dados só no aparelho, sem login e sem nuvem.

## Stack
| Ponto | Escolha |
|---|---|
| UI | Flutter + Material 3 (Material You via `dynamic_color`, tema escuro) |
| Responsividade | `LayoutBuilder`: 1 coluna no celular, 2 colunas a partir de 840 dp |
| Estado | Riverpod (`Notifier`, `StreamProvider`) |
| Navegação | go_router |
| Banco local | Drift (SQLite), valores em **centavos (`int`)** |
| Gráficos | fl_chart (rosca de despesas por categoria) |
| Formatação | intl (pt_BR / BRL) |

## Estrutura
```
lib/
  domain/    modelos puros (FinTransaction, FinCategory, MonthSummary)
  data/      Drift (tabelas + seed), repositório, providers Riverpod
  core/      tema, formatação de dinheiro
  features/  home, add_transaction
```

## Como rodar
```bash
flutter pub get
dart run build_runner build --delete-conflicting-outputs   # gera database.g.dart
flutter run
flutter test
```
Depois de alterar tabelas em `lib/data/database.dart`, rode o `build_runner` de novo e aumente `schemaVersion`.

## Roadmap
1. MVP (atual): lançamentos, categorias padrão, saldo mensal, gráfico, layout adaptativo
2. Orçamentos por categoria, metas, recorrências, edição de lançamento
3. Backup/restore local (JSON/CSV), biometria, notificações de lembrete, widget
