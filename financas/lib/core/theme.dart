import 'package:flutter/material.dart';

const incomeColor = Color(0xFF16A34A);
const expenseColor = Color(0xFFDC2626);

ThemeData buildTheme(ColorScheme scheme) => ThemeData(
      useMaterial3: true,
      colorScheme: scheme,
      cardTheme: CardThemeData(
        elevation: 0,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      ),
      inputDecorationTheme: InputDecorationTheme(border: OutlineInputBorder(borderRadius: BorderRadius.circular(16))),
    );

ColorScheme fallbackScheme(Brightness b) => ColorScheme.fromSeed(seedColor: const Color(0xFF0F766E), brightness: b);

IconData iconFor(String key) => switch (key) {
      'restaurant' => Icons.restaurant,
      'cart' => Icons.shopping_cart,
      'car' => Icons.directions_car,
      'home' => Icons.home,
      'health' => Icons.favorite,
      'movie' => Icons.movie,
      'school' => Icons.school,
      'payments' => Icons.payments,
      'card' => Icons.add_card,
      _ => Icons.more_horiz,
    };
