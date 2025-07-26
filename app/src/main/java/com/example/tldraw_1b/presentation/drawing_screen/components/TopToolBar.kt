package com.example.tldraw_1b.presentation.drawing_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.ink.brush.Brush
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.BrushList
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.ColorPicker
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.EraserButton
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.SizeSlider
import com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components.brushListForPreview
import com.example.tldraw_1b.ui.theme.InkAppTheme

@Composable
fun TopToolBar(
    brushList: List<Brush>,
    selectedBrush: MutableState<Brush>,
    isEraserSelected: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ColorPicker(
            selectedBrush = selectedBrush
        )
        Spacer(modifier = Modifier.size(8.dp))
        BrushList(
            brushList = brushList,
            selectedBrush = selectedBrush
        )
        Spacer(modifier = Modifier.size(8.dp))
        EraserButton(
            isEraserSelected = isEraserSelected,
            onEraserSelected = { isSelected ->
                isEraserSelected.value = isSelected
            }
        )
        Spacer(modifier = Modifier.size(8.dp))
        SizeSlider(
            selectedBrush = selectedBrush
        )
    }
}

@Preview
@Composable
private fun TopToolBarPreview() {
    InkAppTheme {
        TopToolBar(
            brushList = brushListForPreview,
            selectedBrush = remember {
                mutableStateOf(brushListForPreview.first())
            },
            isEraserSelected = remember {
                mutableStateOf(false)
            },
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.onPrimary)

        )
    }
}

