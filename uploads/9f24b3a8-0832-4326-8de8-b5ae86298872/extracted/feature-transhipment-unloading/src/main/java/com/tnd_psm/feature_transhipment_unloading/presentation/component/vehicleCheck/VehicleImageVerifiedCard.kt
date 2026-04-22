package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.tnd_psm.core.ui.component.DamageStatusGrid
import com.tnd_psm.core.ui.theme.DividerColor
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.CaptureImage

@Composable
fun CaptureVehicleCheckCard(
    countColumn: Int = 2,
    isVehicleVerified: MutableState<Boolean>,
    doesLabelChange: Boolean = true
) {
    val statusList = listOf(
        "Vehicle Cleanliness" to true,
        "Paint Condition" to false,
        "Logo verification" to true
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
            .clickable {
                isVehicleVerified.value = true
            }
    ) {
        Column(
            modifier = Modifier.run {
                background(Color.White)
                    .padding(MaterialTheme.dimens.spacingMedium1)
            }
        ) {
            CaptureImage(
                labelText = if (isVehicleVerified.value && doesLabelChange) "Vehicle Container Verification" else "Capture Vehicle Images",
                cameraText = if (isVehicleVerified.value) "Photos Verified" else "(Upload 5 images)",
                isCameraTextVisible = true,
                isVerified = isVehicleVerified.value, isCameraClick = {

                }
            )

            if (isVehicleVerified.value) {
                HorizontalDivider(
                    color = DividerColor,
                    thickness = MaterialTheme.dimens.borderThin,
                    modifier = Modifier.padding(vertical = MaterialTheme.dimens.small)
                )
                DamageStatusGrid(statusList, columnCount = countColumn)
            }
        }
    }
}
