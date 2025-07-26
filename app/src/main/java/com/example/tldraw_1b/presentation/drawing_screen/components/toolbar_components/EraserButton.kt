package com.example.tldraw_1b.presentation.drawing_screen.components.toolbar_components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EraserButton(
    isEraserSelected: MutableState<Boolean>,
    onEraserSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onEraserSelected(!isEraserSelected.value) },
        modifier = modifier
            .size(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Eraser",
            tint = if (isEraserSelected.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}