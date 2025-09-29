package com.emdp.rickandmorty.core.ui.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

@Composable
fun GradientText(
    text: String,
    brush: Brush,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = 1
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(brush = brush)) { append(text) }
        },
        style = style,
        modifier = modifier,
        textAlign = textAlign,
        maxLines = maxLines
    )
}