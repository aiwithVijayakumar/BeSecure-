package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.CompactOutlinedTextField
import com.tnd_psm.core.ui.theme.DividerColor
import com.tnd_psm.core.ui.theme.Graytext
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings

@Composable
fun SealVerificationCard(
    isSealAvailable: Boolean?,
    onSealAvailableChanged: (Boolean) -> Unit,
    sealNumber: String,
    onSealNumberChanged: (String) -> Unit,
    isSealBroken: Boolean?,
    onSealBrokenChanged: (Boolean) -> Unit,
    onUploadImage: () -> Unit
) {
    var isImageCaptured = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                MaterialTheme.dimens.small2,
                shape = RoundedCornerShape(MaterialTheme.dimens.spacingMedium1),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(
                Color.White,
                shape = RoundedCornerShape(MaterialTheme.dimens.spacingMedium1)
            )
            .padding(MaterialTheme.dimens.small2)
    ) {
        Text(
            text = AppStrings.IS_SEAL_AVAILABLE,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )


        HorizontalDivider(
            thickness = MaterialTheme.dimens.borderThin,
            color = DividerColor,
            modifier = Modifier.padding(top = MaterialTheme.dimens.small1)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.offset(x = MaterialTheme.dimens.offsetNegative8)
        ) {
            RadioButton(
                selected = isSealAvailable == true,
                onClick = { onSealAvailableChanged(true) },
                colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
            )
            Text(
                text = AppStrings.YES,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSealAvailable == true) FontWeight.Bold else FontWeight.Normal),
                modifier = Modifier
                    .padding(end = MaterialTheme.dimens.spacingMedium2)
                    .offset(x = MaterialTheme.dimens.offsetNegative3)
            )

            // No Option
            RadioButton(
                selected = isSealAvailable == false,
                onClick = { onSealAvailableChanged(false) },
                colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
            )
            Text(
                text = AppStrings.NO,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSealAvailable == false) FontWeight.Bold else FontWeight.Medium),
                modifier = Modifier.offset(x = MaterialTheme.dimens.offsetNegative3)
            )
        }
        var sealNumberState = remember { mutableStateOf("") }
        if (isSealAvailable == true) {
//            GreenBorderFloatingLabelTextField(
//                value = sealNumber,
//                onValueChange = onSealNumberChanged
//            )
            CompactOutlinedTextField(
                value = sealNumberState.value,
                onValueChange = {
                    onSealNumberChanged(it)
                    sealNumberState.value = it
                },
                label = {
                    Text(
                        AppStrings.ENTER_SEAL_NO,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Graytext,
                            fontWeight = FontWeight.Normal
                        ),
                    )
                },
                placeholder = {
                    Text(
                        "",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors( // Default value
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = GreenPrimary,
                    focusedLabelColor = GreenPrimary,
                    unfocusedLabelColor = GreenPrimary,
                    cursorColor = GreenPrimary,
                    focusedTextColor = GreenPrimary,
                    unfocusedTextColor = GreenPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

        Text(
            text = AppStrings.IS_SEAL_BROKEN,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset(x = MaterialTheme.dimens.offsetNegative8)
        ) {
            // Yes
            RadioButton(
                selected = isSealBroken == true,
                onClick = { onSealBrokenChanged(true) },
                colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
            )
            Text(
                text = AppStrings.YES,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSealBroken == true) FontWeight.Bold else FontWeight.Medium),
                modifier = Modifier
                    .padding(end = MaterialTheme.dimens.spacingMedium2)
                    .offset(x = MaterialTheme.dimens.offsetNegative3)
            )

            // No
            RadioButton(
                selected = isSealBroken == false,
                onClick = { onSealBrokenChanged(false) },
                colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
            )
            Text(
                text = AppStrings.NO,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (isSealBroken == false) FontWeight.Bold else FontWeight.Medium),
                modifier = Modifier.offset(x = MaterialTheme.dimens.offsetNegative3)
            )

            Spacer(modifier = Modifier.weight(1f))
            if (!isImageCaptured.value) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera), // Use your green camera icon
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.spacingMedium8)
                        .clickable {
                            onUploadImage()
                            isImageCaptured.value = true
                        }
                )

                Text(
                    text = AppStrings.UPLOAD_IMAGE,
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.Black),
                    modifier = Modifier
                        .padding(start = MaterialTheme.dimens.extraSmall)
                        .clickable {
                            onUploadImage()
                            isImageCaptured.value = true
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.safexpress_vehicle_img),
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.spacingLarge1)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.small))

                Image(
                    painter = painterResource(id = R.drawable.safexpress_vehicle_img),
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.spacingLarge1)
                )
            }

        }
    }
}
