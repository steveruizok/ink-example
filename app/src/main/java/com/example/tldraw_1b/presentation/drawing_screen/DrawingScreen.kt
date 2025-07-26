package com.example.tldraw_1b.presentation.drawing_screen

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.geometry.ImmutableBox
import androidx.ink.geometry.ImmutableVec
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import androidx.input.motionprediction.MotionEventPredictor
import com.example.tldraw_1b.presentation.drawing_screen.components.TopToolBar
import com.example.tldraw_1b.ui.theme.InkAppTheme
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class)
@SuppressLint("ClickableViewAccessibility")
@Composable
fun DrawingScreen(
    inProgressStrokesView: InProgressStrokesView,
    finishedStrokesState: MutableState<Set<Stroke>>,
    modifier: Modifier = Modifier
) {
    val canvasStrokeRenderer = CanvasStrokeRenderer.create()

    val currentPointerId = remember { mutableStateOf<Int?>(null) }
    val currentStrokeId = remember { mutableStateOf<InProgressStrokeId?>(null) }

    val selectedBrush = remember { mutableStateOf(pressurePen) }
    val isEraserSelected = remember { mutableStateOf(false) }
    val brushes = listOf(pressurePen, highlighterBrush, markerBrush)

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val rootView = FrameLayout(context)
                inProgressStrokesView.apply {
                    layoutParams =
                        FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        )
                }
                val predictor = MotionEventPredictor.newInstance(rootView)
                val touchListener =
                    View.OnTouchListener { view, event ->
                        predictor.record(event)
                        val predictedEvent = predictor.predict()

                        try {
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    Log.d("DrawingSurface", "ACTION_DOWN")
                                    // First pointer - treat it as inking.
                                    view.requestUnbufferedDispatch(event)
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    currentPointerId.value = pointerId
                                    if (isEraserSelected.value){
                                        eraseStrokes(event, finishedStrokesState)
                                    } else {
                                        currentStrokeId.value =
                                            inProgressStrokesView.startStroke(
                                                event = event,
                                                pointerId = pointerId,
                                                brush = selectedBrush.value
                                            )
                                    }
                                    true
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    Log.d("DrawingSurface", "ACTION_MOVE")
                                    val pointerId = checkNotNull(currentPointerId.value)
                                    val strokeId = checkNotNull(currentStrokeId.value)

                                    for (pointerIndex in 0 until event.pointerCount) {
                                        if (event.getPointerId(pointerIndex) != pointerId) continue
                                        if (isEraserSelected.value) {
                                            eraseStrokes(event, finishedStrokesState)
                                        } else {
                                            inProgressStrokesView.addToStroke(
                                                event,
                                                pointerId,
                                                strokeId,
                                                predictedEvent
                                            )
                                        }
                                    }
                                    true
                                }

                                MotionEvent.ACTION_UP -> {
                                    Log.d("DrawingSurface", "ACTION_UP")
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    check(pointerId == currentPointerId.value)
//                                    val currentStrokeId = checkNotNull(currentStrokeId.value)
                                    if (!isEraserSelected.value) {
                                        val currentStrokeId = checkNotNull(currentStrokeId.value)
                                        inProgressStrokesView.finishStroke(
                                            event,
                                            pointerId,
                                            currentStrokeId
                                        )
                                    }
                                    view.performClick()
                                    true
                                }

                                MotionEvent.ACTION_CANCEL -> {
                                    Log.d("DrawingSurface", "ACTION_CANCEL")
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    check(pointerId == currentPointerId.value)

                                    if (!isEraserSelected.value) {
                                        val currentStrokeId = checkNotNull(currentStrokeId.value)
                                        inProgressStrokesView.cancelStroke(currentStrokeId, event)
                                    }
                                    true
                                }

                                else -> false
                            }
                        } finally {
                            predictedEvent?.recycle()
                        }

                    }
                rootView.setOnTouchListener(touchListener)
                rootView.addView(inProgressStrokesView)
                rootView
            },
        )
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            var canvasTransform = Matrix()
            drawContext.canvas.nativeCanvas.concat(canvasTransform)
            val canvas = drawContext.canvas.nativeCanvas

            finishedStrokesState.value.forEach { stroke ->
                canvasStrokeRenderer.draw(
                    stroke = stroke,
                    canvas = canvas,
                    strokeToScreenTransform = canvasTransform
                )
            }
        }
    }
    TopToolBar(
        brushList = brushes,
        selectedBrush = selectedBrush,
        isEraserSelected = isEraserSelected,
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    )
}

private fun eraseStrokes(event: MotionEvent, finishedStrokesState: MutableState<Set<Stroke>>) {
    Log.d("DrawingScreen", "eraseStrokes")
    val point1 = ImmutableVec(event.x - 10, event.y - 10)
    val point2 = ImmutableVec(event.x + 10, event.y + 10)
    val eraserBox = ImmutableBox.fromTwoPoints(point1, point2)
    val threshold = 0.0f
    val strokesToErase = finishedStrokesState.value.filter { stroke ->
        stroke.shape.computeCoverageIsGreaterThan(
            box = eraserBox,
            coverageThreshold = threshold,
        )
    }
    if (strokesToErase.isNotEmpty()) {
        Snapshot.withMutableSnapshot {
            finishedStrokesState.value -= strokesToErase
        }
    }
}

@Preview
@Composable
private fun DrawingScreenPreview() {
    InkAppTheme {
        DrawingScreen(
            inProgressStrokesView = InProgressStrokesView(LocalContext.current),
            finishedStrokesState = finishedStrokesStatePreview,
            modifier = Modifier
        )
    }
}

internal val finishedStrokesStatePreview = mutableStateOf(emptySet<Stroke>())