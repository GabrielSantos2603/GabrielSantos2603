import 'package:dynamic_color/dynamic_color.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/date_symbol_data_local.dart';

import 'core/theme.dart';
import 'features/add_transaction/add_transaction_screen.dart';
import 'features/home/home_screen.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await initializeDateFormatting('pt_BR');
  runApp(const ProviderScope(child: FinancasApp()));
}

final _router = GoRouter(routes: [
  GoRoute(path: '/', builder: (_, _) => const HomeScreen()),
  GoRoute(path: '/add', builder: (_, _) => const AddTransactionScreen()),
]);

class FinancasApp extends StatelessWidget {
  const FinancasApp({super.key});

  @override
  Widget build(BuildContext context) {
    // Material You (Android 12+) com fallback para a paleta própria.
    return DynamicColorBuilder(builder: _app);
  }

  Widget _app(ColorScheme? light, ColorScheme? dark) {
    return MaterialApp.router(
      title: 'Finanças',
      debugShowCheckedModeBanner: false,
      routerConfig: _router,
      theme: buildTheme(light ?? fallbackScheme(Brightness.light)),
      darkTheme: buildTheme(dark ?? fallbackScheme(Brightness.dark)),
      themeMode: ThemeMode.system,
      locale: const Locale('pt', 'BR'),
      localizationsDelegates: GlobalMaterialLocalizations.delegates,
      supportedLocales: const [Locale('pt', 'BR')],
    );
  }
}
