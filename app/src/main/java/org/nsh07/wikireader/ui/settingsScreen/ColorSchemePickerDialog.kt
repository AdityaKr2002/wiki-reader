package org.nsh07.wikireader.ui.settingsScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import org.nsh07.wikireader.R
import org.nsh07.wikireader.ui.theme.WikiReaderTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorPickerButton(
    color: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        shapes = IconButtonDefaults.shapes(),
        colors = IconButtonDefaults.iconButtonColors(containerColor = color),
        modifier = modifier.size(48.dp),
        onClick = onClick
    ) {
        AnimatedContent(isSelected) { isSelected ->
            when (isSelected) {
                true -> Icon(Icons.Outlined.Check, tint = Color.Black, contentDescription = null)
                else ->
                    if (color == Color.White) Icon(
                        painterResource(R.drawable.colors),
                        tint = Color.Black,
                        contentDescription = null
                    )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorSchemePickerDialog(
    currentColor: Color,
    modifier: Modifier = Modifier,
    setShowDialog: (Boolean) -> Unit,
    onColorChange: (Color) -> Unit,
) {
    val colorSchemes = listOf(
        Color(0xfffeb4a7), Color(0xffffb3c0), Color(0xfffcaaff), Color(0xffb9c3ff),
        Color(0xff62d3ff), Color(0xff44d9f1), Color(0xff52dbc9), Color(0xff78dd77),
        Color(0xff9fd75c), Color(0xffc1d02d), Color(0xfffabd00), Color(0xffffb86e),
        Color.White
    )

    BasicAlertDialog(
        onDismissRequest = { setShowDialog(false) },
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.chooseColorScheme),
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(16.dp))

                Column(Modifier.align(Alignment.CenterHorizontally)) {
                    (0..11 step 4).forEach {
                        Row {
                            colorSchemes.slice(it..it + 3).fastForEach { color ->
                                ColorPickerButton(
                                    color,
                                    color == currentColor,
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    onColorChange(color)
                                }
                            }
                        }
                    }
                    ColorPickerButton(
                        colorSchemes.last(),
                        colorSchemes.last() == currentColor,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        onColorChange(colorSchemes.last())
                    }
                }

                Spacer(Modifier.height(24.dp))

                TextButton(
                    shapes = ButtonDefaults.shapes(),
                    onClick = { setShowDialog(false) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.ok))
                }
            }
        }
    }
}

@Preview
@Composable
fun ColorPickerDialogPreview() {
    var currentColor by remember { mutableStateOf(Color(0xfffeb4a7)) }
    WikiReaderTheme(darkTheme = true) {
        ColorSchemePickerDialog(
            currentColor,
            setShowDialog = {},
            onColorChange = { currentColor = it }
        )
    }
}
