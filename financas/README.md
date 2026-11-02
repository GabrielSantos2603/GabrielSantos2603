# Finanças (Android · Kotlin)

App de finanças pessoais **100% offline** (dados só no aparelho, sem login e sem nuvem).

## Stack
- UI: Jetpack Compose + Material 3 (dynamic color, tema escuro, `WindowSizeClass` para celular/tablet)
- Arquitetura: Clean Architecture + MVVM com `StateFlow` (fluxo unidirecional)
- Dados: Room (SQLite), valores em **centavos (`Long`)**
- DI: Hilt · Navegação: Navigation Compose (rotas type-safe) · Assíncrono: Coroutines/Flow
- Gráficos: Vico (já como dependência, ainda não usado)

## Estrutura (pacotes que depois viram módulos Gradle)
```
domain/   modelos, interface do repositório, use cases (Kotlin puro)
data/     Room (entities, DAOs, database) + implementação do repositório
di/       módulos Hilt
ui/       theme, navigation, home, addtransaction
```

## Como rodar
Abra a pasta `financas/` no Android Studio (Ladybug+), aguarde o sync e execute `app`.
Testes: `./gradlew test`.

## Roadmap
1. MVP (este commit): lançamentos, categorias padrão, saldo mensal, layout adaptativo
2. Orçamentos por categoria, metas, recorrências, lembretes (WorkManager)
3. Gráficos (Vico), exportar/importar backup local (CSV/JSON), biometria, widget
