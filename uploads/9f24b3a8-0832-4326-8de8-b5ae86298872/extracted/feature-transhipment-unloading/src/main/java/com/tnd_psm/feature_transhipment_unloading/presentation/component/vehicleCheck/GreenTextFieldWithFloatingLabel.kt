package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings

@Composable
fun GreenBorderFloatingLabelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editTextValue by remember { mutableStateOf("") }
    OutlinedTextField(
        value = editTextValue,
        onValueChange = {
            editTextValue = it
            onValueChange(it)
        },
        label = {
            Text(
                AppStrings.ENTER_SEAL_NO,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
            )
        },
        textStyle = MaterialTheme.typography.labelLarge.copy(color = Color.Black),
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(MaterialTheme.dimens.small),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            unfocusedBorderColor = GreenPrimary,
            cursorColor = GreenPrimary,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray,
        ),
        singleLine = true,
        visualTransformation = if (editTextValue.isEmpty())
            PlaceholderTransformation(" ")
        else VisualTransformation.None,
    )
}


class PlaceholderTransformation(val placeholder: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return PlaceholderFilter(text, placeholder)
    }
}

fun PlaceholderFilter(text: AnnotatedString, placeholder: String): TransformedText {

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return 0
        }

        override fun transformedToOriginal(offset: Int): Int {
            return 0
        }
    }

    return TransformedText(AnnotatedString(placeholder), numberOffsetTranslator)
}
