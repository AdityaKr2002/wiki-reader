package org.nsh07.wikireader.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.launch
import org.nsh07.wikireader.data.WikiPhoto
import org.nsh07.wikireader.data.WikiPhotoDesc
import org.nsh07.wikireader.ui.scaffoldComponents.FullScreenImageTopBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenImage(
    photo: WikiPhoto?,
    photoDesc: WikiPhotoDesc?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { FullScreenImageTopBar(photoDesc = photoDesc, onBack = onBack) },
        containerColor = Color.Black,
        modifier = modifier
    ) { _ ->
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var size by remember { mutableStateOf(IntSize.Zero) }

        // Zoom/Offset logic. Also prevents image from going out of bounds
        val state = rememberTransformableState { scaleChange, offsetChange, _ ->
            val maxX = (size.width * (scale - 1) / 2f)
            val maxY = (size.height * (scale - 1) / 2f)

            scale = (scale * scaleChange).coerceIn(1f..8f)

            offset += offsetChange.times(scale)
            offset = Offset(
                offset.x.coerceIn(-maxX, maxX),
                offset.y.coerceIn(-maxY, maxY)
            )
        }

        val coroutineScope = rememberCoroutineScope()

        if (photo != null && photoDesc != null)
            Box(modifier = Modifier.fillMaxSize()) {
                PageImage(
                    photo = photo,
                    photoDesc = photoDesc,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .onSizeChanged { size = it }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = state)
                        .align(Alignment.Center)
                        .pointerInput(Unit) {
                            detectTapGestures(onDoubleTap = {
                                coroutineScope.launch {
                                    if (scale == 1f) // Zoom in only if the image is zoomed out
                                        state.animateZoomBy(4f)
                                    else
                                        state.animateZoomBy(0.25f)
                                }
                            })
                        }
                )
            }
    }
}