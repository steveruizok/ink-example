package com.example.tldraw_1b

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
import android.view.View
import android.graphics.Matrix44
import android.graphics.Matrix
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.UiThread
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tldraw_1b.ui.theme.InkAppTheme
import androidx.ink.authoring.InProgressStrokesView
import androidx.ink.authoring.InProgressStrokeId
import androidx.ink.authoring.InProgressStrokesFinishedListener
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import androidx.input.motionprediction.MotionEventPredictor

class MainActivity : ComponentActivity(), InProgressStrokesFinishedListener {
    private lateinit var inProgressStrokesView: InProgressStrokesView
    private lateinit var canvasStrokeRenderer: CanvasStrokeRenderer
    private val finishedStrokesState = mutableStateOf(emptySet<Stroke>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inProgressStrokesView = InProgressStrokesView(this)
        canvasStrokeRenderer = CanvasStrokeRenderer.create()

        setContent {
//            DrawingView(inProgressStrokesView = inProgressStrokesView)
            DrawingSurface(inProgressStrokesView, canvasStrokeRenderer, finishedStrokesState.value)
        }

        inProgressStrokesView.addFinishedStrokesListener(this)
    }

    @UiThread
    override fun onStrokesFinished(strokes: Map<InProgressStrokeId, Stroke>) {
        finishedStrokesState.value += strokes.values
        // Delay the removal to prevent the flash - let the next frame render first
        inProgressStrokesView.post {
            inProgressStrokesView.removeFinishedStrokes(strokes.keys)
        }
    }
}

@Composable
fun DrawingView(
    inProgressStrokesView: InProgressStrokesView,
) {

    Box(modifier = Modifier.fillMaxSize()) {
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
                        // ... (handle touch events)
                        true
                    }

                rootView.setOnTouchListener(touchListener)
                rootView.addView(inProgressStrokesView)
                rootView
            },
        ) {}
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
fun DrawingSurface(
    inProgressStrokesView: InProgressStrokesView,
    canvasStrokeRenderer: CanvasStrokeRenderer,
    finishedStrokesState: Set<Stroke>
) {

    val currentPointerId = remember { mutableStateOf<Int?>(null) }
    val currentStrokeId = remember { mutableStateOf<InProgressStrokeId?>(null) }
    val defaultBrush = Brush.createWithColorIntArgb(
        family = StockBrushes.pressurePenLatest,
        colorIntArgb = Color.Black.toArgb(),
        size = 5F,
        epsilon = 0.1F
    )

    Box(modifier = Modifier.fillMaxSize()) {
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
                                    // First pointer - treat it as inking.
                                    view.requestUnbufferedDispatch(event)
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    currentPointerId.value = pointerId
                                    currentStrokeId.value =
                                        inProgressStrokesView.startStroke(
                                            event = event,
                                            pointerId = pointerId,
                                            brush = defaultBrush
                                        )
                                    true
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val pointerId = checkNotNull(currentPointerId.value)
                                    val strokeId = checkNotNull(currentStrokeId.value)

                                    for (pointerIndex in 0 until event.pointerCount) {
                                        if (event.getPointerId(pointerIndex) != pointerId) continue
                                        inProgressStrokesView.addToStroke(
                                            event,
                                            pointerId,
                                            strokeId,
                                            predictedEvent
                                        )
                                    }
                                    true
                                }

                                MotionEvent.ACTION_UP -> {
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    check(pointerId == currentPointerId.value)
                                    val currentStrokeId = checkNotNull(currentStrokeId.value)
                                    inProgressStrokesView.finishStroke(
                                        event,
                                        pointerId,
                                        currentStrokeId
                                    )
                                    view.performClick()
                                    true
                                }

                                MotionEvent.ACTION_CANCEL -> {
                                    val pointerIndex = event.actionIndex
                                    val pointerId = event.getPointerId(pointerIndex)
                                    check(pointerId == currentPointerId.value)

                                    val currentStrokeId = checkNotNull(currentStrokeId.value)
                                    inProgressStrokesView.cancelStroke(currentStrokeId, event)
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
        ) {

        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasTransform = Matrix()
            drawContext.canvas.nativeCanvas.concat(canvasTransform)
            val canvas = drawContext.canvas.nativeCanvas

            finishedStrokesState.forEach { stroke ->
                canvasStrokeRenderer.draw(canvas = canvas, stroke = stroke, strokeToScreenTransform = canvasTransform)
            }
        }
    }
}