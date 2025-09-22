package org.example.project

import androidx.compose.ui.graphics.Color


interface ColorPalette {
    // Graph Colors
    val GraphColorSet1_Light:Color
    val GraphColorSet1_Dark:Color

    val GraphColorSet2_Light:Color
    val GraphColorSet2_Dark :Color

    // Background Colors
    val BackgroundPrimary:Color
    val BackgroundSecondary :Color

    // Text Colors
    val TextPrimary   :Color
    val TextSecondary :Color

    // Slider Colors
    val SliderThumbColor     :Color
    val SliderActiveTrack     :Color
    val SliderInactiveTrack   :Color
}

object AppColors {

    object Light : ColorPalette {
        // Graph Colors
        override val GraphColorSet1_Light = Color(0xFF4FC3F7)
        override val GraphColorSet1_Dark  = Color(0xFF0288D1)

        override val GraphColorSet2_Light = Color(0xFF81C784)
        override val GraphColorSet2_Dark  = Color(0xFF388E3C)

        // Background Colors
        override val BackgroundPrimary   = Color(0xFFDDDDDD)
        override val BackgroundSecondary = Color(0xFFB5B5B5)

        // Text Colors
        override val TextPrimary   = Color(0xFF000000)
        override val TextSecondary = Color(0xFF000000)

        // Slider Colors
        override val SliderThumbColor    = Color(0xFF5D6472)
        override val SliderActiveTrack   = Color(0xFFA0A4AD)
        override val SliderInactiveTrack = Color(0xFFD1D3D8)
    }

    object Dark : ColorPalette {
        // Graph Colors
        override val GraphColorSet1_Light = Color(0xFF4FC3F7)
        override val GraphColorSet1_Dark  = Color(0xFF0288D1)

        override val GraphColorSet2_Light = Color(0xFF81C784)
        override val GraphColorSet2_Dark  = Color(0xFF66BB6A)

        // Background Colors
        override val BackgroundPrimary   = Color(0xFF121212)
        override val BackgroundSecondary = Color(0xFF1E1E1E)

        // Text Colors
        override val TextPrimary   = Color(0xFFB0B0B0)
        override val TextSecondary = Color(0xFFB0C0D0)

        // Slider Colors
        override val SliderThumbColor    = Color(0xFF5D6472)
        override val SliderActiveTrack   = Color(0xFF30343D)
        override val SliderInactiveTrack = Color(0xFF40444D)

//        override val SliderThumbColor    = Color(0xFFB0BEC5)
//        override val SliderActiveTrack   = Color(0xFF90CAF9)
//        override val SliderInactiveTrack = Color(0xFF424282)
    }
}