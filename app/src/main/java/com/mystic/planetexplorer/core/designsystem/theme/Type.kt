package com.mystic.planetexplorer.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mystic.planetexplorer.R

private val Lexend = FontFamily(
    Font(resId = R.font.lexend_deca)
)

// Set of Material typography styles to start with
val Typography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = Lexend),
        displayMedium = displayMedium.copy(fontFamily = Lexend),
        displaySmall = displaySmall.copy(fontFamily = Lexend),

        headlineLarge = headlineLarge.copy(fontFamily = Lexend),
        headlineMedium = headlineMedium.copy(fontFamily = Lexend),
        headlineSmall = headlineSmall.copy(fontFamily = Lexend),

        titleLarge = titleLarge.copy(fontFamily = Lexend),
        titleMedium = titleMedium.copy(fontFamily = Lexend),
        titleSmall = titleSmall.copy(fontFamily = Lexend),

        bodyLarge = bodyLarge.copy(fontFamily = Lexend),
        bodyMedium = bodyMedium.copy(fontFamily = Lexend),
        bodySmall = bodySmall.copy(fontFamily = Lexend),

        labelLarge = labelLarge.copy(fontFamily = Lexend),
        labelMedium = labelMedium.copy(fontFamily = Lexend),
        labelSmall = labelSmall.copy(fontFamily = Lexend),
    )
}

//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)