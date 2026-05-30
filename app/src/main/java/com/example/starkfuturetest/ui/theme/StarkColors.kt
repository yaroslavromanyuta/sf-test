package com.example.starkfuturetest.ui.theme

import androidx.compose.ui.graphics.Color

object StarkColors {
    val StarkRed = Color(0xFFE30613)
    val StarkRedDark = Color(0xFF991B1B)
    val WarningAmber = Color(0xFFFBBF24)
    val WarningAmberLight = Color(0xFF92400E)  // amber-800: 5.56:1 on tinted bg (WarningAmber is only 1.46:1)
    val SuccessGreen = Color(0xFF22C55E)
    val SuccessGreenLight = Color(0xFF166534)  // green-800: 6.54:1 on light bg (was #19832A at 4.46:1)
    val ErrorRed = Color(0xFFEF4444)
    val ErrorRedLight = Color(0xFFBA1A1A)

    val BackgroundDark = Color(0xFF0A0A0A)
    val SurfaceDark = Color(0xFF141414)
    val SurfaceContainerDark = Color(0xFF1F1F1F)
    val SurfaceContainerHighDark = Color(0xFF262626)
    val OnSurfaceDark = Color(0xFFFFFFFF)
    val OnSurfaceVariantDark = Color(0xFFA3A3A3)
    val OutlineDark = Color(0xFF525252)
    val OutlineVariantDark = Color(0xFF333333)

    val BackgroundLight = Color(0xFFF5F5F5)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceContainerHighLight = Color(0xFFF0F0F0)
    val OnSurfaceLight = Color(0xFF121414)
    val OnSurfaceVariantLight = Color(0xFF4A4A4A)
    val OutlineLight = Color(0xFFA07470)  // darkened from #AF8782: 3.69:1 vs bg (was 2.91:1, below 3:1 for UI components)
    val OutlineVariantLight = Color(0xFFD1D1D1)
}