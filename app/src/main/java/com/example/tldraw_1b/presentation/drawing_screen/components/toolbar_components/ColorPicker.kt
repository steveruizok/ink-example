package com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.ink.brush.Brush
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.color_picker.ColorSpectrum
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.color_picker.ColorUI
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.color_picker.ColorUIList
import com.example.tldraw_1b.ui.theme.InkAppTheme
import com.example.tldraw_1b.ui.theme.latteBlue
import com.example.tldraw_1b.ui.theme.latteFlamingo
import com.example.tldraw_1b.ui.theme.latteGreen
import com.example.tldraw_1b.ui.theme.latteLavender
import com.example.tldraw_1b.ui.theme.latteMaroon
import com.example.tldraw_1b.ui.theme.latteMauve
import com.example.tldraw_1b.ui.theme.lattePeach
import com.example.tldraw_1b.ui.theme.lattePink
import com.example.tldraw_1b.ui.theme.latteRed
import com.example.tldraw_1b.ui.theme.latteRosewater
import com.example.tldraw_1b.ui.theme.latteSapphire
import com.example.tldraw_1b.ui.theme.latteSky
import com.example.tldraw_1b.ui.theme.latteTeal
import com.example.tldraw_1b.ui.theme.latteYellow
import com.example.tldraw_1b.ui.theme.mochaBlue
import com.example.tldraw_1b.ui.theme.mochaFlamingo
import com.example.tldraw_1b.ui.theme.mochaGreen
import com.example.tldraw_1b.ui.theme.mochaLavender
import com.example.tldraw_1b.ui.theme.mochaMaroon
import com.example.tldraw_1b.ui.theme.mochaMauve
import com.example.tldraw_1b.ui.theme.mochaPeach
import com.example.tldraw_1b.ui.theme.mochaPink
import com.example.tldraw_1b.ui.theme.mochaRed
import com.example.tldraw_1b.ui.theme.mochaRosewater
import com.example.tldraw_1b.ui.theme.mochaSapphire
import com.example.tldraw_1b.ui.theme.mochaSky
import com.example.tldraw_1b.ui.theme.mochaTeal
import com.example.tldraw_1b.ui.theme.mochaYellow

val standardColorList = listOf(
    Color.Black,
    Color.Red,
    Color.Blue,
    Color.Green,
    Color.Magenta,
    Color.Cyan,
    Color.White
)

val mochaColorList = listOf(
    mochaRosewater,
    mochaFlamingo,
    mochaPink,
    mochaMauve,
    mochaRed,
    mochaMaroon,
    mochaPeach,
    mochaYellow,
    mochaGreen,
    mochaTeal,
    mochaBlue,
    mochaSapphire,
    mochaSky,
    mochaLavender
)

val latteColorList = listOf(
    latteRosewater,
    latteFlamingo,
    lattePink,
    latteMauve,
    latteRed,
    latteMaroon,
    lattePeach,
    latteYellow,
    latteGreen,
    latteTeal,
    latteBlue,
    latteSapphire,
    latteSky,
    latteLavender
)

@Composable
fun StandardColorUIList(
    selectedBrush: MutableState<Brush>,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Standard Colors",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .padding(16.dp, 0.dp)
    )
    ColorUIList(
        selectedBrush = selectedBrush,
        colorUIList = standardColorList
    )
}
@Composable
fun MochaColorUIList(
    selectedBrush: MutableState<Brush>,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Catppuccin Mocha Colors",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .padding(16.dp, 0.dp)
    )
    ColorUIList(
        selectedBrush = selectedBrush,
        colorUIList = mochaColorList
    )
}@Composable
fun LatteColorUIList(
    selectedBrush: MutableState<Brush>,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Catppuccin Latte Colors",
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
            .padding(16.dp, 0.dp)
    )
    ColorUIList(
        selectedBrush = selectedBrush,
        colorUIList = latteColorList
    )
}

@Composable
fun ColorPicker(
    selectedBrush: MutableState<Brush>,
    modifier: Modifier = Modifier
) {
    val selectedColor = remember { mutableIntStateOf(selectedBrush.value.colorIntArgb) }
    // Update selectedColor when selectedBrush changes
    LaunchedEffect(selectedBrush.value.colorIntArgb) {
        selectedColor.intValue = selectedBrush.value.colorIntArgb
    }
    val expanded = remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ColorUI(Color(selectedColor.intValue.toULong() shl 32))
        Box(
            modifier = Modifier
        ) {
            IconButton(
                modifier = Modifier
                    .size(24.dp),
                onClick = { expanded.value = true },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    StandardColorUIList(selectedBrush)
                    MochaColorUIList(selectedBrush)
                    LatteColorUIList(selectedBrush)
                    ColorSpectrum()
                }
            }
        }
    }
}

@Preview(widthDp = 100)
@Composable
private fun ColorPickerPreview() {
    InkAppTheme {
        ColorPicker(
            selectedBrush = selectedBrushPreview,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        )
    }
}

internal val selectedBrushPreview = mutableStateOf(brushListForPreview.first())