package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.theme.Graytext
import com.tnd_psm.core.ui.theme.dimens

@Composable
fun ScanCardShort(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Scan to Update Short Package", modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.dimens.spacingMedium1)
            .background(Color.White, shape = RoundedCornerShape(MaterialTheme.dimens.small1))
            .padding(
                horizontal = MaterialTheme.dimens.spacingMedium8,
                vertical = MaterialTheme.dimens.small2
            )
    ) {
        var inputText = remember { mutableStateOf(value) }

        BasicTextField(
            value = inputText.value,
            onValueChange = {
                inputText.value = it
                onValueChange(it)
            },
            singleLine = true,

            textStyle = MaterialTheme.typography.labelLarge.copy(color = Color.Black),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_scan),
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.dimens.spacingMedium8)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))
                    Box {
                        if (inputText.value.isEmpty()) {
                            Text(
                                placeholder,
                                style = MaterialTheme.typography.labelLarge.copy(color = Graytext)
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}



