package com.example.tldraw_1b.presentation.drawing_screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes

// Pressure sensitive brush
val pressurePen = Brush.createWithColorIntArgb(
    family = StockBrushes.pressurePenLatest,
    colorIntArgb = Color.Magenta.toArgb(),
    size = 8F,
    epsilon = 0.1F
)

// Highlighter brush, no pressure sensitivity
val highlighterBrush = Brush.createWithColorIntArgb(
    family = StockBrushes.highlighterLatest,
    colorIntArgb = Color(0x9000BCD4).toArgb(),
    size = 50F,
    epsilon = 0.1F
)

// Circular brush, no pressure sensitivity
val markerBrush = Brush.createWithColorIntArgb(
    family = StockBrushes.markerLatest,
    colorIntArgb = Color.Magenta.toArgb(),
    size = 5F,
    epsilon = 0.1F
)