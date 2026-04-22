package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.CustomSwitch
import com.tnd_psm.core.ui.component.DamageStatusGrid
import com.tnd_psm.core.ui.component.TextWithStar
import com.tnd_psm.core.ui.theme.DividerColor
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.ui.theme.textColor
import com.tnd_psm.core.utils.noRippleClickable

@Composable
fun CaptureVehicleImageCard(
    isContainerDamage: MutableState<Boolean>,
    isCameraClick: MutableState<Boolean>,
) {

    val statusList = listOf(
        "Right" to true,
        "Top" to true,
        "Left" to true,
        "Floor" to true,
        "Front" to false
    )
    Box(
        modifier = Modifier
            .padding(
                start = MaterialTheme.dimens.large2,
                end = MaterialTheme.dimens.large2,
                top = MaterialTheme.dimens.spacingMedium1
            )
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
    ) {
        Column(
            modifier = Modifier.run {
                background(Color.White)
                    .padding(MaterialTheme.dimens.spacingMedium1)
            }
        ) {
            CaptureImage(
                labelText = if (isContainerDamage.value) "Vehicle Container Verification" else "Capture Vehicle Images",
                cameraText = if (isContainerDamage.value) "Photos Verified" else "(Upload 5 images)",
                isCameraTextVisible = true,
                isVerified = isContainerDamage.value, isCameraClick = {
                    isCameraClick.value = true
                }
            )

            HorizontalDivider(
                color = DividerColor,
                thickness = MaterialTheme.dimens.borderThin,
                modifier = Modifier.padding(vertical = MaterialTheme.dimens.small)
            )
            if (isCameraClick.value) {
                DamageStatusGrid(statusList)
                HorizontalDivider(
                    color = DividerColor,
                    thickness = MaterialTheme.dimens.borderThin,
                    modifier = Modifier.padding(vertical = MaterialTheme.dimens.small)
                )
            }

            ContainerDamage(isContainerDamage)
        }
    }
}


@Composable
fun CaptureImage(
    labelText: String,
    cameraText: String,
    isCameraTextVisible: Boolean,
    isVerified: Boolean, isCameraClick: () -> Unit
) {
    Row(

        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextWithStar(text = labelText, modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(if (isVerified) R.drawable.ic_successful else R.drawable.ic_camera),
            contentDescription = "Camera",
            modifier = Modifier
                .size(MaterialTheme.dimens.medium2)
                .noRippleClickable {
                    isCameraClick()
                }
        )
        if (isCameraTextVisible) {
            Spacer(modifier = Modifier.width(MaterialTheme.dimens.extraSmall))
            Text(
                text = cameraText,
                style = MaterialTheme.typography.labelSmall.copy(color = if (isVerified) GreenPrimary else textColor),

                )
        }
    }
}

@Composable
fun ContainerDamage(isContainerDamage: MutableState<Boolean>) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Is Container Damaged?",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        )

        CustomSwitch(
            checked = isContainerDamage.value,
            onCheckedChange = { isContainerDamage.value = it }
        )
    }
}


